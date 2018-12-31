import http from './../../../base/api/public'
import querystring from 'querystring'

let sysConfig = require('@/../config/sysConfig');
let apiUrl = sysConfig.xcApiUrlPre;

export const page_list = (page, size, param) => {
  //  将前台的json对象转换为键值对的字符串作为get请求参数拼接在url后面传入后台
  let stringparam = querystring.stringify(param);

  // apiUrl为跨域请求解决方案请求路径为apiUrl开头时则最自动访问proxyTable代理中间件
  return http.requestQuickGet(apiUrl + "/cms/list/" + page + "/" + size + "?" + stringparam);
};

export const page_add = pageForm => {
  return http.requestPost(apiUrl + "/cms/add", pageForm);
};

export const page_findOneById = id => {
  return http.requestGet(apiUrl + "/cms/get/"+id);
};

export const page_edit = (id,paramForm) => {
  return http.requestPut(apiUrl + "/cms/update/"+id,paramForm);
};

export const page_del = (id) => {
  return http.requestDelete(apiUrl + "/cms/delete/"+id);
};

export const preview = (id) => {
  return http.requestGet(apiUrl+"/cms/preview/"+id);
};

export const postPage = (id) => {
  return http.requestGet(apiUrl+"/cms/postPage/"+id);
};



