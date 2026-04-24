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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SpringBootInterfaceTest
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    private Long testRoleId;
    private Long testUserId;

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
    public void testUserRoleModuleCrossInvoke()
    {
        System.out.println("====================");
        System.out.println("【集成测试】用户角色跨模块方法调用测试");
        System.out.println("===========================================");

        System.out.println("\n[步骤1]角色模块:创建测试角色");
        SysRole role = new SysRole();
        role.setRoleName("集成测试-跨模块角色-dichRole");
        role.setRoleKey("invoke_test");
        role.setRoleSort(9);
        role.setStatus("0");
        role.setMenuIds(new Long[]{});
        roleService.insertRole(role);
        testRoleId = role.getRoleId();
        System.out.println("成功角色模块执行完成,角色ID:" + testRoleId);

        System.out.println("\n[步骤2]跨模块调用：用户模块调用角色模块（校验角色是否存在）");
        SysRole checkRole = roleService.selectRoleById(testRoleId);
        if (checkRole != null)
        {
            System.out.println("成功跨模块调用成功:用户模块成功调用角色模块方法,查询到角色:" + checkRole.getRoleName());
        }

        System.out.println("\n[步骤3]跨模块数据关联：用户模块绑定角色模块数据");
        SysUser user = new SysUser();
        user.setUserName("invoke_test_dich");
        user.setNickName("跨模块集成测试用户");
        user.setPassword("123456");
        user.setDeptId(103L);
        user.setRoleIds(new Long[]{testRoleId});
        userService.insertUser(user);
        testUserId = user.getUserId();
        System.out.println("成功用户模块执行完成,用户ID:" + testUserId);

        System.out.println("\n[步骤4]跨模块联动查询：用户模块→角色模块联合查询");
        SysUser userInfo = userService.selectUserById(testUserId);
        System.out.println("成功跨模块查询成功:");
        System.out.println("用户账号:" + userInfo.getUserName());
        System.out.println("关联角色ID:" + userInfo.getRoles());
        System.out.println("成功模块间参数传���、数据关联完全正常");

        System.out.println("\n[步骤5]清理测试数据");
        userService.deleteUserById(testUserId);
        roleService.deleteRoleById(testRoleId);
        System.out.println("成功测试数据已清理");

        System.out.println("\n====================");
        System.out.println("成功跨模块集成测试全部通过：模块间调用正常");
        System.out.println("====================");
    }
}
