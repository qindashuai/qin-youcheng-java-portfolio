package com.qinyoucheng.rag.controller;

import com.qinyoucheng.rag.common.PageResult;
import com.qinyoucheng.rag.common.Result;
import com.qinyoucheng.rag.common.ResultCode;
import com.qinyoucheng.rag.dto.DocumentUploadDTO;
import com.qinyoucheng.rag.service.DocumentService;
import com.qinyoucheng.rag.vo.DocumentVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public Result<Long> uploadDocument(@RequestParam("file") MultipartFile file,
                                        @Valid DocumentUploadDTO dto) {
        if (file == null || file.isEmpty()) {
            return Result.fail(ResultCode.DOCUMENT_UPLOAD_ERROR, "上传文件不能为空");
        }
        Long documentId = documentService.uploadDocument(file, dto);
        return Result.success(documentId);
    }

    @GetMapping("/{id}")
    public Result<DocumentVO> getDocument(@PathVariable Long id) {
        DocumentVO vo = documentService.getDocument(id);
        return Result.success(vo);
    }

    @GetMapping("/list")
    public Result<PageResult<DocumentVO>> listDocuments(
            @RequestParam(required = false) Long knowledgeBaseId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<DocumentVO> result = documentService.listDocuments(knowledgeBaseId, pageNum, pageSize);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return Result.success();
    }

    @PostMapping("/{id}/reprocess")
    public Result<Void> reprocessDocument(@PathVariable Long id) {
        documentService.processDocument(id);
        return Result.success();
    }
}
