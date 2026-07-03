package cl.ucm.taller.tallermecanico.config;

import cl.ucm.taller.tallermecanico.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) // Activa CorsConfig
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Reglas según Rúbrica (Página 3)
                .requestMatchers("/auth/**").permitAll()
                // Datos exclusivos del jefe (ADMIN): métricas del negocio y gestión de
                // usuarios. Van ANTES de la regla general de GET para tener prioridad.
                // Las órdenes SÍ las ve la secretaria (USER), por eso no están aquí.
                .requestMatchers(HttpMethod.GET, "/api/dashboard/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/reservas/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
            )
            // Sin esto, Spring Security responde 403 también cuando NO hay token.
            // La rúbrica exige diferenciar: sin token -> 401, token con rol insuficiente -> 403.
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(
                        "{\"message\":\"No autenticado: se requiere un token válido\",\"statusCode\":401,\"uriRequested\":\"" + request.getRequestURI() + "\"}"
                    );
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(
                        "{\"message\":\"Acceso denegado: su rol no tiene permisos para esta operación\",\"statusCode\":403,\"uriRequested\":\"" + request.getRequestURI() + "\"}"
                    );
                })
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
