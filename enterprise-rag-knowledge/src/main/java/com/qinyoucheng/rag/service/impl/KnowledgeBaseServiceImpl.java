package com.qinyoucheng.rag.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qinyoucheng.rag.common.BusinessException;
import com.qinyoucheng.rag.common.PageResult;
import com.qinyoucheng.rag.common.ResultCode;
import com.qinyoucheng.rag.entity.KnowledgeBase;
import com.qinyoucheng.rag.mapper.KnowledgeBaseMapper;
import com.qinyoucheng.rag.service.KnowledgeBaseService;
import com.qinyoucheng.rag.vo.KnowledgeBaseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    private final KnowledgeBaseMapper knowledgeBaseMapper;

    public KnowledgeBaseServiceImpl(KnowledgeBaseMapper knowledgeBaseMapper) {
        this.knowledgeBaseMapper = knowledgeBaseMapper;
    }

    @Override
    public Long createKnowledgeBase(KnowledgeBase knowledgeBase) {
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBase::getName, knowledgeBase.getName());
        if (knowledgeBaseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.KNOWLEDGE_BASE_EXISTS, "知识库名称已存在");
        }

        knowledgeBase.setDocumentCount(0);
        knowledgeBase.setStatus(1);
        knowledgeBase.setCreateTime(LocalDateTime.now());
        knowledgeBase.setUpdateTime(LocalDateTime.now());
        knowledgeBaseMapper.insert(knowledgeBase);

        log.info("知识库创建成功: id={}, name={}", knowledgeBase.getId(), knowledgeBase.getName());
        return knowledgeBase.getId();
    }

    @Override
    public KnowledgeBaseVO getKnowledgeBase(Long id) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(id);
        if (kb == null) {
            throw new BusinessException(ResultCode.KNOWLEDGE_BASE_NOT_FOUND);
        }
        return convertToVO(kb);
    }

    @Override
    public PageResult<KnowledgeBaseVO> listKnowledgeBases(String category, int pageNum, int pageSize) {
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            wrapper.eq(KnowledgeBase::getCategory, category);
        }
        wrapper.orderByDesc(KnowledgeBase::getCreateTime);

        Page<KnowledgeBase> page = knowledgeBaseMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);

        List<KnowledgeBaseVO> vos = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), pageNum, pageSize, vos);
    }

    @Override
    public void updateKnowledgeBase(KnowledgeBase knowledgeBase) {
        KnowledgeBase existing = knowledgeBaseMapper.selectById(knowledgeBase.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.KNOWLEDGE_BASE_NOT_FOUND);
        }
        knowledgeBase.setUpdateTime(LocalDateTime.now());
        knowledgeBaseMapper.updateById(knowledgeBase);
    }

    @Override
    public void deleteKnowledgeBase(Long id) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(id);
        if (kb == null) {
            throw new BusinessException(ResultCode.KNOWLEDGE_BASE_NOT_FOUND);
        }
        knowledgeBaseMapper.deleteById(id);
        log.info("知识库删除成功: id={}", id);
    }

    private KnowledgeBaseVO convertToVO(KnowledgeBase kb) {
        KnowledgeBaseVO vo = new KnowledgeBaseVO();
        BeanUtils.copyProperties(kb, vo);
        return vo;
    }
}
