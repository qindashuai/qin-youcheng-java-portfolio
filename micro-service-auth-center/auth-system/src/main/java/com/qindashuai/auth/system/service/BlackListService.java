package com.qindashuai.auth.system.service;

import com.qindashuai.auth.common.PageResult;
import com.qindashuai.auth.system.entity.SysBlackList;

public interface BlackListService {

    PageResult<SysBlackList> listBlackList(Integer targetType, String appKey, Integer pageNum, Integer pageSize);

    SysBlackList addToBlackList(SysBlackList blackList);

    void removeFromBlackList(Long id);

    boolean isIpBlacklisted(String ip);

    boolean isTokenBlacklisted(String token);
}
