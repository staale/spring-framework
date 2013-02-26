package org.springframework.test.web.servlet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Ståle Undheim <staale@staale.org>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = AcceptHeaderDetectionTests.Config.class)
public class AcceptHeaderDetectionTests {

    @Configuration
    @EnableWebMvc
    static class Config extends WebMvcConfigurerAdapter {

        @Bean
        public UrlBasedViewResolver urlBasedViewResolver() {
            UrlBasedViewResolver resolver = new UrlBasedViewResolver();
            resolver.setViewClass(JstlView.class);
            return resolver;
        }
        
        @Bean
        WebController webController() {
            return new WebController();
        }

    }
    
    @Controller
    static class WebController {
        
        @RequestMapping(value = "/some.data", produces = MediaType.APPLICATION_JSON_VALUE)
        @ResponseBody
        public String getSomeData() {
            return "{\"hello\": \"world\"}";
        }
    }

    @Autowired
   	private WebApplicationContext wac;

   	private MockMvc mockMvc;


   	@Before
   	public void setup() {
   		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
   	}

    @Test
    public void testRequest() throws Exception {
        mockMvc.perform(get("/some.data").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        .andExpect(content().string(equalTo("{\"hello\": \"world\"}")));
    }
}
