package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 对前端传过来的明文进行md5加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        // 对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        // 设置账号的状态，正常状态
        employee.setStatus(StatusConstant.ENABLE);
        // 设置密码 默认密码123456
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        // 设置创建时间和修改时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        // 设置当前这条记录的创建人id和修改人id
        // TODO 后期需要修改为当前登录用户的id
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.save(employee);
    }


    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {

        // 开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateStatus(Integer status, Long id) {

        // 第一种方法：创建一个对象
//        Employee employee = new Employee();
//        employee.setId(id);
//        employee.setStatus(status);
//        employee.setUpdateTime(LocalDateTime.now());
        // 第二种方法:创建一个对象
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();

        employeeMapper.update(employee);
    }

    @Override
    public void editEmployee(EmployeeDTO employeeDTO) {

        Employee employee = Employee.builder()
                .id(employeeDTO.getId())
                .username(employeeDTO.getUsername())
                .name(employeeDTO.getName())
                .phone(employeeDTO.getPhone())
                .sex(employeeDTO.getSex())
                .idNumber(employeeDTO.getIdNumber()).build();

        // 第二种方法：对象的属性拷贝
//        BeanUtils.copyProperties(employeeDTO, employee);

        // 设置更新时间
//        employee.setUpdateTime(LocalDateTime.now());
        // 设置更新用户
//        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.update(employee);

    }

    @Override
    public Employee getgetEmployeeById(Long id) {
        Employee employee = employeeMapper.getByUserId(id);
        // 防止前端看到我们的密码
        employee.setPassword("****");
        return employee;
    }
}
