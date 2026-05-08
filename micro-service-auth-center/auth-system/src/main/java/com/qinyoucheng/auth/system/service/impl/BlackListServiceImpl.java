package com.qinyoucheng.auth.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qinyoucheng.auth.common.PageResult;
import com.qinyoucheng.auth.common.RedisUtil;
import com.qinyoucheng.auth.system.entity.SysBlackList;
import com.qinyoucheng.auth.system.mapper.SysBlackListMapper;
import com.qinyoucheng.auth.system.service.BlackListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class BlackListServiceImpl implements BlackListService {

    @Resource
    private SysBlackListMapper blackListMapper;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public PageResult<SysBlackList> listBlackList(Integer targetType, String appKey, Integer pageNum, Integer pageSize) {
        Page<SysBlackList> page = new Page<>(pageNum, pageSize);
        IPage<SysBlackList> result = blackListMapper.selectPageWithCondition(page, targetType, appKey);
        return PageResult.of(result.getTotal(), pageNum, pageSize, result.getRecords());
    }

    @Override
    public SysBlackList addToBlackList(SysBlackList blackList) {
        blackListMapper.insert(blackList);

        if (blackList.getTargetType() == 1 && blackList.getTargetValue() != null) {
            long expiration = blackList.getExpireTime() != null
                    ? blackList.getExpireTime() - System.currentTimeMillis()
                    : TimeUnit.DAYS.toMillis(365);
            if (expiration > 0) {
                redisUtil.addToIpBlacklist(blackList.getTargetValue(), expiration);
            }
        }

        log.info("添加黑名单: targetType={}, targetValue={}", blackList.getTargetType(), blackList.getTargetValue());
        return blackList;
    }

    @Override
    public void removeFromBlackList(Long id) {
        SysBlackList blackList = blackListMapper.selectById(id);
        if (blackList != null) {
            blackListMapper.deleteById(id);
            if (blackList.getTargetType() == 1) {
                redisUtil.removeFromIpBlacklist(blackList.getTargetValue());
            }
            log.info("移除黑名单: id={}, targetValue={}", id, blackList.getTargetValue());
        }
    }

    @Override
    public boolean isIpBlacklisted(String ip) {
        return redisUtil.isIpBlacklisted(ip);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return redisUtil.isTokenBlacklisted(token);
    }
}
