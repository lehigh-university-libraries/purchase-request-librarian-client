package edu.lehigh.libraries.purchase_request.librarian_client.security;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Exercises the {@link SecurityConfig} filter chain in isolation: public vs. protected
 * URL matching, and Basic Auth against the {@link DaoAuthenticationProvider}/BCrypt setup.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SecurityConfig.class)
@WebAppConfiguration
class SecurityConfigTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
    }

    @Test
    void protectedUrlRejectsUnauthenticatedRequests() throws Exception {
        mockMvc.perform(get("/librarians"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedUrlRejectsInvalidCredentials() throws Exception {
        when(userRepository.findByUsername("librarian")).thenReturn(null);

        mockMvc.perform(get("/librarians").with(httpBasic("librarian", "wrong-password")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedUrlAcceptsValidBasicAuth() throws Exception {
        User user = new User();
        user.setUsername("librarian");
        user.setPassword(new BCryptPasswordEncoder().encode("secret"));
        when(userRepository.findByUsername("librarian")).thenReturn(user);

        // No controller is mapped in this minimal context, so a 404 (rather than 401)
        // confirms the request was authenticated and passed on by the filter chain.
        mockMvc.perform(get("/librarians").with(httpBasic("librarian", "secret")))
            .andExpect(status().isNotFound());
    }

    @Test
    void publicUrlsAreAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/resources/js/nonexistent.js"))
            .andExpect(status().isNotFound());
    }

}
