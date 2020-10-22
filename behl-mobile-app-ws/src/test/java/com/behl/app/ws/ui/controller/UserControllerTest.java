package com.behl.app.ws.ui.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.Resource;

import com.behl.app.ws.service.UserService;
import com.behl.app.ws.shared.dto.AddressDTO;
import com.behl.app.ws.shared.dto.UserDto;
import com.behl.app.ws.ui.model.response.UserRest;

class UserControllerTest {

	@InjectMocks
	UserController userController;

	@Mock
	UserService userService;

	UserDto userDto;

	final String USER_ID = "1234dsfre";

	@BeforeEach
	void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
		userDto = new UserDto();
		userDto.setFirstName("Kanav");
		userDto.setLastName("Behl");
		userDto.setEmail("Test@test.com");
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		userDto.setEmailVerificationToken(null);
		userDto.setUserId(USER_ID);
		userDto.setAddresses(getAddressesDto());
		userDto.setEncryptedPassword("Password");
	}

	@Test
	void testGetUser() {
		when(userService.getUserByUserId(anyString())).thenReturn(userDto);

		UserRest userRest = userController.getUser(USER_ID);
		assertNotNull(userRest);
		assertEquals(USER_ID, userRest.getUserId());
		assertEquals(userDto.getFirstName(), userRest.getFirstName());
		assertEquals(userDto.getLastName(), userRest.getLastName());
		assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());
	}

	private List<AddressDTO> getAddressesDto() {

		AddressDTO addressDto = new AddressDTO();
		addressDto.setType("shipping");
		addressDto.setCity("London");
		addressDto.setCountry("UK");
		addressDto.setPostalCode("AB12CD");
		addressDto.setStreetName("Best Street");

		AddressDTO billingAddressDto = new AddressDTO();
		billingAddressDto.setType("shipping");
		billingAddressDto.setCity("London");
		billingAddressDto.setCountry("UK");
		billingAddressDto.setPostalCode("AB12CD");
		billingAddressDto.setStreetName("Best Street");

		List<AddressDTO> addresses = new ArrayList<>();
		addresses.add(addressDto);
		addresses.add(billingAddressDto);

		return addresses;
	}

}
