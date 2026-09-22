package in.gov.dilrmp.configs;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;



import jakarta.servlet.http.Cookie;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class UserSecurityConfiguration {




        @Autowired
        private UserDetailsService userDetailsService;
        @Autowired
        private LoginSucessfullHandler loginSuccessHandler;
        @Autowired
        private AuthenticationFailureHandler customAuthenticationFailureHandler;
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public HttpFirewall configureFirewall() {
                StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
                strictHttpFirewall.setAllowedHttpMethods(Arrays.asList("GET", "POST", "HEAD"));
                return strictHttpFirewall;
        }



        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

            http.csrf(csrf -> csrf
                            .disable())
//                    .headers(headers -> headers
//                            // Strict Transport Security (HSTS)
//                            .httpStrictTransportSecurity(hstsConfig -> hstsConfig
//                                    .maxAgeInSeconds(HSTS_MAX_AGE)
//                                    .includeSubDomains(true)
//                                    .preload(true)
//                            )
//                            // Content Security Policy (CSP)
//                            .contentSecurityPolicy(cspConfig -> cspConfig
//                                    .policyDirectives(CSP_POLICY)
//                            )
//                            // Additional Security Headers
//                            .frameOptions(frameOptions -> frameOptions.deny())
//                            .contentTypeOptions(contentTypeOptions -> {})
//                            .referrerPolicy(referrerPolicy -> referrerPolicy.policy(REFERRER_POLICY))
//                    )
                    .sessionManagement(management -> management
                            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                            .sessionFixation().migrateSession()
                            .maximumSessions(1)
                            .sessionRegistry(sessionRegistry())
                            .maxSessionsPreventsLogin(true)
                            .expiredUrl("/login?sessionExpired")
                    )
                    .authorizeHttpRequests(requests -> requests
                                                .requestMatchers("/userProfile/contact-us").permitAll()
                                                .requestMatchers("/userProfile/about-us").permitAll()
                                                .requestMatchers("/userProfile/forget-password").permitAll()
                                                .requestMatchers("/userProfile/login").permitAll()
                                                .requestMatchers("/reports/**").permitAll()
                                                .requestMatchers("/current-status/**","/chart/**","/physical/report/**").permitAll()



                                                .requestMatchers("/","/favicon.ico","/error","/dashboard-chart","/getDashboardData","/getStateData","/getDocument", "/loginPage","/check-login","/getToken","/static/**", "/user/create-igr-user",
                                                                "/captcha-servlet", "/PhyscialComponent/**","/api/chart/**","/physicalProgressReports/**","/physcial/report/**","/css/**","/js/**","/plugins/**","/naksha-dashboard",

                                                                "/dilrmp/**", "/images/**","/dashboard/**","/master/report/pdf/**","/PhyscialComponent/CLR/**","/topandbottom/report/**")
                                                .permitAll()

                                                .requestMatchers("/user/create-top-level-user").permitAll()
                                                .requestMatchers("/token/**", "/verifyCaptcha/**")
                                                .permitAll()
                                                .requestMatchers("/dolr/**")
                                                .hasAnyRole("DOLR","ADMIN","SGO")
                                                .requestMatchers("/state/**")
                                                .hasAnyRole("STATE","DISTRICT","ULB","STATEULB","GD","SGO","ADC")
                                                .anyRequest()
                                                .authenticated())
                                                .addFilterBefore(new CustomFilter(), UsernamePasswordAuthenticationFilter.class)
                                .formLogin(form -> form
                                        .loginPage("/loginPage")
                                                .loginProcessingUrl("/login")
                                                .successHandler(loginSuccessHandler)
                                                .failureHandler(customAuthenticationFailureHandler)
                                                .permitAll()
                                )
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/?logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .clearAuthentication(true)
                                                .addLogoutHandler((request, response, authentication) -> {
                                                                        // Manually set the HTTP-only and Secure attributes for the cookie
                                                                        Cookie cookie = new Cookie(
                                                                                        "JSESSIONID",
                                                                                        null);
                                                                        cookie.setPath("/");
                                                                        cookie.setMaxAge(0);
                                                                        cookie.setHttpOnly(true);
                                                                        cookie.setSecure(true); // Set the Secure flag for the cookie
                                                                        response.addCookie(cookie);
                                                                        // Remove the session from
                                                                        // SessionRegistry
                                                                        sessionRegistry()
                                                                                        .removeSessionInformation(
                                                                                                        request.getSession()
                                                                                                                        .getId());
                                                                })
                                                .permitAll()
                                )
                    .exceptionHandling(ex -> ex
                            .authenticationEntryPoint((request, response, authException) -> {
                                String xhrHeader = request.getHeader("X-Requested-With");
                                if ("XMLHttpRequest".equals(xhrHeader)) {
                                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                                } else {
                                    String contextPath = request.getContextPath();
                                    response.sendRedirect(contextPath + "/loginPage");
                                }
                            })
                    );
            http.userDetailsService(userDetailsService);
            return http.build();

        }



        @Bean
        public SessionRegistry sessionRegistry() {
                return new SessionRegistryImpl();
        }

}
