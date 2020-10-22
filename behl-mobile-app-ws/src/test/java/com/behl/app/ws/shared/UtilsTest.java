package com.behl.app.ws.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

@ExtendWith(SpringExtension.class) // @RunWith(SpringRunner.class with Junit4
@SpringBootTest
class UtilsTest {
	
	@Autowired
	Utils utils;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGenerateUserId() {
		String userId = utils.generateUserId(30);
		String userId2 = utils.generateAddressId(30);
		
		assertNotNull(userId);
		assertTrue(userId.length() == 30);
		assertTrue(!userId.equalsIgnoreCase(userId2));
		
	}

	@Test
	void testHasTokenNotExpired() {
		String token = utils.generateEmailVerificationToken("someUserID");
		assertNotNull(token);
		
		boolean hasTokenExpired = Utils.hasTokenExpired(token);
		assertFalse(hasTokenExpired);
	}
	
	@Test
	final void testHasTokenExpired() {
		String expiredToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxVEF3OGVRbWU1WXQ2eXVZeThFRkV1TUlLZlN5Z3giLCJleHAiOjE1ODE1OTU5MjV9.4VuaGW-bKZ1BQyO4VRsLeCT_I1V98DafBh28oHU-4SkBya7PWlF54trk6VxG16XOxNTuUAOlqePw5zCaUkWPkQ";
		boolean hasTokenExpired = Utils.hasTokenExpired(expiredToken);
		
		assertTrue(!hasTokenExpired);
	}

}
