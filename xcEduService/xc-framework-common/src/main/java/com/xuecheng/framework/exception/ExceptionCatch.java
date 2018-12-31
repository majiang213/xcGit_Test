package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
/**
 * 全局异常管理器
 */
public class ExceptionCatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    //使用EXCEPTIONS存放异常类型和错误代码的映射，ImmutableMap的特点的一旦创建不可改变，并且线程安全
    private static ImmutableMap<Class<? extends Throwable>,ResultCode> EXCEPTIONS;

    //使用builder来构建一个异常类型和错误代码的异常
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();

    /**
     * 监听customException异常,返回json格式的失败代码
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException e){
        LOGGER.error("catch exception : {}\\r\\nexception: ",e.getMessage(), e);
       return new ResponseResult(e.getResultCode());
    }

    @ExceptionHandler
    @ResponseBody
    public ResponseResult exception(Exception e){
        // 记录日志
        LOGGER.error("catch exception : {}",e.getMessage(), e);

        // 判断异常集合是否已经实例化,未实例化则将其实例化
        if (EXCEPTIONS == null){
            EXCEPTIONS = builder.build();
        }
        // 从集合汇总获取当前异常的响应码
        ResultCode resultCode = EXCEPTIONS.get(e.getClass());
        // 响应码不为null则将该响应码返回前端
        if (resultCode != null){
            return new ResponseResult(resultCode);
        }
        // 响应码为null则返回统一错误响应码
        return new ResponseResult(CommonCode.FAIL);
    }

    static {
        //在这里加入一些基础的异常类型判断,异常对象的class对象为key,响应码为value
        builder.put(NullPointerException.class, CommonCode.NULL_POINTER);
    }

}
