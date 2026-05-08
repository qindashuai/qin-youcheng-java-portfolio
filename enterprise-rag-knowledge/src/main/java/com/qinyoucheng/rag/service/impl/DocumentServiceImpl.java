package com.qinyoucheng.rag.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qinyoucheng.rag.common.BusinessException;
import com.qinyoucheng.rag.common.PageResult;
import com.qinyoucheng.rag.common.ResultCode;
import com.qinyoucheng.rag.dto.DocumentUploadDTO;
import com.qinyoucheng.rag.entity.KnowledgeBase;
import com.qinyoucheng.rag.entity.KnowledgeDocument;
import com.qinyoucheng.rag.mapper.KnowledgeBaseMapper;
import com.qinyoucheng.rag.mapper.KnowledgeDocumentMapper;
import com.qinyoucheng.rag.service.DocumentService;
import com.qinyoucheng.rag.util.DocumentParserUtil;
import com.qinyoucheng.rag.vo.DocumentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    private final KnowledgeDocumentMapper documentMapper;
    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final ChunkServiceImpl chunkService;
    private final VectorServiceImpl vectorService;

    @Value("${rag.upload.path:./uploads}")
    private String uploadPath;

    @Value("${rag.upload.allowed-types:pdf,docx,doc,txt}")
    private String allowedTypes;

    public DocumentServiceImpl(KnowledgeDocumentMapper documentMapper,
                               KnowledgeBaseMapper knowledgeBaseMapper,
                               ChunkServiceImpl chunkService,
                               VectorServiceImpl vectorService) {
        this.documentMapper = documentMapper;
        this.knowledgeBaseMapper = knowledgeBaseMapper;
        this.chunkService = chunkService;
        this.vectorService = vectorService;
    }

    @Override
    public Long uploadDocument(MultipartFile file, DocumentUploadDTO dto) {
        validateFile(file);

        KnowledgeBase kb = knowledgeBaseMapper.selectById(dto.getKnowledgeBaseId());
        if (kb == null) {
            throw new BusinessException(ResultCode.KNOWLEDGE_BASE_NOT_FOUND);
        }

        String originalFilename = file.getOriginalFilename();
        String fileType = DocumentParserUtil.getFileExtension(originalFilename);
        Path filePath = saveFile(file);

        KnowledgeDocument document = new KnowledgeDocument();
        document.setKnowledgeBaseId(dto.getKnowledgeBaseId());
        document.setTitle(dto.getTitle() != null ? dto.getTitle() : originalFilename);
        document.setFileName(originalFilename);
        document.setFilePath(filePath.toString());
        document.setFileType(fileType);
        document.setFileSize(file.getSize());
        document.setChunkStrategy(dto.getChunkStrategy());
        document.setChunkSize(dto.getChunkSize());
        document.setChunkOverlap(dto.getChunkOverlap());
        document.setParseStatus(0);
        document.setVectorStatus(0);
        document.setChunkCount(0);
        document.setCreateTime(LocalDateTime.now());
        document.setUpdateTime(LocalDateTime.now());
        documentMapper.insert(document);

        kb.setDocumentCount(kb.getDocumentCount() + 1);
        knowledgeBaseMapper.updateById(kb);

        processDocumentAsync(document.getId());

        return document.getId();
    }

    @Async
    public void processDocumentAsync(Long documentId) {
        processDocument(documentId);
    }

    @Override
    public void processDocument(Long documentId) {
        KnowledgeDocument document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_FOUND);
        }

        try {
            document.setParseStatus(1);
            documentMapper.updateById(document);

            String content = DocumentParserUtil.parse(
                    Paths.get(document.getFilePath()), document.getFileType());

            document.setParseStatus(2);
            documentMapper.updateById(document);

            chunkService.splitAndSave(documentId, content,
                    document.getChunkStrategy(),
                    document.getChunkSize(),
                    document.getChunkOverlap());

            vectorService.embedChunksByDocumentId(documentId);

            document.setVectorStatus(2);
            documentMapper.updateById(document);

            log.info("文档处理完成: documentId={}", documentId);
        } catch (Exception e) {
            log.error("文档处理失败: documentId={}", documentId, e);
            document.setParseStatus(3);
            document.setVectorStatus(3);
            documentMapper.updateById(document);
        }
    }

    @Override
    public DocumentVO getDocument(Long id) {
        KnowledgeDocument document = documentMapper.selectById(id);
        if (document == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_FOUND);
        }
        return convertToVO(document);
    }

    @Override
    public PageResult<DocumentVO> listDocuments(Long knowledgeBaseId, int pageNum, int pageSize) {
        LambdaQueryWrapper<KnowledgeDocument> wrapper = new LambdaQueryWrapper<>();
        if (knowledgeBaseId != null) {
            wrapper.eq(KnowledgeDocument::getKnowledgeBaseId, knowledgeBaseId);
        }
        wrapper.orderByDesc(KnowledgeDocument::getCreateTime);

        Page<KnowledgeDocument> page = documentMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);

        List<DocumentVO> vos = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), pageNum, pageSize, vos);
    }

    @Override
    public void deleteDocument(Long id) {
        KnowledgeDocument document = documentMapper.selectById(id);
        if (document == null) {
            throw new BusinessException(ResultCode.DOCUMENT_NOT_FOUND);
        }
        documentMapper.deleteById(id);

        KnowledgeBase kb = knowledgeBaseMapper.selectById(document.getKnowledgeBaseId());
        if (kb != null && kb.getDocumentCount() > 0) {
            kb.setDocumentCount(kb.getDocumentCount() - 1);
            knowledgeBaseMapper.updateById(kb);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.DOCUMENT_UPLOAD_ERROR, "上传文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException(ResultCode.DOCUMENT_UPLOAD_ERROR, "文件名不能为空");
        }
        String ext = DocumentParserUtil.getFileExtension(originalFilename).toLowerCase();
        List<String> allowed = Arrays.asList(allowedTypes.split(","));
        if (!allowed.contains(ext)) {
            throw new BusinessException(ResultCode.DOCUMENT_TYPE_NOT_SUPPORT,
                    "不支持的文件类型: " + ext + "，允许类型: " + allowedTypes);
        }
    }

    private Path saveFile(MultipartFile file) {
        try {
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            Path dir = Paths.get(uploadPath, dateDir);
            Files.createDirectories(dir);

            String originalFilename = file.getOriginalFilename();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String newFilename = timestamp + "_" + originalFilename;
            Path filePath = dir.resolve(newFilename);

            file.transferTo(filePath.toFile());
            return filePath;
        } catch (IOException e) {
            log.error("文件保存失败", e);
            throw new BusinessException(ResultCode.DOCUMENT_UPLOAD_ERROR, "文件保存失败: " + e.getMessage());
        }
    }

    private DocumentVO convertToVO(KnowledgeDocument document) {
        DocumentVO vo = new DocumentVO();
        BeanUtils.copyProperties(document, vo);
        return vo;
    }
}
