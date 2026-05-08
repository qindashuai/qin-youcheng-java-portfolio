package com.qinyoucheng.rag.strategy;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SemanticChunkStrategy implements ChunkStrategy {

    private static final List<String> SECTION_MARKERS = Arrays.asList(
            "\n第", "\n一、", "\n二、", "\n三、", "\n四、", "\n五、",
            "\n1.", "\n2.", "\n3.", "\n4.", "\n5.",
            "\n1、", "\n2、", "\n3、", "\n4、", "\n5、",
            "\n##", "\n###", "\n####",
            "\n摘要", "\n前言", "\n引言", "\n背景",
            "\n总结", "\n结论", "\n附录",
            "\r\n第", "\r\n一、", "\r\n二、"
    );

    @Override
    public List<String> chunk(String content, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        if (content == null || content.isEmpty()) {
            return chunks;
        }

        List<Integer> splitPoints = findSemanticSplitPoints(content);

        int start = 0;
        for (int i = 0; i < splitPoints.size(); i++) {
            int end = splitPoints.get(i);
            String chunk = content.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                if (chunk.length() > chunkSize) {
                    chunks.addAll(splitLongChunk(chunk, chunkSize, overlap));
                } else {
                    chunks.add(chunk);
                }
            }
            start = end;
        }

        if (start < content.length()) {
            String chunk = content.substring(start).trim();
            if (!chunk.isEmpty()) {
                if (chunk.length() > chunkSize) {
                    chunks.addAll(splitLongChunk(chunk, chunkSize, overlap));
                } else {
                    chunks.add(chunk);
                }
            }
        }

        return chunks;
    }

    private List<Integer> findSemanticSplitPoints(String content) {
        List<Integer> points = new ArrayList<>();
        for (String marker : SECTION_MARKERS) {
            int index = content.indexOf(marker);
            while (index != -1) {
                if (!points.contains(index)) {
                    points.add(index);
                }
                index = content.indexOf(marker, index + 1);
            }
        }
        points.sort(Integer::compareTo);
        return points;
    }

    private List<String> splitLongChunk(String chunk, int chunkSize, int overlap) {
        List<String> result = new ArrayList<>();
        int start = 0;
        while (start < chunk.length()) {
            int end = Math.min(start + chunkSize, chunk.length());
            String sub = chunk.substring(start, end).trim();
            if (!sub.isEmpty()) {
                result.add(sub);
            }
            start = end - overlap;
            if (start <= 0 || start >= chunk.length()) {
                start = end;
            }
        }
        return result;
    }

    @Override
    public String getStrategyName() {
        return "SEMANTIC";
    }
}
