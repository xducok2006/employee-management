package com.example.employee_manage_project.config;

import com.example.employee_manage_project.entity.Role;
import com.example.employee_manage_project.exception.CustomAccessDeniedHandler;
import com.example.employee_manage_project.exception.CustomAuthenticationEntryPoint;
import com.example.employee_manage_project.repository.RoleRepository;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${jwt.base64-secret}")
    private String SIGN_KEY;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final RoleRepository roleRepository;
    @Bean
    public PasswordEncoder passwordEncoder()
    {

        return new BCryptPasswordEncoder();
    }
    private SecretKey getSecretKey()
    {
        byte[] keyByte = Base64.getDecoder().decode(SIGN_KEY);
        return new SecretKeySpec(keyByte,0,keyByte.length,"HmacSHA256");
    }
    private void addRolePermission(String roleName, List<GrantedAuthority> authorities)
    {
        Role role = roleRepository.findByNameWithPermissions(roleName).orElseThrow(() ->
            new UsernameNotFoundException("Role not found: " + roleName));

        authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getName()));

        role.getPermissions().forEach(permission -> authorities.add(new
                SimpleGrantedAuthority(permission.getName())));
    }
    @Bean
    public JwtAuthenticationConverter converter()
    {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt-> {
            List<GrantedAuthority> authorities = new ArrayList<>();
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles != null) {
                roles.forEach(roleName ->
                        addRolePermission(roleName, authorities)
                );
            }
            return authorities;
        });
        return converter;
    }
    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        return request -> request.getHeader("Token");
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity)
    {
        httpSecurity.authorizeHttpRequests(request ->
                request.requestMatchers("/api/login","/api/register","/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**","/test-redisson","/test-redisson/watchdog").permitAll()
                .anyRequest().authenticated()).
                oauth2ResourceServer(oauth2->
                oauth2.bearerTokenResolver(bearerTokenResolver()).jwt(jwt->jwt.decoder(jwtDecoder()).jwtAuthenticationConverter(converter()))).exceptionHandling(
                        exception ->
                                exception.authenticationEntryPoint(customAuthenticationEntryPoint).accessDeniedHandler(customAccessDeniedHandler));
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }


    @Bean
    public JwtDecoder jwtDecoder()
    {
        return NimbusJwtDecoder.withSecretKey(getSecretKey()).macAlgorithm(MacAlgorithm.HS256).build();
    }
    @Bean
    public JwtEncoder jwtEncoder()
    {

        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }


}
