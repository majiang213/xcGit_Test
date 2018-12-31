package com.xuecheng.manage_cms.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/test")
public class TestFreemarker {

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/freemarker")
    public String testFreemarker(Map<String,Object> map){
        ResponseEntity<Map> entity = restTemplate.getForEntity
                ("http://localhost:31001/cms/config/getModel/5c25ac85e065e83a8cde31c7",
                Map.class);

        map.putAll(Objects.requireNonNull(entity.getBody()));

        return "index_banner";
    }
}
