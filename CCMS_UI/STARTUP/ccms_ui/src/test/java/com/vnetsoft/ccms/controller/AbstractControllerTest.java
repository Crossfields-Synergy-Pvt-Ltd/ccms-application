package com.vnetsoft.ccms.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractControllerTest {

    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public abstract void setUp();

    protected void configureController(Object controller) {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    protected ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(url)
                .accept(MediaType.APPLICATION_JSON));
    }

    protected ResultActions performGetWithAuth(String url, String auth) throws Exception {
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(url)
                .header("Authorization", auth)
                .accept(MediaType.APPLICATION_JSON));
    }

    protected ResultActions performPost(String url, String jsonBody) throws Exception {
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON));
    }

    protected ResultActions performDelete(String url) throws Exception {
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(url)
                .accept(MediaType.APPLICATION_JSON));
    }

    protected void expectSuccess(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
              .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    protected String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
