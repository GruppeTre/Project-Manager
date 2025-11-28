package com.mavi.projectmanager.controller;

import com.mavi.projectmanager.controller.utils.SessionUtils;
import com.mavi.projectmanager.model.Account;
import com.mavi.projectmanager.model.Employee;
import com.mavi.projectmanager.model.Role;
import com.mavi.projectmanager.service.AccountService;
import com.mavi.projectmanager.service.EmployeeService;
import com.mavi.projectmanager.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;

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
    @MockitoBean
    private HttpSession session;

    private Account testAccount;
    private Employee testEmployee;
    private Account emptyAccount;
    private List<Account> accountList;
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

        accountList = new ArrayList<>();
        accountList.add(testAccount);

        session.setAttribute("account", testAccount);
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

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class)))
                .thenReturn(true);

        mockMvc.perform(get("/edit/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("editUserPage"))
                .andExpect(model().attribute("account", testAccount))
                .andExpect(model().attribute("roles", Role.values()));
        mockedStatic.close();
    }

    @Test
    void shouldShowOverviewPage() throws Exception {
        Mockito.when(accountService.getAccounts()).thenReturn(accountList);

        //Required for the static isLoggedIn method in Session utils.
        //Creates a mocked static and as long as it's a HttpSession.class it will pass as true.
        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class)))
                .thenReturn(true);

        mockMvc.perform(get("/overview").sessionAttr("account", testAccount).param("viewMode", "accounts"))
                .andExpect(status().isOk())
                .andExpect(view().name("overviewPage"))
                .andExpect(model().attribute("accounts", accountList))
                .andExpect(model().attribute("session", testAccount));
        mockedStatic.close();
    }

    /*
    ======================================
    =             POST TESTS             =
    ======================================
     */


    @Test
    void shouldLogInUser() throws Exception{

        //mock correct log in details by forcing service class to return true on login method call
        when(accountService.accountLogin(any(Account.class))).thenReturn(true);

        //mock getting full account details out of service
        when(accountService.getAccountByMail(testAccount.getMail())).thenReturn(testAccount);

        mockMvc.perform(post("/login")
                        .param("mail", testEmployee.getMail())
                        .param("password", testAccount.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/overview?viewMode=accounts"))
                .andExpect(flash().attributeCount(0))
                .andExpect(request().sessionAttribute("account", testAccount));
    }

    @Test
    void shouldRedirectUserToLoginPageOnWrongCredentials() throws Exception {

        //mock wrong log in credentials by forcing service class to return false
        when(accountService.accountLogin(any(Account.class))).thenReturn(false);

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

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class)))
                .thenReturn(true);

        mockMvc.perform(post("/editUser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/overview"))
                .andExpect(flash().attributeCount(0));
        mockedStatic.close();
    }

    @Test
    void shouldCreateUser() throws Exception {
        Account createdTestAccount = testAccount;

        Mockito.when(accountService.createUser(createdTestAccount)).thenReturn(createdTestAccount);

        MockedStatic<SessionUtils> mockedStatic = Mockito.mockStatic(SessionUtils.class);
        mockedStatic.when(() -> SessionUtils.isLoggedIn(Mockito.any(HttpSession.class)))
                .thenReturn(true);

        mockMvc.perform(post("/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/overview"));
        mockedStatic.close();
    }
}