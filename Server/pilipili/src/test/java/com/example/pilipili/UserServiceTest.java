package com.example.pilipili;

import com.example.pilipili.api.UserRepository;
import com.example.pilipili.model.User;
import com.example.pilipili.service.UserService;
import com.example.pilipili.service.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@DataJpaTest
public class UserServiceTest {
    @TestConfiguration
    static class UserServiceImplTestContextConfiguration {

        @Bean
        public UserService userService() {
            return new UserServiceImpl();
        }

    }

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Before
    public void setUp() {
        User user = new User();
        user.setUserName("yueyue");
        user.setPassword("123456");
        Mockito.when(userRepository.findUserByUserName(user.getUserName())).thenReturn(user);
    }

    @Test
    public void whenValidName_theUserShouldBeFound() {
        String name = "yueyue";
        User found = userService.getUserByName(name);

        assert(found.getUserName())
                .equals(name);
    }

    @Test
    public void whenInValidName_theUserShouldBeNull() {
        String name = "yueyue2";
        User found = userService.getUserByName(name);

        assert(found == null);
    }

}
