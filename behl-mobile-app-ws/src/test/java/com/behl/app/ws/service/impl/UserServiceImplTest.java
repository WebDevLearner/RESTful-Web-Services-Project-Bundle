package com.behl.app.ws.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.behl.app.ws.exceptions.UserServiceException;
import com.behl.app.ws.io.entity.AddressEntity;
import com.behl.app.ws.io.entity.UserEntity;
import com.behl.app.ws.io.repositories.PasswordResetTokenRepository;
import com.behl.app.ws.io.repositories.UserRepository;
import com.behl.app.ws.shared.AmazonSES;
import com.behl.app.ws.shared.Utils;
import com.behl.app.ws.shared.dto.AddressDTO;
import com.behl.app.ws.shared.dto.UserDto;

class UserServiceImplTest {

	@InjectMocks
	UserServiceImpl userService;

	@Mock
	UserRepository userRepository;

	@Mock
	Utils utils;

	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Mock
	AmazonSES amazonSES;

	String userId = "1234";
	String encryptedPassword = "Password";

	UserEntity userEntity;

	@BeforeEach
	void setUp() throws Exception {

		MockitoAnnotations.initMocks(this); // so that Mockito could instantiate objects for like userService.

		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Kanav");
		userEntity.setLastName("Behl");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(encryptedPassword);
		userEntity.setEmail("test@test.com");
		userEntity.setEmailVerificationToken("someToken");
		userEntity.setAddresses(getAddressesEntity());
	}

	@Test
	void testGetUser() {

		when(userRepository.findByEmail(anyString())).thenReturn(userEntity); // we mock the userRepository here.

		UserDto userDto = userService.getUser("test@test.com");

		assertNotNull(userDto);
		assertEquals("Kanav", userDto.getFirstName());
	}

	@Test
	final void testGetUser_UsernameNotFoundException() {

		when(userRepository.findByEmail(anyString())).thenReturn(null);

		assertThrows(UsernameNotFoundException.class,

				() -> {
					userService.getUser("test@test.com");
				}
		);
	}

	@Test
	final void testCreateUser_CreateUserServiceException() {

		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstName("Kanav");
		userDto.setLastName("Behl");
		userDto.setPassword("Password");
		userDto.setEmail("someone@email.com");

		assertThrows(UserServiceException.class, 
				() -> {
						userService.createUser(userDto);
						}
				);
	}

	@Test
	final void testCreateUser() {

		when(userRepository.findByEmail(anyString())).thenReturn(null);
		when(utils.generateAddressId(anyInt())).thenReturn("Address");
		when(utils.generateUserId(anyInt())).thenReturn(userId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
		Mockito.doNothing().when(amazonSES).verifyEmail(any(UserDto.class));

		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstName("Kanav");
		userDto.setLastName("Behl");
		userDto.setPassword("Password");
		userDto.setEmail("someone@email.com");

		UserDto storedUserDetails = userService.createUser(userDto);
		assertNotNull(storedUserDetails);
		assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
		assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
		assertNotNull(storedUserDetails.getUserId());
		assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
		verify(utils, times(storedUserDetails.getAddresses().size())).generateAddressId(30);
		verify(bCryptPasswordEncoder, times(1)).encode("Password");
		verify(userRepository, times(1)).save(any(UserEntity.class));

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

	private List<AddressEntity> getAddressesEntity() {

		List<AddressDTO> addresses = getAddressesDto();

		Type listType = new TypeToken<List<AddressEntity>>() {
		}.getType();
		return new ModelMapper().map(addresses, listType);
	}

}
