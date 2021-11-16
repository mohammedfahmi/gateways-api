package com.musala.gatewaysapi.configuration.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import javax.annotation.Nonnull;

/**
 * Configuration class for web security
 */
@Configuration
@EnableWebSecurity
@Setter
@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "endpoint.security.user")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final String REQUEST_WILDCARD = "/**";
    private String name;
    private String password;
    private String role;
    private String managementContext;

    private static final String NOOP_ENCODING = "{noop}";

    @SuppressWarnings("RedundantThrows")
    @Override
    public void configure(@Nonnull final WebSecurity web) throws Exception {
        web.ignoring().antMatchers(managementContext + REQUEST_WILDCARD);
    }

    @Override
    protected void configure(@Nonnull final AuthenticationManagerBuilder authenticationBuilder) throws Exception {
        authenticationBuilder
                .inMemoryAuthentication()
                .withUser(name)
                .password(NOOP_ENCODING + password)
                .roles(role);
    }

    @Override
    protected void configure(@Nonnull final HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().hasRole(role)
                .and()
                .httpBasic();

        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

}
