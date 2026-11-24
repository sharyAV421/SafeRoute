package edu.saferoute.configuracion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.cors.*;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
@Configuration @EnableMethodSecurity
public class SecurityConfig {
 @Value("${saferoute.cors-origin}") private String corsOrigin;
 private final JwtFilter jwtFilter;
 public SecurityConfig(JwtFilter jwtFilter){this.jwtFilter=jwtFilter;}
 @Bean SecurityFilterChain chain(HttpSecurity http)throws Exception {
  http.csrf(c->c.disable()).cors(Customizer.withDefaults()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a.requestMatchers("/actuator/health","/api/auth/login").permitAll()
   .requestMatchers(HttpMethod.GET,"/api/riesgos/niveles").permitAll().anyRequest().authenticated());
  http.addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class);
  return http.build();
 }
 @Bean FilterRegistrationBean<JwtFilter> jwtFilterServletRegistration(JwtFilter filter){
  FilterRegistrationBean<JwtFilter> registration=new FilterRegistrationBean<>(filter);
  registration.setEnabled(false);
  return registration;
 }
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)throws Exception{return configuration.getAuthenticationManager();}
 @Bean CorsConfigurationSource corsConfigurationSource(){
  CorsConfiguration c=new CorsConfiguration();c.addAllowedOrigin(corsOrigin);c.setAllowedMethods(java.util.List.of("GET","POST","PATCH","OPTIONS"));c.setAllowedHeaders(java.util.List.of("Authorization","Content-Type"));
  UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/api/**",c);return s;
 }
}
