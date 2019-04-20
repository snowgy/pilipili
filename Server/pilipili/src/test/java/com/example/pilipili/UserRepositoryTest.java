package com.example.pilipili;

import com.example.pilipili.api.UserRepository;
import com.example.pilipili.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindByName_thenReturnEmployee() {
        // given
        User user = new User();
        user.setUserName("testUser");
        user.setPassword("123456");
        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findUserByUserName(user.getUserName());

        // then
        assert(found.getUserName().equals(user.getUserName()));
    }

    @Test
    public void whenNotFindByName_thenReturnNull() {

        // when
        User found = userRepository.findUserByUserName("balabala");

        // then
        assert(found == null);
    }
}
