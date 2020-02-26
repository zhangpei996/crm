package com.shsxt.crm.controller;

import com.shsxt.base.BaseController;
import com.shsxt.crm.exceptions.ParamsException;
import com.shsxt.crm.model.ResultInfo;
import com.shsxt.crm.model.UserModel;
import com.shsxt.crm.service.UserService;
import com.shsxt.crm.utils.CookieUtil;
import com.shsxt.crm.utils.LoginUserUtil;
import com.shsxt.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {
    @Resource
    private UserService userService;

    @ResponseBody
    @PostMapping("login")
    public ResultInfo login(String userName,String userPwd){
        UserModel userModel = userService.login(userName,userPwd);
        return success("登录成功",userModel);
    }
    @ResponseBody
    @PostMapping("updatePassword")
    public ResultInfo updatePassword(HttpServletRequest request,String oldPassword,String newPassword,String confirmPassword){
        userService.updateUserPassword(LoginUserUtil.releaseUserIdFromCookie(request),oldPassword,newPassword,confirmPassword);
        return success("密码更新成功");
    }


}
