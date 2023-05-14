package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.example.common.R;
import org.example.entity.Employee;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String password = employee.getPassword();
        String s = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(wrapper);
        if (emp == null){
            return R.error("用户不存在");
        }
        if (!emp.getPassword().equals(s)){
            return R.error("密码错误");
        }
        if (emp.getStatus() == 0){
            return R.error("账号已禁用");
        }
        HttpSession session = request.getSession();
        session.setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpServletRequest request){
        log.info("新增员工，员工信息{}",employee);

        Long empId = (Long) request.getSession().getAttribute("employee");
        //设置初始密码，需要md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("000000".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        boolean save = employeeService.save(employee);
        if (save){
            return R.success("新增成功");
        }
        return R.error("新增失败");
    }

    /*用工信息分页*/
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("入参page:{},pageSize:{},name:{}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        return R.success(employeeService.page(pageInfo, queryWrapper));
    }

    @PutMapping
    /*根据id修改员工信息*/
    public R<String> update(@RequestBody Employee employee,HttpServletRequest request){
        log.info(employee.toString());
        log.info("线程id为{}",Thread.currentThread().getId());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(((Long) request.getSession().getAttribute("employee")));
        if (employeeService.updateById(employee)) {
            return R.success("员工修改成功");
        }
        return null;
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询emp");
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}
