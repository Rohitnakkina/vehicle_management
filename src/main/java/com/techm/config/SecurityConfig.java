package com.techm.config;

import com.techm.entity.ServiceAdvisor;
import com.techm.repository.ServiceAdvisorRepository;
import com.techm.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetails customUserDetails;
    
    @Autowired
    private ServiceAdvisorRepository serviceAdvisorRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	
    	
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin-login","/admin-register", "/css/**").permitAll()
                .requestMatchers("/admin/**").hasRole("Admin")
                .requestMatchers("/advisor/**").hasRole("ServiceAdvisor")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/admin-login")
                .loginProcessingUrl("/admin-login")
                .successHandler((request, response, authentication) -> {
                    String role = authentication.getAuthorities().stream()
                        .findFirst()
                        .map(auth -> auth.getAuthority())
                        .orElse("");

                    if ("ROLE_Admin".equals(role)) {
                        response.sendRedirect("/admin/dashboard");
                    } else if ("ROLE_ServiceAdvisor".equals(role)) {
                        // Get the logged-in user's email
                        String email = authentication.getName(); 

                        // Fetch the Service Advisor ID using email
                        ServiceAdvisor advisor = serviceAdvisorRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Service Advisor not found"));

                        // Redirect to Service Advisor dashboard with their ID
                        response.sendRedirect("/advisor/dashboard/" + advisor.getId());
                    } else {
                        response.sendRedirect("/admin-login?error");
                    }
                })                .failureUrl("/admin-login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/admin-login?logout")
                .invalidateHttpSession(true) // Invalidate session
                .deleteCookies("JSESSIONID") // Clear session cookie
                .permitAll()
            );
        return http.build();
    }
}


