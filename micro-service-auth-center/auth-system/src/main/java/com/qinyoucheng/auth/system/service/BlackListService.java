package com.qinyoucheng.auth.system.service;

import com.qinyoucheng.auth.common.PageResult;
import com.qinyoucheng.auth.system.entity.SysBlackList;

public interface BlackListService {

    PageResult<SysBlackList> listBlackList(Integer targetType, String appKey, Integer pageNum, Integer pageSize);

    SysBlackList addToBlackList(SysBlackList blackList);

    void removeFromBlackList(Long id);

    boolean isIpBlacklisted(String ip);

    boolean isTokenBlacklisted(String token);
}
