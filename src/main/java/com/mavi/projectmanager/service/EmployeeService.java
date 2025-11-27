package com.mavi.projectmanager.service;

import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.repository.AccountRepository;
import com.mavi.projectmanager.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public boolean isValidMail(Employee employee){
        return employee != null;
    }

    public EmployeeService(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    public Employee getEmployeeByMail(String mail) {

        Employee employee = employeeRepository.getEmployeeByMail(mail);

        if (!isValidMail(employee)) {
            throw new InvalidFieldException("Mail does not exist", Field.EMAIL);
        }
        return employee;
    }
}
