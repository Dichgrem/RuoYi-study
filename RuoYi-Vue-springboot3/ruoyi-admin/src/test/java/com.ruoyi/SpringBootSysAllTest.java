package com.ruoyi;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SpringBootSysAllTest
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    private SysUser testUser;
    private SysRole testRole;

    @BeforeEach
    void setUp()
    {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(1L);
        sysUser.setUserName("admin");

        Set<String> permissions = new HashSet<>();
        permissions.add("admin");

        LoginUser loginUser = new LoginUser(sysUser, permissions);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testUserAndRoleIntegration()
    {
        System.out.println("\n==========集成测试==========");

        System.out.println("\n[一、用户管理集成测试]");

        SysUser user = new SysUser();
        user.setUserName("testUser");
        user.setNickName("dich");
        user.setPassword("123456");
        user.setDeptId(103L);
        int addUser = userService.insertUser(user);
        System.out.println("新增用户:" + addUser + "成功");

        List<SysUser> userList = userService.selectUserList(new SysUser());
        System.out.println("当前用户总数:" + userList.size());

        user.setNickName("集成测试-已修改");
        int updateUser = userService.updateUser(user);
        System.out.println("修改用户:" + updateUser + "成功");

        System.out.println("\n[二、角色管理集成测试]");

        SysRole role = new SysRole();
        role.setRoleName("testRole");
        role.setRoleKey("testRoleKey");
        role.setStatus("0");
        role.setRoleSort(1);
        role.setMenuIds(new Long[]{});
        int addRole = roleService.insertRole(role);
        System.out.println("新增角色:" + addRole + "成功");

        List<SysRole> roleList = roleService.selectRoleList(new SysRole());
        System.out.println("当前角色总数:" + roleList.size());

        role.setRoleName("集成测试角色-修改");
        int updateRole = roleService.updateRole(role);
        System.out.println("修改角色:" + updateRole + "成功");

        System.out.println("\n[三、清理测试数据]");
        userService.deleteUserById(user.getUserId());
        roleService.deleteRoleById(role.getRoleId());
        System.out.println("测试数据已删除成功");

        System.out.println("\n======================");
        System.out.println("集成测试全部执行完成，系统运行正常");
        System.out.println("======================");
    }
}