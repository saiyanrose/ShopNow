package com.shopme.admin.user;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.common.entity.Country;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	@WithMockUser(username = "piyushmalik08@gmail.com",password = "piyush",roles = "ADMIN")
	public void testListCountries() throws Exception {
		String url="/countries/list";
		MvcResult result= mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();
		
		String jsonResponse=result.getResponse().getContentAsString();
		System.out.println(jsonResponse);
		
		Country[] countries=objectMapper.readValue(jsonResponse,Country[].class);
	}
	
	@Test
	@WithMockUser(username = "piyushmalik08@gmail.com",password = "piyush",roles = "ADMIN")
	public void testsaveCountries() throws Exception {
		String url="/countries/save";
		String countryName="CANADA";
		String countryCode="CA";
		Country country=new Country(countryName, countryCode);
		MvcResult result= mockMvc.perform(post(url).contentType("application/json")
				.content(objectMapper.writeValueAsString(country)).with(csrf())).andDo(print()).
				andExpect(status().isOk()).
				andReturn();
		
		String jsonResponse=result.getResponse().getContentAsString();
		System.out.println(jsonResponse);
		
		Country[] countries=objectMapper.readValue(jsonResponse,Country[].class);
	}
}
