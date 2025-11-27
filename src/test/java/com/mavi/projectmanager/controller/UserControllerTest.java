package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Role;
import com.mavi.projectmanager.service.AccountService;
import com.mavi.projectmanager.service.EmployeeService;
import com.mavi.projectmanager.service.ProjectService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.mockito.internal.matchers.InstanceOf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.View;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;
    @MockitoBean
    private ProjectService projectService;
    @MockitoBean
    private EmployeeService employeeService;

    private Account testAccount;
    private Employee testEmployee;
    private Account emptyAccount;
    private Employee emptyEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee();
        testEmployee.setId(1);
        testEmployee.setPosition("Lead Developer");
        testEmployee.setFirstName("Peter");
        testEmployee.setLastName("Petersen");
        testEmployee.setMail("pepe@company.com");

        testAccount = new Account();
        testAccount.setId(1);
        testAccount.setRole(Role.ADMIN);
        testAccount.setPassword("1234");
        testAccount.setEmployee(testEmployee);

        emptyAccount = new Account();
        emptyEmployee = new Employee();
    }


    /*
    ======================================
    =            GETTER TESTS            =
    ======================================
     */
    @Test
    void shouldShowLoginPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("account", emptyAccount))
                .andExpect(model().attribute("employee", emptyEmployee));
    }

    @Test
    void shouldShowEditUserPage() throws Exception{

        when(accountService.getAccountByID(1)).thenReturn(testAccount);

        mockMvc.perform(get("/user/edit/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("editUserPage"))
                .andExpect(model().attribute("account", testAccount))
                .andExpect(model().attribute("roles", Role.values()));
    }

    /*
    ======================================
    =             POST TESTS             =
    ======================================
     */


    @Test
    void shouldLogInUser() throws Exception{

        //mock correct log in details by forcing service class to return true on login method call
        when(accountService.accountLogin(any(Employee.class), any(Account.class))).thenReturn(true);

        //mock getting full account details out of service
        when(accountService.getAccountByMail(any(Account.class), eq(testEmployee.getMail()))).thenReturn(testAccount);

        mockMvc.perform(post("/login")
                        .param("mail", testEmployee.getMail())
                        .param("password", testAccount.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/overview"))
                .andExpect(flash().attributeCount(0))
                .andExpect(request().sessionAttribute("account", testAccount));
    }

    @Test
    void shouldRedirectUserToLoginPageOnWrongCredentials() throws Exception {

        //mock wrong log in credentials by forcing service class to return false
        when(accountService.accountLogin(any(Employee.class), any(Account.class))).thenReturn(false);

        mockMvc.perform(post("/login")
                        .param("mail", testEmployee.getMail())
                        .param("password", testAccount.getPassword()))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("error", true))
                .andExpect(model().attribute("account", Matchers.instanceOf(Account.class)))
                .andExpect(model().attribute("employee", Matchers.instanceOf(Employee.class)));
    }

    @Test
    void shouldEditUser() throws Exception{
        Account updatedTestAccount = testAccount;

        when(accountService.updatedAccount(updatedTestAccount)).thenReturn(updatedTestAccount);

        mockMvc.perform(post("/user/editUser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/edit/1"))
                .andExpect(flash().attributeCount(0));
    }
}