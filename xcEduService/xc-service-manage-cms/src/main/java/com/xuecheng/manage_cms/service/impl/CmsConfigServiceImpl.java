package com.xuecheng.manage_cms.service.impl;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.service.CmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CmsConfigServiceImpl implements CmsConfigService {

    @Autowired
    private CmsConfigRepository cmsConfigRepository;

    /**
     * 通过id查询CMS数据配置模型对象
     * @param id
     * @return
     */
    @Override
    public CmsConfig getModel(String id) {
        Optional<CmsConfig> cmsConfigOptional = cmsConfigRepository.findById(id);
        if (cmsConfigOptional.isPresent()){
            return cmsConfigOptional.get();
        }
        ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        return null;
    }
}
