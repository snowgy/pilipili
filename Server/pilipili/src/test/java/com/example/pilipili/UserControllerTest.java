package com.example.pilipili;

import com.example.pilipili.model.User;
import com.example.pilipili.service.UserService;
import com.example.pilipili.service.auth.LoginService;
import com.example.pilipili.service.auth.SignupService;
import com.example.pilipili.web.UserController;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setupMockMvc() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testLoginSuccess() throws Exception {
        String str = "{\"userName\":\"root\",\"password\":\"1234\"}";
        System.err.println(str);
        byte[] outputBytes = str.getBytes("UTF-8");
        mvc.perform(post("/userLogin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testLoginPasswordFail() throws Exception {
        String str = "{\"userName\":\"root\",\"password\":\"1234567\"}";
        System.err.println(str);
        byte[] outputBytes = str.getBytes("UTF-8");
        mvc.perform(post("/userLogin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1));
    }

    @Test
    public void testLoginNoUserExisted() throws Exception {
        String str = "{\"userName\":\"root2\",\"password\":\"1234567\"}";
        System.err.println(str);
        byte[] outputBytes = str.getBytes("UTF-8");
        mvc.perform(post("/userLogin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1));
    }

//    @Test
//    public void testSignUpSuccess() throws Exception {
//        String str = "{\"userName\":\"gygy4\",\"password\":\"123456\"}";
//        System.err.println(str);
//        byte[] outputBytes = str.getBytes("UTF-8");
//        mvc.perform(post("/userSignup")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(str)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(0));
//    }

    @Test
    public void testSignUpFail() throws Exception {
        String str = "{\"userName\":\"root\",\"password\":\"123456\"}";
        System.err.println(str);
        byte[] outputBytes = str.getBytes("UTF-8");
        mvc.perform(post("/userSignup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1));


    }

    @Test
    public void testGetUserImage() throws Exception {
        mvc.perform(post("/getUserImages")
                    .param("userName", "root")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
    }

    @Test
    public void testGetUserImageZero() throws Exception {
        mvc.perform(post("/getUserImages")
                .param("userName", "gygy3")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetLovedPhoto() throws Exception {
        mvc.perform(post("/getLovedImages")
                .param("userName", "yvette")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testGetLovedPhotoZero() throws Exception {
        mvc.perform(post("/getLovedImages")
                .param("userName", "gygy3")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
