package com.c4_soft.dzone_oauth2_spring.c4_greeting_api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import com.c4_soft.springaddons.security.oauth2.test.annotations.WithMockAuthentication;
import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;

@WebMvcTest(GreetingController.class)
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import(SecurityConf.class)
class GreetingControllerTest {
	private static final String greeting = "Hello!";
	
	@MockBean
	GreetingService greetingService;
	
	@Autowired
	MockMvc mockMvc;
	
	@BeforeEach
	public void setUp() {
		when(greetingService.getGreeting()).thenReturn(greeting);
	}

	@Test
	void givenSecurityContextIsEmpty_whenGetGreeting_thenUnauthorized() throws Exception {
		mockMvc.perform(get("/greeting")).andExpect(status().isUnauthorized());
	}

	@Test
	@WithAnonymousUser
	void givenSecurityContextIsAnonymous_whenGetGreeting_thenUnauthorized() throws Exception {
		mockMvc.perform(get("/greeting")).andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockAuthentication(name = "ch4mp", authorities = {"NICE", "AUTHOR"})
	void givenUserHasNiceAuthority_whenGetGreeting_thenOk() throws Exception {
		mockMvc.perform(get("/greeting")).andExpect(status().isOk()).andExpect(jsonPath("$.message").value(greeting));
	}

	@Test
	@WithMockAuthentication("AUTHOR")
	void givenUserDoesNotHaveNiceAuthority_whenGetGreeting_thenForbidden() throws Exception {
		mockMvc.perform(get("/greeting")).andExpect(status().isForbidden());
	}

	@Test
	@WithJwt("brice.json")
	void givenUserIsBrice_whenGetGreeting_thenOk() throws Exception {
		mockMvc.perform(get("/greeting")).andExpect(status().isOk()).andExpect(jsonPath("$.message").value(greeting));
	}

	@Test
	@WithJwt("igor.json")
	void givenUserIsIgor_whenGetGreeting_thenForbidden() throws Exception {
		mockMvc.perform(get("/greeting")).andExpect(status().isForbidden());
	}

}
