<template>
  <div>
    <el-form :inline="true" :model="param" class="demo-form-inline">
      <el-form-item label="请选择站点">
        <el-select v-model="param.siteId" placeholder="请选择站点">
          <el-option v-for="site in siteList" :label="site.siteName" :value="site.siteId"
                     :key="site.siteId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="页面别名">
        <el-input v-model="param.pageAliase" placeholder="别名"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="small" @click="queryPage">查询</el-button>
      </el-form-item>
      <router-link class="mui-tab-item" :to="{path:'/cms/page/add/', query: {
        page: this.param.page,
        siteId: this.param.siteId,
        pageAliase: this.param.pageAliase
      }}">
        <el-button type="primary" size="small">新增页面</el-button>
      </router-link>
    </el-form>

    <el-table
      :data="list"
      stripe
      style="width: 100%">
      <el-table-column type="index" width="60">
      </el-table-column>
      <el-table-column prop="pageName" label="页面名称" width="120">
      </el-table-column>
      <el-table-column prop="pageAliase" label="别名" width="120">
      </el-table-column>
      <el-table-column prop="pageType" label="页面类型" width="150">
      </el-table-column>
      <el-table-column prop="pageWebPath" label="访问路径" width="250">
      </el-table-column>
      <el-table-column prop="pagePhysicalPath" label="物理路径" width="250">
      </el-table-column>
      <el-table-column prop="pageCreateTime" label="创建时间" width="180">
      </el-table-column>
      <el-table-column label="操作" width="80">
        <!-- 插槽,代表当前行记录 -->
        <template slot-scope="page">
          <el-button
            size="small" type="text"
            @click="edit(page.row.pageId)">编辑
          </el-button>
          <el-button
            size="small" type="text"
            @click="del(page.row.pageId)">删除
          </el-button>
          <el-button
            size="small" type="text"
            @click="preview(page.row.pageId)">页面预览
          </el-button>
          <el-button
            size="small" type="text"
            @click="postPage(page.row.pageId)">页面发布
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      layout="prev, pager, next"
      :total="total"
      :page-size="param.size"
      :current-page="param.page"
      v-on:current-change="changePage"
      style="float: right">
    </el-pagination>
  </div>
</template>

<script>
  // 引入cms定义的所有方法
  import * as cmsApi from '../api/cms'
  // vue对象
  export default {
    name: "page_list",
    // 设置参数
    data() {
      return {
        list: [],
        total: 50,
        param: {
          pageAliase: '',
          siteId: '',
          page: 1,
          size: 10
        },
        siteList: [
          {
            siteId: '5a751fab6abb5044e0d19ea1',
            siteName: '门户主站'
          },
          {
            siteId: 'test001',
            siteName: '测试站'
          }
        ]
      }
    },
    // 配置方法
    methods: {
      queryPage: function () {
        cmsApi.page_list(this.param.page, this.param.size, this.param).then((result) => {
          // 将结果打印在控制台日志中
          console.log(result);
          this.list = result.queryResult.list;
          this.total = result.queryResult.total;
        });
      },
      changePage: function (page) {
        this.param.page = page;
        this.queryPage();
      },
      // 点击编辑按键进行跳转,传入当前行数据的id
      edit: function (pageId) {
        this.$router.push({
          path: '/cms/page/edit/' + pageId,
          query: {
            // pageId: pageId,
            page: this.param.page,
            siteId: this.param.siteId
          }
        })
      },
      del: function (id) {
        this.$confirm('确认删除吗？', '提示', {}).then(() => {// 确认框
          cmsApi.page_del(id).then(result => {
            if (result.success) {// 提示框
              this.$message({
                message: '删除成功',
                type: 'success'
              });
              this.queryPage();
            } else {
              this.$message.error(result.message);
            }
          })
        })
      },
      preview: function (pageId) {
        // 打开新窗口,发送请求
        window.open("http://www.xuecheng.com/cms/preview/"+pageId);
      },
      postPage: function (pageId) {
        this.$confirm('确认发布吗？', '提示', {}).then(() => {// 确认框
          cmsApi.postPage(pageId).then(result => {
            if (result.success) {// 提示框
              this.$message({
                message: '发布成功',
                type: 'success'
              });
              this.queryPage();
            } else {
              this.$message.error(result.message);
            }
          })
        })
      }
    },
    // 定义钩子方法
    mounted() {
      // this.queryPage();
      // 从当前路由地址中获取参数,将从地址栏中获取的参数从字符串类型转换回int类型,如果获取的参数为null,则将1赋值给page
      this.param.page = Number.parseInt(this.$route.query.page || 1);
      // 若从路由地址中获取的参数为null,则将空字符串赋值给siteid
      this.param.siteId = this.$route.query.siteId || '';
      this.param.pageAliase = this.$route.query.pageAliase || '';
      // 查询
      this.queryPage();
    }
  }
</script>

<style scoped>

</style>
