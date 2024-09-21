package net.journalApp.service;

import net.journalApp.entity.User;
import net.journalApp.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

//@Mock: Use when testing a small part of your code and don't need Spring.
//@MockBean: Use when testing your Spring application but want to replace real parts (beans) with fakes.
public class UserDetailsServiceImplTests {

    // @InjectMocks: It is used in unit tests to automatically inject mock objects (created with @Mock) into the class being tested.
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void initializeMock() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void loadUserByUsernameTest() {
        when(userRepository.findByUsername(ArgumentMatchers.anyString())).thenReturn(User.builder().username("Jack").password("hsvdsGPRHgfg").roles(new ArrayList<>()).build());
        UserDetails user = userDetailsService.loadUserByUsername("Jack");
        Assertions.assertNotNull(user);
    }
}
