package com.behl.app.ws;

import java.util.Arrays;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.behl.app.ws.io.entity.AuthorityEntity;
import com.behl.app.ws.io.entity.RoleEntity;
import com.behl.app.ws.io.entity.UserEntity;
import com.behl.app.ws.io.repositories.AuthorityRepository;
import com.behl.app.ws.io.repositories.RoleRepository;
import com.behl.app.ws.io.repositories.UserRepository;
import com.behl.app.ws.shared.Roles;
import com.behl.app.ws.shared.Utils;

@Component
public class InitialUsersSetup {
/* Use it with H2 database for testing
 * 
 */
//	@Autowired
//	RoleRepository roleRepository;
//
//	@Autowired
//	AuthorityRepository authorityRepository;
//
//	@Autowired
//	BCryptPasswordEncoder bCryptPasswordEncoder;
//
//	@Autowired
//	UserRepository userRepository;
//
//	@Autowired
//	Utils utils;
//
//	@EventListener
//	@Transactional
//	public void onApplicationEvent(ApplicationReadyEvent event) {
//		System.out.println("From App ready event...");
//
//		AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
//		AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
//		AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");
//
//		createRole("ROLE_USER", Arrays.asList(readAuthority, writeAuthority));
//		RoleEntity roleAdmin = createRole("ROLE_ADMIN", Arrays.asList(readAuthority, writeAuthority, deleteAuthority));
//
//		createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority, writeAuthority));
////		RoleEntity roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(readAuthority, writeAuthority));
//
//		if (roleAdmin == null)
//			return;
//
//		UserEntity adminUser = new UserEntity();
//
//		try {
////			adminUser.setFirstName("Navneet");
////			adminUser.setLastName("Behl");
////			adminUser.setEmail("admin@test.com");
////			adminUser.setEmailVerificationStatus(true);
////			adminUser.setUserId(utils.generateUserId(30));
////			adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("test"));
////			adminUser.setRoles(Arrays.asList(roleAdmin));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		userRepository.save(adminUser);
//	}
//
//	@Transactional
//	private AuthorityEntity createAuthority(String name) {
//
//		AuthorityEntity authority = authorityRepository.findByName(name);
//
//		if (authority == null) {
//			authority = new AuthorityEntity(name);
//			authorityRepository.save(authority);
//		}
//
//		return authority;
//	}
//
//	@Transactional
//	private RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
//
//		RoleEntity role = roleRepository.findByName(name);
//
//		if (role == null) {
//			role = new RoleEntity(name);
//			role.setAuthorities(authorities);
//			roleRepository.save(role);
//		}
//
//		return role;
//	}

}
