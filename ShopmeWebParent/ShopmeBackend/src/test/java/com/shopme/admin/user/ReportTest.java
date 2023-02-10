package com.shopme.admin.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ReportTest {

	@Autowired 
	private MockMvc mockMvc;

	@Test
	@WithMockUser(username = "piyushmalik08@gmail.com", password = "piyush", authorities = {"Admin"})
	public void testGetReportDataLast7Days() throws Exception {		
		
		String requestURL = "/report/sales/last_7_days";		

		mockMvc.perform(get(requestURL)).andExpect(status().isOk()).andDo(print());
	}
}