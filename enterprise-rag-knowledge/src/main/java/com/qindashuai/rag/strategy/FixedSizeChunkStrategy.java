package com.qindashuai.rag.strategy;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FixedSizeChunkStrategy implements ChunkStrategy {

    @Override
    public List<String> chunk(String content, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        if (content == null || content.isEmpty()) {
            return chunks;
        }

        int contentLength = content.length();
        if (contentLength <= chunkSize) {
            chunks.add(content.trim());
            return chunks;
        }

        int start = 0;
        while (start < contentLength) {
            int end = Math.min(start + chunkSize, contentLength);

            if (end < contentLength) {
                int lastPeriod = content.lastIndexOf('。', end);
                int lastNewline = content.lastIndexOf('\n', end);
                int lastSpace = content.lastIndexOf(' ', end);
                int breakPoint = Math.max(Math.max(lastPeriod, lastNewline), lastSpace);

                if (breakPoint > start && breakPoint < end) {
                    end = breakPoint + 1;
                }
            }

            String chunk = content.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            start = end - overlap;
            if (start <= 0) {
                start = end;
            }
            if (start >= contentLength) {
                break;
            }
        }

        return chunks;
    }

    @Override
    public String getStrategyName() {
        return "FIXED_SIZE";
    }
}
