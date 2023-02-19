package com.ironhack.demosecurityjwt.security;

import com.ironhack.demosecurityjwt.filters.CustomAuthenticationFilter;
import com.ironhack.demosecurityjwt.filters.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * This is the main configuration class for security in the application. It enables web security,
 * sets up the password encoder, and sets up the security filter chain.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    // UserDetailsService is an interface provided by Spring Security that defines a way to retrieve user information
    @Autowired
    private UserDetailsService userDetailsService;

    // Autowired instance of the AuthenticationManagerBuilder
    @Autowired
    private AuthenticationManagerBuilder authManagerBuilder;

    /**
     * Bean definition for PasswordEncoder
     *
     * @return an instance of the DelegatingPasswordEncoder
     */
    @Bean
    public PasswordEncoder encoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Bean definition for AuthenticationManager
     *
     * @param authenticationConfiguration the instance of AuthenticationConfiguration
     * @return an instance of the AuthenticationManager
     * @throws Exception if there is an issue getting the instance of the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Bean definition for SecurityFilterChain
     *
     * @param http the instance of HttpSecurity
     * @return an instance of the SecurityFilterChain
     * @throws Exception if there is an issue building the SecurityFilterChain
     */
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CustomAuthenticationFilter instance created
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authManagerBuilder.getOrBuild());
        // set the URL that the filter should process
        customAuthenticationFilter.setFilterProcessesUrl("/api/login");
        // disable CSRF protection
        http.csrf().disable();
        // set the session creation policy to stateless
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        // set up authorization for different request matchers and user roles
        http.authorizeHttpRequests((requests) -> requests
               // .requestMatchers("/api/login/**").permitAll()
               // .requestMatchers(GET, "/api/users").hasAnyAuthority("ROLE_USER")
                //.requestMatchers(POST, "/api/users").hasAnyAuthority("ROLE_ADMIN")
                //Admins should be able to access the balance for any account and to modify it. - understanding they can do ALL methods of accounts and transactions.
//                .requestMatchers(GET, "/bank/accounts/**").hasAnyAuthority("ROLE_ADMIN")
//                .requestMatchers(POST, "/bank/accounts/**").hasAnyAuthority("ROLE_ADMIN")
//                .requestMatchers(GET, "/accounts/**").hasAnyAuthority("ROLE_ADMIN")
//                .requestMatchers(POST, "/accounts/**").hasAnyAuthority("ROLE_ADMIN")
//                .requestMatchers(GET, "/bank/**").hasAnyAuthority("ROLE_ADMIN")
//                .requestMatchers(POST, "/bank/**").hasAnyAuthority("ROLE_ADMIN")
//                //Account Holders accesses
         //       .requestMatchers(GET, "/accounts/**").hasAnyAuthority("ROLE_ACCOUNT_HOLDER")
//                .requestMatchers(POST, "/accounts/transfer").hasAuthority("ROLE_ACCOUNT_HOLDER")
//                //Third party users
//                .requestMatchers(POST,"/accounts/transfer").hasAuthority("ROLE_THIRD_PARTY")
//                .requestMatchers(GET, "/accounts/**").hasAuthority("ROLE_THIRD_PARTY")
                //.anyRequest().authenticated())
               // .anyRequest().denyAll());
                .anyRequest().permitAll());
        // add the custom authentication filter to the http security object
        http.addFilter(customAuthenticationFilter);
        // Add the custom authorization filter before the standard authentication filter.
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        // Build the security filter chain to be returned.
        return http.build();
    }
}
