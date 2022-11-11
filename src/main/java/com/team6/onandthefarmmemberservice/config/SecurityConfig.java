package com.team6.onandthefarmmemberservice.config;

import com.team6.onandthefarmmemberservice.repository.UserRepository;
import com.team6.onandthefarmmemberservice.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity // 시큐리티 활성화 -> 기본 스프링 필터체인에 등록
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter){
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable() // csrf 미적용
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 사용 하지않음
                .and()
                .authorizeRequests()
                .antMatchers("/api/user/members/login", "/api/user/members/refresh", "/api/user/members/login/phone").permitAll()
                .antMatchers("/api/seller/members/login", "/api/seller/members/refresh", "/api/seller/members/signup", "/api/seller/members/email", "/api/seller/members/emailConfirm", "/api/seller/members/search/id", "/api/seller/members/search/passwd", "/api/seller/members/passwd").permitAll()
                .antMatchers("/api/admin/members/refresh").permitAll()
                .antMatchers("/api/user/product/list/**", "/api/user/product/{\\d+}", "/api/user/product/QnA/{\\d+}").permitAll()
                .antMatchers("/api/user/review/info", "/api/user/review/list/**").permitAll()
                .antMatchers("/api/seller/product/list/**").permitAll()
                .antMatchers("/api/user/**").hasAnyRole("USER", "SELLER")
                .antMatchers("/api/seller/**").hasRole("SELLER")
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .antMatchers("/api/feign/**").permitAll()
                .anyRequest().permitAll()
                .and().cors();
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // controller 시작 전에 jwt 인증을 하기 위한 필터 등록

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        //configuration.addAllowedOrigin("http://54.180.119.61:3000");
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}