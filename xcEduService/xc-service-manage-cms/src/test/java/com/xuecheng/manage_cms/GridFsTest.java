package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;

@SpringBootTest(classes = ManageCmsApplication.class)
@RunWith(SpringRunner.class)
public class GridFsTest {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    /**
     * 向GridFS中储存文件
     * @throws FileNotFoundException
     */
    @Test
    public void saveFileToGridFs() throws FileNotFoundException {
        // 获取文件输入流
        InputStream inputStream = new FileInputStream(new File("D:/index_banner.ftl"));
        // 调用store方法传入文件输入流,文件名称,内容描述(?),将文件存储进GridFs中,并返回该文件的id
        ObjectId objectId = gridFsTemplate.store(inputStream, "轮播图模板测试文件001");
        System.out.println(objectId);
    }

    /**
     * 从GridFs中下载文件
     * @throws IOException
     */
    @Test
    public void downLoadFileByGridFs() throws IOException {
        // 根据id查询文件,获得信息对象
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("5c25cb1a8bf7f72180a01f39")));
        // 打开文件下载流,传入文件的id,返回文件下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        // 创建GridFsResource对象,构造函数传入文件对象和下载流对象,获得文件源
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        // 获得文件源中的文件数据,并将流
        String s = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        System.out.println(s);
    }

    /**
     * 从GridFs中删除文件
     */
    @Test
    public void deleteFile(){
        //根据文件id删除fs.files和fs.chunks中的记录
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is("5c25b52b8bf7f705808b7eb6")));
    }
}
