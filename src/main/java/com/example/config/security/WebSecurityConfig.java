package com.example.config.security;

import com.example.jwt.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Value("${api.version}")
    private String versionPrefix;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                
                // exception handling 할 때 우리가 만든 클래스 추가
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                
                // Spring Security는 기본적으로 세션을 사용한다.
                // 여기선 세션을 사용하지 않기에 세션 설정을 Stateless로 설정
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 로그인, 회원가입 API는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll 설정
                .and()
                .authorizeRequests()
                    .antMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**").permitAll()
                    .antMatchers(versionPrefix + "/member/signup").permitAll()
                    .antMatchers(versionPrefix + "/member/login").permitAll()
                    .antMatchers(versionPrefix + "/member/reissue").permitAll()
                    .antMatchers(versionPrefix + "/member/logout").permitAll()
                    .anyRequest().authenticated() // 나머지는 전부 인증 필요
                // JwtFilter 를 addFilterBefore로 등록했던 JwtSecurityConfig 클래스를 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider))

                // form 기반 로그인 비활성화
                .and()
                .formLogin().disable();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
