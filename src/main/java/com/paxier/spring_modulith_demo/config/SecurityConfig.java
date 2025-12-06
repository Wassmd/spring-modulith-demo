package com.paxier.spring_modulith_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
//  private String issuerUri;
//
//  @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
//  private String jwkSetUri;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/actuator/**",
                "/apidoc/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwtConfigurer -> jwtConfigurer
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
//                .decoder(jwtDecoder())
            )
        );

    return http.build();
  }

//  @Bean
//  public JwtDecoder jwtDecoder() {
//    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
//
//    OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator("spring-modulith-demo-admin");
//    OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
//    OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
//
//    jwtDecoder.setJwtValidator(withAudience);
//    return jwtDecoder;
//  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(jwt -> createGrantedAuthorities(jwt.getClaims()));
    return converter;
  }

  private Collection<GrantedAuthority> createGrantedAuthorities(Map<String, Object> claims) {
    Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
    List<String> roles = (List<String>) realmAccess.get("roles");
    return roles.stream()
        .map("ROLE_"::concat)
        .map(SimpleGrantedAuthority::new)
        .map(GrantedAuthority.class::cast)
        .toList();
  }

  /**
     * Custom validator to check JWT audience claim
     */
//    record AudienceValidator(String audience) implements OAuth2TokenValidator<Jwt> {
//
//    @Override
//      public OAuth2TokenValidatorResult validate(Jwt jwt) {
//        List<String> audiences = jwt.getAudience();
//        if (audiences.contains(this.audience)) {
//          return OAuth2TokenValidatorResult.success();
//        }
//        OAuth2Error error = new OAuth2Error(
//            "invalid_token",
//            "The required audience '" + this.audience + "' is missing",
//            null
//        );
//        return OAuth2TokenValidatorResult.failure(error);
//      }
    //}

}

