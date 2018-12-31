import Home from '@/module/home/page/home.vue';// 引入基本页
import page_list from '@/module/cms/page/page_list.vue';// 引入当前模块页面 @代表src目录
import page_add from '@/module/cms/page/page_add.vue';// 引入页面模块
import page_edit from '@/module/cms/page/page_edit.vue';

export default [{
  path: '/cms',
  component: Home,
  name: 'CMS内容管理', // 菜单名称
  hidden: false,
  children: [
    // 配置映射路径,        选项名称,       模板页面,             是否隐藏
    {path: '/cms/page/list', name: '页面列表', component: page_list, hidden: false},
    {path: '/cms/page/add', name: '新增页面', component: page_add, hidden: true},
    {path: '/cms/page/edit/:pageId', name: '修改页面', component: page_edit, hidden: true}
  ]
}]
