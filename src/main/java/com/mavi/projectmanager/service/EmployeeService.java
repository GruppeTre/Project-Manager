package com.mavi.projectmanager.service;

import com.mavi.projectmanager.exception.Field;
import com.mavi.projectmanager.exception.InvalidFieldException;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.repository.AccountRepository;
import com.mavi.projectmanager.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    public Employee getEmployeeByMail(String mail) {

        //har lige udkommenteret exceptionen her, ved ikke om det er det rigtige sted at throwe i en getter
//        if (employee == null) {
//            throw new InvalidFieldException("Mail does not exist", Field.EMAIL);
//        }

        return employeeRepository.getEmployeeByMail(mail);
    }

    //Get all accounts with specific role
    public List<Employee> getEmployeesByRole() {return employeeRepository.getEmployeesByRole();}

    private boolean isValidMail(Employee employee){
        return employee != null;
    }
}
