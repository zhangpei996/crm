package com.shsxt.crm.service;

import com.shsxt.crm.dao.UserMapper;
import com.shsxt.crm.model.UserModel;
import com.shsxt.crm.utils.AssertUtil;
import com.shsxt.crm.utils.Md5Util;
import com.shsxt.crm.utils.UserIDBase64;
import com.shsxt.crm.vo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    /**
     * 登录
     * 非空判断
     * 用户名查询非空
     * 判断user里密码是否和传入的参数相等
     * 相等return user
     */
    public UserModel login(String userName,String userPwd){
        checkLoginParams(userName,userPwd);
        User user = userMapper.queryUserByUserName(userName);
        AssertUtil.isTrue(user==null,"用户名不正确");
        AssertUtil.isTrue(!Md5Util.encode(userPwd).equals(user.getUserPwd()),"密码不正确");
        UserModel userModel = new UserModel(UserIDBase64.encoderUserID(user.getId()),user.getUserName(),user.getTrueName());
        return userModel;
    }

    private void checkLoginParams(String userName, String userPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"密码不能为空");
    }
    /**
     * 显示
     */
    public User selectByPrimaryKey(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }
    /**
     * 密码修改
     * 用户id 原始密码 新密码，确认密码
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserPassword(Integer userId,String oldPassword,String newPassword,String confirmPassword){
        checkUpdateParams( userId, oldPassword, newPassword, confirmPassword);
        User user = userMapper.selectByPrimaryKey(userId);
        user.setUserPwd(Md5Util.encode(newPassword));
        Integer flag = userMapper.updateByPrimaryKeySelective(user);
        if (flag < 1){
            AssertUtil.isTrue(true,"更新失败");
        }
    }

    private void checkUpdateParams(Integer userId, String oldPassword, String newPassword, String confirmPassword) {
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"原始密码不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"新密码不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword),"确认密码不能为空");
        AssertUtil.isTrue(!newPassword.equals(confirmPassword),"新密码和确认密码不相同");
        User user = userMapper.selectByPrimaryKey(userId);
        AssertUtil.isTrue(!user.getUserPwd().equals(Md5Util.encode(oldPassword)),"原始密码不正确");
    }

}
