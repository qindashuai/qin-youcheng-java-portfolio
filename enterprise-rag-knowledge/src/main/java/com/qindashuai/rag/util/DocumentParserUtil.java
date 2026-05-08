package com.qindashuai.rag.util;

import com.qindashuai.rag.common.BusinessException;
import com.qindashuai.rag.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DocumentParserUtil {

    public static String parse(Path filePath, String fileType) {
        try {
            switch (fileType.toUpperCase()) {
                case "PDF":
                    return parsePdf(filePath);
                case "DOCX":
                case "DOC":
                    return parseDocx(filePath);
                case "TXT":
                    return parseTxt(filePath);
                default:
                    throw new BusinessException(ResultCode.DOCUMENT_TYPE_NOT_SUPPORT,
                            "不支持的文档类型: " + fileType);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("文档解析失败: {}", filePath, e);
            throw new BusinessException(ResultCode.DOCUMENT_PARSE_ERROR,
                    "文档解析失败: " + e.getMessage());
        }
    }

    private static String parsePdf(Path filePath) throws IOException {
        try (PDDocument document = PDDocument.load(filePath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }

    private static String parseDocx(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             XWPFDocument document = new XWPFDocument(is)) {
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            return paragraphs.stream()
                    .map(XWPFParagraph::getText)
                    .filter(text -> !text.trim().isEmpty())
                    .collect(Collectors.joining("\n"));
        }
    }

    private static String parseTxt(Path filePath) throws IOException {
        return new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
    }
}
