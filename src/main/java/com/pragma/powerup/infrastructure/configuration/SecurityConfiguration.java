package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.infrastructure.security.JwtAccessDeniedHandler;
import com.pragma.powerup.infrastructure.security.JwtAuthenticationEntryPoint;
import com.pragma.powerup.infrastructure.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String ENDPOINT_RESTAURANT_CREATE = "/api/v1/restaurant";
    private static final String ENDPOINT_RESTAURANT_LIST = "/api/v1/restaurant";
    private static final String ENDPOINT_RESTAURANT_IS_OWNER = "/api/v1/restaurant/*/is-owner";
    private static final String ENDPOINT_DISH_CREATE = "/api/v1/dish";
    private static final String ENDPOINT_DISH_UPDATE = "/api/v1/dish/**";
    private static final String ENDPOINT_DISH_LIST = "/api/v1/dish/restaurant/*";
    private static final String ENDPOINT_ORDER_CREATE = "/api/v1/order";
    private static final String ENDPOINT_ORDER_LIST = "/api/v1/order";
    private static final String ENDPOINT_ORDER_ASSIGN = "/api/v1/order/*/assign";
    private static final String ENDPOINT_ORDER_READY = "/api/v1/order/*/ready";
    private static final String ENDPOINT_ORDER_CANCEL = "/api/v1/order/*/cancel";

    private static final String SWAGGER_API_DOCS_PATH = "/v3/api-docs/**";
    private static final String SWAGGER_UI_PATH = "/swagger-ui/**";
    private static final String SWAGGER_HTML_PATH = "/swagger-ui.html";

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_OWNER = "OWNER";
    private static final String ROLE_CUSTOMER = "CUSTOMER";
    private static final String ROLE_EMPLOYEE = "EMPLOYEE";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
                .authorizeHttpRequests(auth -> auth
                        .antMatchers(SWAGGER_API_DOCS_PATH, SWAGGER_UI_PATH, SWAGGER_HTML_PATH).permitAll()
                        .antMatchers(HttpMethod.GET, ENDPOINT_RESTAURANT_LIST).hasRole(ROLE_CUSTOMER)
                        .antMatchers(HttpMethod.GET, ENDPOINT_DISH_LIST).hasRole(ROLE_CUSTOMER)
                        .antMatchers(HttpMethod.POST, ENDPOINT_RESTAURANT_CREATE).hasRole(ROLE_ADMIN)
                        .antMatchers(HttpMethod.GET, ENDPOINT_RESTAURANT_IS_OWNER).hasRole(ROLE_OWNER)
                        .antMatchers(HttpMethod.POST, ENDPOINT_DISH_CREATE).hasRole(ROLE_OWNER)
                        .antMatchers(HttpMethod.PATCH, ENDPOINT_DISH_UPDATE).hasRole(ROLE_OWNER)
                        .antMatchers(HttpMethod.POST, ENDPOINT_ORDER_CREATE).hasRole(ROLE_CUSTOMER)
                        .antMatchers(HttpMethod.GET, ENDPOINT_ORDER_LIST).hasRole(ROLE_EMPLOYEE)
                        .antMatchers(HttpMethod.PATCH, ENDPOINT_ORDER_ASSIGN).hasRole(ROLE_EMPLOYEE)
                        .antMatchers(HttpMethod.PATCH, ENDPOINT_ORDER_READY).hasRole(ROLE_EMPLOYEE)
                        .antMatchers(HttpMethod.PATCH, ENDPOINT_ORDER_CANCEL).hasRole(ROLE_CUSTOMER)
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
