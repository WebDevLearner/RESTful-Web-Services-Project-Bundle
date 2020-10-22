package com.behl.app.ws.ui.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.behl.app.ws.ui.model.request.LoginRequestModel;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

@RestController
public class AuthenticationController {
	
	@ApiOperation("User Login")
	@ApiResponses(value = {
	@ApiResponse(code = 200, 
					message = "Response Headers",
					responseHeaders = {
							@ResponseHeader(name = "authorization", 
									description = "Bearer <JWT value here>", 
									response = String.class),
							@ResponseHeader(name="userId",
								description="<Public User Id value here>",
								response = String.class)
					
	})
	})
	@PostMapping("/users/login")
	public void theFakeLogin(@RequestBody LoginRequestModel loginRequestModel) {
	/**ideally this method will not be called when http POST request is sent to /users/login.
	 * The spring framework will override the request for /users/login and will use its own login web service to respond.
	 * 
	 */
		throw new IllegalStateException("This method should not be called. This method is implemneted by Spring Security.");
	}
}
