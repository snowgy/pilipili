package com.example.pilipili;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
public class ImageControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setupMockMvc() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testGetAllImages() throws Exception {
        mvc.perform(post("/getAllImages")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteImageFailed() throws Exception {
        mvc.perform(post("/deleteImage")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("imgId", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetLikeNumber() throws Exception {
        mvc.perform(post("/getLikeNumber")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("path", "/home/gongyue/img/JPEG_20190523_175202_8666866242312622454.jpg")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateLikeNumber() throws Exception {
        String str = "{\"userName\":\"yvette\",\"id\":\"8\", \"likeNum\":\"0\"}";
        System.err.println(str);
        byte[] outputBytes = str.getBytes("UTF-8");
        mvc.perform(post("/updateLikeNum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
