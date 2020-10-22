package com.behl.app.ws.ui.controller;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.behl.app.ws.exceptions.UserServiceException;
import com.behl.app.ws.service.AddressService;
import com.behl.app.ws.service.UserService;
import com.behl.app.ws.shared.Roles;
import com.behl.app.ws.shared.dto.AddressDTO;
import com.behl.app.ws.shared.dto.UserDto;
import com.behl.app.ws.ui.model.request.PasswordResetModel;
import com.behl.app.ws.ui.model.request.PasswordResetRequestModel;
import com.behl.app.ws.ui.model.request.UserDetailsRequestModel;
import com.behl.app.ws.ui.model.response.AddressesRest;
import com.behl.app.ws.ui.model.response.ErrorMessages;
import com.behl.app.ws.ui.model.response.OperationStatusModel;
import com.behl.app.ws.ui.model.response.RequestOperationName;
import com.behl.app.ws.ui.model.response.RequestOperationStatus;
import com.behl.app.ws.ui.model.response.UserRest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/users") // http://localhost:8080/mobile-app-ws/users
//@CrossOrigin(origins= {"http://localhost:8083", "http://localhost:8084"})	//CORS:- Cross Origin Resource Sharing.
public class UserController {
	
	///login is provided automatically by Spring Framework.
	
	@Autowired
	UserService userService;
	
	@Autowired
	AddressService addressesService;
	
	@Autowired
	AddressService addressService;
	
//	@PostAuthorize("hasRole('ADMIN') or returnObject.userId == principal.userId")
	@ApiOperation(value = "The Get User Details Web Service Endpoint", 
			notes="${userController.GetUser.ApiOperation.Notes}")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@GetMapping(path = "/{id}", 
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
	public UserRest getUser(@PathVariable String id) {		//return type was Resource<UserRest>
		
		UserRest returnValue = new UserRest();
		
		UserDto userDto = userService.getUserByUserId(id);
//		BeanUtils.copyProperties(userDto, returnValue);
		ModelMapper modelMapper = new ModelMapper();
		returnValue = modelMapper.map(userDto, UserRest.class);
		
		return returnValue; //changed from > return new Resource<>(returnValue);
	}

	
	@PostMapping(
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
			consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
			)
	public UserRest createUSer(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		UserRest returnValue = new UserRest();
		
		if(userDetails.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		
//		UserDto userDto = new UserDto();
//		BeanUtils.copyProperties(userDetails, userDto);		//BeanUtils does shallow mapping while ModelMapper copies the dependent objects as well
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		userDto.setRoles(new HashSet<>(Arrays.asList(Roles.ROLE_USER.name())));
		
		UserDto createdUser = userService.createUser(userDto);
//		BeanUtils.copyProperties(createdUser, returnValue);
		returnValue = modelMapper.map(createdUser, UserRest.class);
		
		return returnValue;
	}

	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@PutMapping(path="/{id}",
			consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
			)
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		
		UserRest returnValue = new UserRest();
		
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);
		
		UserDto updatedUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updatedUser, returnValue);
		
		return returnValue;
	}

//	@PreAuthorize("hasAuthority('DELETE_AUTHORITY')")		//@PreAuthorize("hasRole('ADMIN')")
//	@Secured("ROLE_ADMIN") //@PreAuthorize("hasRole('ROLE_ADMIN') or #id == principal.id") 
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@DeleteMapping(path="/{id}",
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
			)
	public OperationStatusModel deleteUser(@PathVariable String id) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		
		userService.deleteUser(id);
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}

	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
	public Resources<UserRest> getUsers(@RequestParam(value="page", defaultValue="0") int page, 
			@RequestParam(value="limit", defaultValue="2") int limit){
		
		List<UserRest> returnValue = new ArrayList<UserRest>();
		
		List<UserDto> users = userService.getUsers(page, limit);
		
		Type listType = new TypeToken<List<UserRest>>() {
		}.getType();
		returnValue = new ModelMapper().map(users, listType);
		
//		for (UserDto userDto : users) {
//			UserRest userModel = new UserRest();
//			BeanUtils.copyProperties(userDto, userModel);
//			returnValue.add(userModel);
//		}
		return new Resources<>(returnValue);
	}
	
	//http://localhost:8080/mobile-app-ws/users/{addressID}/addresses
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@GetMapping(path="/{id}/addresses",
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
	public Resources<AddressesRest> getUserAddresses(@PathVariable String id){
		
		List<AddressesRest> addressesListRestModel = new ArrayList<>();
		
		List<AddressDTO> addressesDTO = addressesService.getAddresses(id);
		
		if(addressesDTO != null && !addressesDTO.isEmpty()) {
			java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
			addressesListRestModel = new ModelMapper().map(addressesDTO, listType);
			
			for (AddressesRest addressRest : addressesListRestModel) {
				
				Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId())).withSelfRel();
				
				addressRest.add(addressLink);
				
				Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
				addressRest.add(userLink);
			}
		}
		return new Resources<>(addressesListRestModel);
	}
	

	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
	})
	@GetMapping(path="/{userId}/addresses/{addressId}",
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
	public Resource<AddressesRest> getUserAddress(@PathVariable String userId, 
			@PathVariable String addressId){
		AddressDTO addressesDto = addressService.getAddress(addressId);
		
		ModelMapper modelMapper = new ModelMapper();

//		Link addressLink = linkTo(UserController.class).slash(userId).slash("addresses").slash(addressId).withSelfRel();
//		Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
//		Link addressesLink = linkTo(UserController.class).slash(userId).slash("addresses").withRel("addresses");

		Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();

		Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
		Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");

		AddressesRest addressesRestModel = modelMapper.map(addressesDto, AddressesRest.class);
		
		addressesRestModel.add(addressLink);
		addressesRestModel.add(userLink);
		addressesRestModel.add(addressesLink);
		return new Resource<>(addressesRestModel);

	}
	
	//http://localhost:8080/mobile-app-ws/users/email-verification?token=ssdfaf
	@GetMapping(path = "/email-verification", produces= {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
		
		boolean isVerified = userService.verifyEmailToken(token);
		
		if(isVerified) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		} else {
			returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		}
		
		
		return returnValue;
	}
	
	
//	http://localhost:8080/mobile-app-ws/users/password-reset-request
	@PostMapping(path = "/password-reset-request", 
			produces= {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public OperationStatusModel requestReset (@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		
		boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());
		
		returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		
		if(operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		
		return returnValue;
	}
	
	
	@PostMapping(path="/password-reset", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
		OperationStatusModel returnValue = new OperationStatusModel();
		
		boolean operationResult = userService.resetPassword(
				passwordResetModel.getToken(),
				passwordResetModel.getPassword()
				);
		
		returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		
		if(operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		return returnValue;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
