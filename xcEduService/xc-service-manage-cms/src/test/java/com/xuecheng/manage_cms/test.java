package com.xuecheng.manage_cms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootTest(classes = ManageCmsApplication.class)
@RunWith(SpringRunner.class)
public class test {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testGetModel(){
        ResponseEntity<Map> entity =            //http://localhost:31001/cms/config/getModel/5c25ac85e065e83a8cde31c7
                restTemplate.getForEntity("http://localhost:31001/cms/config/getModel/5a791725dd573c3574ee333f", Map.class);

        Map body = entity.getBody();
        System.out.println(body);
    }


}
