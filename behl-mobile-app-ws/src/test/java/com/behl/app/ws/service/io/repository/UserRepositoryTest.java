package com.behl.app.ws.service.io.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.behl.app.ws.io.entity.AddressEntity;
import com.behl.app.ws.io.entity.UserEntity;
import com.behl.app.ws.io.repositories.UserRepository;

//to make it an integration test we use the following annotation
@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {

	@Autowired
	UserRepository userRepository;
	static boolean recordsCreated = false;

	
	@BeforeEach
	void setUp() throws Exception {
		
		if(!recordsCreated) createRecords();
	}

	@Test
	void testGetVerifiedUsers() {

		Pageable pageableRequest = PageRequest.of(0, 2);
		Page<UserEntity> pages = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);

		assertNotNull(pages);
		
		List<UserEntity> userEntities = pages.getContent();
		assertNotNull(userEntities);
		System.out.println(userEntities.size());
		assertTrue(userEntities.size() == 2);
	}
	
	@Test
	final void testFindUserByFirstName() {
		String firstName = "Navneet";
		List<UserEntity> users = userRepository.findUserByFirstName(firstName);
		assertNotNull(users);
		assertTrue(users.size()==2);
		
		UserEntity user = users.get(0);
		assertTrue(user.getFirstName().equals(firstName));
	}
	
	
	@Test
	final void testFindUserBylastName() {
		String lastName = "Behl";
		List<UserEntity> users = userRepository.findUserByLastName(lastName);
		assertNotNull(users);
		assertTrue(users.size()==2);
		
		UserEntity user = users.get(0);
		assertTrue(user.getLastName().equals(lastName));
	}
	
	@Test
	final void testFindUserByKeyword() {
		String keyword = "hl";
		List<UserEntity> users = userRepository.findUserByKeyword(keyword);
		assertNotNull(users);
		assertTrue(users.size()==2);
		
		UserEntity user = users.get(0);
		assertTrue(
				user.getLastName().contains(keyword) || user.getFirstName().contains(keyword)
				);
	}
	
	@Test
	final void testFindUserFirstNameAndLastNameByKeyword() {
		String keyword = "hl";
		List<Object[]> users = userRepository.findUserFirstNameAndLastNameByKeyword(keyword);
		assertNotNull(users);
		assertTrue(users.size()==2);
		
		Object[] user = users.get(0);
		
		assertTrue(user.length == 2);
		
		String userFirstName = String.valueOf(user[0]);
		String userLastName = String.valueOf(user[1]);
		
		assertNotNull(userFirstName);
		assertNotNull(userLastName);
		
		System.out.println("First name = " + userFirstName);
		System.out.println("Last name = " + userLastName);

	}
	
	@Test
	final void testUpdateUserEmailVerificationStatus() {
		boolean newEmailVerificationStatus = false;
		userRepository.updateUserEmailVerificationStatus(newEmailVerificationStatus, "12234");
		
		UserEntity storedUserDetails = userRepository.findByUserId("12234");
	
		boolean storedemailverificationStatus = storedUserDetails.getEmailVerificationStatus();
		
		assertTrue(storedemailverificationStatus == newEmailVerificationStatus);
	}
	
	
	@Test
	final void testfindUserEntityByUserId() {
		String userId = "12234";
		UserEntity userEntity = userRepository.findUserEntityByUserId(userId);
		
		assertNotNull(userEntity);
		assertTrue(userEntity.getUserId().equals(userId));
	}
	
	@Test
	final void testGetUserEntityFullNameById() {
		String userId = "12234";
		
		List<Object[]> records = userRepository.getUserEntityFullNameById(userId);
		
		assertNotNull(records);
		assertTrue(records.size() == 1);
		
		Object[] userDetails = records.get(0);
		
		String firstName = String.valueOf(userDetails[0]);
		String lastName = String.valueOf(userDetails[1]);
		
	}
	
	@Test
	final void testupdateUserEntityEmailVerificationStatus() {
		boolean newEmailVerificationStatus = false;
		userRepository.updateUserEntityEmailVerificationStatus(newEmailVerificationStatus, "12234");
		
		UserEntity storedUserDetails = userRepository.findByUserId("12234");
		
		boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerificationStatus();
		
		assertTrue(storedEmailVerificationStatus == newEmailVerificationStatus);
	}
	
	
	private void createRecords() {
		
		//Prepare User entity. presuming we use in memory DB like H2 for testing
		UserEntity userEntity = new UserEntity();
		userEntity.setFirstName("Navneet");
		userEntity.setLastName("Behl");
		userEntity.setUserId("12234");
		userEntity.setEncryptedPassword("xxxxx");
		userEntity.setEmail("testTest@test.com");
		userEntity.setEmailVerificationStatus(true);
		
		//prepare User Addresses
		AddressEntity addressEntity = new AddressEntity();
		addressEntity.setType("shipping");
		addressEntity.setAddressId("FDSAW");
		addressEntity.setCity("London");
		addressEntity.setCountry("UK");
		addressEntity.setPostalCode("ABC123");
		addressEntity.setStreetName("my street");
		
		List<AddressEntity> addresses = new ArrayList<AddressEntity>();
		addresses.add(addressEntity);
		
		userEntity.setAddresses(addresses);
		userRepository.save(userEntity);
		
		
		/////////////////////////////////////
		UserEntity userEntity2 = new UserEntity();
		userEntity2.setFirstName("Navneet");
		userEntity2.setLastName("Behl");
		userEntity2.setUserId("122345");
		userEntity2.setEncryptedPassword("xxxxx");
		userEntity2.setEmail("testTest@test.com");
		userEntity2.setEmailVerificationStatus(true);
		
		//prepare User Addresses
		AddressEntity addressEntity2 = new AddressEntity();
		addressEntity2.setType("shipping");
		addressEntity2.setAddressId("FDSAWs");
		addressEntity2.setCity("London");
		addressEntity2.setCountry("UK");
		addressEntity2.setPostalCode("ABC123");
		addressEntity2.setStreetName("my street");
		
		List<AddressEntity> addresses2 = new ArrayList<AddressEntity>();
		addresses.add(addressEntity2);
		
		userEntity2.setAddresses(addresses2);
		userRepository.save(userEntity2);
		
		recordsCreated = true;
	}


	
}
