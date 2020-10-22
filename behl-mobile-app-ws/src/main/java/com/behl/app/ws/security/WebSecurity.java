package com.behl.app.ws.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.behl.app.ws.io.repositories.UserRepository;
import com.behl.app.ws.service.UserService;

@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled=true) //This can also be used with other class with @Configuration. securedEnabled = true enables @Secured. prePostEnabled enables @Preauthorize & @PostAuthorize.
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

	private final UserService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserRepository userRepository;
	
	public WebSecurity(UserService userDetailsService, BCryptPasswordEncoder bcryptPasswordEncoder, UserRepository userRepository) {
		this.userDetailsService = userDetailsService;
		this.bCryptPasswordEncoder = bcryptPasswordEncoder;
		this.userRepository = userRepository;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.cors()
		.and()
		.csrf().disable().authorizeRequests()
		.antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL)
		.permitAll()
		.antMatchers(HttpMethod.GET, SecurityConstants.VERIFICATION_EMAIL_URL)
		.permitAll()
		.antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_RESET_REQUEST_URL)
		.permitAll()
		.antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_RESET_URL)
		.permitAll()
		.antMatchers(SecurityConstants.H2_CONSOLE)
		.permitAll()
		.antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**")
		.permitAll()
//		.antMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("DELETE_AUTHORITY")		//hasRole("ADMIN") or  or .hasAnyAuthority("DELETE_AUTHORITY", "DELETE_ALL_AUTHORITY") or .hasAnyRole("ADMIN", "USER")
		.anyRequest().authenticated().and()
		.addFilter(getAuthenticationFilter())
		.addFilter(new AuthorizationFilter(authenticationManager(), userRepository))
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		http.headers().frameOptions().disable();	//to enable h2 console open in web browser by disabling frame options http header which will prevent .
//		we disable frame options http header which prevents the browser to load your page in html tags like iframe/frame etc.
//		to enable h2 console open up in browser window we will need to disable this option.
//		DONT USE THIS OPTION ON PRODUCTION DEPLOYMENT!!!
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}
	
	public AuthenticationFilter getAuthenticationFilter() throws Exception{
		final AuthenticationFilter filter = new AuthenticationFilter(authenticationManager());
		filter.setFilterProcessesUrl("/users/login");
		return filter;
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
		configuration.setAllowCredentials(true); //creds like auth headers, cookies, SSL client certs etc.
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
		
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}


















