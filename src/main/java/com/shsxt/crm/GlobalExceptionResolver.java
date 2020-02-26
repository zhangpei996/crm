package com.shsxt.crm;

import com.alibaba.fastjson.JSON;
import com.shsxt.crm.exceptions.NoLoginException;
import com.shsxt.crm.exceptions.ParamsException;
import com.shsxt.crm.model.ResultInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
       ModelAndView m = new ModelAndView();
       if (e instanceof NoLoginException){
           NoLoginException nle = (NoLoginException) e;
           m.setViewName("no_login");
           m.addObject("msg",nle.getMsg());
           m.addObject("code",nle.getCode());
           m.addObject("ctx",request.getContextPath());
           return m;
       }
        m.setViewName("errors");
        m.addObject("code",400);
        m.addObject("msg","系统异常,请稍后再试...");
        if (handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            ResponseBody annotation = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            if (annotation ==null){
                /**
                 * 返回视图
                 */
                if (e instanceof ParamsException){
                    m.addObject("code",((ParamsException) e).getCode());
                    m.addObject("msg",((ParamsException) e).getMsg());
                }
                return m;
            }else {
                /**
                 * 返回json
                 */
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统错误请重试");
                if (e instanceof ParamsException){
                    resultInfo.setCode(((ParamsException) e).getCode());
                    resultInfo.setMsg(((ParamsException) e).getMsg());
                }
                response.setCharacterEncoding("utf8");
                response.setContentType("application/json;charset=utf8");
                PrintWriter printWriter = null;
                try {
                    printWriter = response.getWriter();
                    printWriter.write(JSON.toJSONString(resultInfo));
                    printWriter.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }finally {
                    if (printWriter!=null){
                        printWriter.close();
                    }
                }
                return null;
            }
        }else {
            return m;
        }

    }
    /**
     * 异常处理机制需要实现一个接口
     * 异常处理分为两种
     * json
     * 通过反射类注解判断
     * 通过response输出流
     * 视图
     * 返回视图
     */
}
