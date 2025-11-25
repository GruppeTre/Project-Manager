package com.mavi.projectmanager.service;

import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.repository.AccountRepository;
import com.mavi.projectmanager.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    public Employee getEmployeeByMail(String mail) {

        Employee checkEmployee = employeeRepository.getEmployeeByMail(mail);

        if (checkEmployee == null) {
            throw new RuntimeException();
        }
        return checkEmployee;
    }
}
