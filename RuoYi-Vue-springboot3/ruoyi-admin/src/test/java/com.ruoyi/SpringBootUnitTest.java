package com.ruoyi;

import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.impl.SysRoleServiceImpl;
import com.ruoyi.system.service.impl.SysUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpringBootUnitTest
{
    @Mock
    private SysUserMapper userMapper;

    @Mock
    private SysRoleMapper roleMapper;

    @Mock
    private SysUserRoleMapper userRoleMapper;

    @Mock
    private SysUserPostMapper userPostMapper;

    @Mock
    private SysRoleMenuMapper roleMenuMapper;

    @Mock
    private SysRoleDeptMapper roleDeptMapper;

    @InjectMocks
    private SysUserServiceImpl userService;

    @InjectMocks
    private SysRoleServiceImpl roleService;

    private SysUser testUser;
    private SysRole testRole;

    @BeforeEach
    void initData()
    {
        System.out.println("=======================");
        System.out.println("单元测试初始化测试数据");
        System.out.println("=======================");

        testUser = new SysUser();
        testUser.setUserId(1001L);
        testUser.setUserName("202606206");
        testUser.setNickName("测试用户:dich");

        testRole = new SysRole();
        testRole.setRoleId(1001L);
        testRole.setRoleName("202606206");
        testRole.setRoleKey("测试角色:测试工程师");
        testRole.setMenuIds(new Long[]{});
    }

    @Test
    public void testUserService()
    {
        System.out.println("\n-------------用户单元测试-------------");

        when(userMapper.insertUser(any(SysUser.class))).thenReturn(1);
        int addResult = userService.insertUser(testUser);
        System.out.println("用户新增结果:" + addResult + "成功");

        when(userMapper.updateUser(any(SysUser.class))).thenReturn(1);
        testUser.setNickName("修改后的姓名:dich");
        int updateResult = userService.updateUser(testUser);
        System.out.println("用户修改结果:" + updateResult + "成功");

        when(userMapper.deleteUserById(1001L)).thenReturn(1);
        int deleteResult = userService.deleteUserById(1001L);
        System.out.println("用户删除结果:" + deleteResult + "成功");
    }

    @Test
    public void testRoleService()
    {
        System.out.println("\n--------------角色单元测试---------------");

        when(roleMapper.insertRole(any(SysRole.class))).thenReturn(1);
        int addResult = roleService.insertRole(testRole);
        System.out.println("角色新增结果:" + addResult + "成功");

        when(roleMapper.updateRole(any(SysRole.class))).thenReturn(1);
        testRole.setRoleName("修改后的角色名测试人员dich");
        int updateResult = roleService.updateRole(testRole);
        System.out.println("角色修改结果:" + updateResult + "成功");

        when(roleMapper.deleteRoleById(1001L)).thenReturn(1);
        int deleteResult = roleService.deleteRoleById(1001L);
        System.out.println("角色删除结果:" + deleteResult + "成功");

        System.out.println("\n单元测试全部执行成功");
    }
}
