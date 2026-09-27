package ru.mirea.autopartsstore.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import ru.mirea.autopartsstore.auth.entity.AppUser;
import ru.mirea.autopartsstore.auth.entity.UserRole;
import ru.mirea.autopartsstore.auth.repository.AppUserRepository;
import ru.mirea.autopartsstore.auth.service.JwtService;
import ru.mirea.autopartsstore.customer.entity.Customer;
import ru.mirea.autopartsstore.customer.repository.CustomerRepository;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "jwt.secret=MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=",
        "jwt.expiration=86400000"
})
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {

        String suffix = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        Customer customer = new Customer();
        customer.setName("Test User");
        customer.setEmail(
                "user-" + suffix + "@test.ru"
        );

        customer =
                customerRepository.save(customer);

        AppUser user = new AppUser();
        user.setEmail(customer.getEmail());
        user.setPasswordHash("test");
        user.setRole(UserRole.USER);
        user.setCustomer(customer);

        user = appUserRepository.save(user);

        userToken =
                jwtService.generateToken(user);


        AppUser admin = new AppUser();
        admin.setEmail(
                "admin-" + suffix + "@test.ru"
        );
        admin.setPasswordHash("test");
        admin.setRole(UserRole.ADMIN);

        admin = appUserRepository.save(admin);

        adminToken =
                jwtService.generateToken(admin);
    }

    @Test
    void parts_shouldBePublic() throws Exception {

        mockMvc.perform(
                        get("/api/parts")
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void profile_shouldRequireAuthentication()
            throws Exception {

        mockMvc.perform(
                        get("/api/profile")
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void inventory_shouldRejectUser()
            throws Exception {

        mockMvc.perform(
                        get("/api/inventory")
                                .header(
                                        "Authorization",
                                        "Bearer " + userToken
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void inventory_shouldAllowAdmin()
            throws Exception {

        mockMvc.perform(
                        get("/api/inventory")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void profile_shouldReturnCurrentUser()
            throws Exception {

        mockMvc.perform(
                        get("/api/profile")
                                .header(
                                        "Authorization",
                                        "Bearer " + userToken
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.role")
                                .value("USER")
                )
                .andExpect(
                        jsonPath("$.customerId")
                                .exists()
                );
    }

    @Test
    void orders_shouldRequireAuthentication()
            throws Exception {

        mockMvc.perform(
                        get("/api/orders")
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void orders_shouldAllowUser()
            throws Exception {

        mockMvc.perform(
                        get("/api/orders")
                                .header(
                                        "Authorization",
                                        "Bearer " + userToken
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.content").isArray()
                );
    }

    @Test
    void orderStatus_shouldRejectUser()
            throws Exception {

        mockMvc.perform(
                        patch("/api/orders/999/status")
                                .header(
                                        "Authorization",
                                        "Bearer " + userToken
                                )
                                .contentType(
                                        "application/json"
                                )
                                .content("""
                            {
                              "status": "CONFIRMED"
                            }
                            """)
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void partsCreate_shouldRejectUser()
            throws Exception {

        mockMvc.perform(
                        post("/api/parts")
                                .header(
                                        "Authorization",
                                        "Bearer " + userToken
                                )
                                .contentType(
                                        "application/json"
                                )
                                .content("""
                            {
                              "name": "Test part",
                              "sku": "TEST",
                              "article": "TEST",
                              "description": "Test",
                              "price": 500,
                              "manufacturerId": 1,
                              "categoryId": 1
                            }
                            """)
                )
                .andExpect(
                        status().isForbidden()
                );
    }
}
