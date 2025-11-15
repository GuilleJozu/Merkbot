package com.ls.merkbot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/ventas",
                                "/ventas/{id}",
                                "/productos",
                                "/productos/{id}",
                                "/precios",
                                "/precios/{id}"
                        )
                        .authenticated()
                        .requestMatchers(
                                "/ventas/nuevo",
                                "/productos/nuevo",
                                "/precios/nuevo"
                        )
                        .hasAnyRole("ADMINISTRADOR", "JEFE_VENTA", "CAPTURISTA")

                        .requestMatchers(
                                "/ventas/editar/**",
                                "/ventas/eliminar/**",
                                "/productos/editar/**",
                                "/productos/eliminar/**",
                                "/precios/editar/**",
                                "/precios/eliminar/**",
                                "/clientes/**", "/graficas/**"
                        )
                        .hasAnyRole("ADMINISTRADOR", "JEFE_VENTA")
                        .requestMatchers("/usuarios/**").hasAnyRole("ADMINISTRADOR")
                        .requestMatchers("/login", "/css/**", "/js/**", "/img/**").permitAll()
                .anyRequest().authenticated()

        )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }






}




