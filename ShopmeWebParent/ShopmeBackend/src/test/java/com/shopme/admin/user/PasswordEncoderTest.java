package com.shopme.admin.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderTest {

	@Test
	public void testEncoder() {
		PasswordEncoder encoder=new BCryptPasswordEncoder();
		String raw="sonu";
		String encodedPassword=encoder.encode(raw);
		System.out.println(encodedPassword);
	}
}
