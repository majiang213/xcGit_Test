package com.xuecheng.manage_cms.web.controller;

import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.CmsPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class CmsPreviewController extends BaseController {

    @Autowired
    private CmsPageService cmsPageService;

    @GetMapping("/cms/preview/{pageId}")
    public void preview(@PathVariable("pageId") String pageId){
        try {
            String pageHtml = cmsPageService.getPageHtml(pageId);
            if (pageHtml == null){
                ExceptionCast.cast(CmsCode.CMS_COURSE_PERVIEWISNULL);
            }
            // 通过父类的response对象获得字符输出流
            PrintWriter writer = response.getWriter();
            writer.write(pageHtml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
