package com.xuecheng.manage_cms.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 配置类
public class MongoDBConfig {

    @Value("${spring.data.mongodb.database}")
    private String db;

    @Bean
    public GridFSBucket getGridFsBucket(MongoClient mongoClient){// 从IoC容器汇总获取MongoClient对象
        // 通过mongo客户端对象传入当前连接数据库名获取数据库对象
        MongoDatabase database = mongoClient.getDatabase(db);
        // 通过GridFsBuckets的静态create方法传入数据库对象获取用于打开下载流的gridFsBucket对象
        GridFSBucket gridFSBucket = GridFSBuckets.create(database);
        return gridFSBucket;
    }
}
