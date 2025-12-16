package com.mavi.projectmanager.service;

import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    //Jacob Klitgaard
    public Employee getEmployeeByMail(String mail) {
        return employeeRepository.getEmployeeByMail(mail);
    }

}
