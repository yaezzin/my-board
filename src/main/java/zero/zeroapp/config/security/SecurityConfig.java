package zero.zeroapp.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import zero.zeroapp.service.sign.TokenService;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenService tokenService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/exception/**",
                "/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() // http basic 인증방식 비활성화
                .formLogin().disable() // form login 비활성화
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 유지 x
                .and()

                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/posts").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/posts/{id}").access("@postGuard.check(#id)")
                .antMatchers(HttpMethod.DELETE, "/api/posts/{id}").access("@postGuard.check(#id)")
                .antMatchers(HttpMethod.GET, "/image/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/sign-in", "/api/sign-up", "/api/refresh-token").permitAll()
                .antMatchers(HttpMethod.POST, "/api/posts").authenticated()
                .antMatchers(HttpMethod.GET, "/api/**").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/members/{id}/**").access("@memberGuard.check(#id)")
                .anyRequest().hasAnyRole("ADMIN")

                // "@<빈이름>.<메소드명>(<인자, #id로하면 URL에 지정한 {id}가 매핑되어서 인자로 들어감>)"
                // 스프링 빈으로 등록한 MemberGuard.check()를 호출하고, 반환 값이 true라면 접근을 허용/
                // 그 외의 요청들은, 모두 관리자 권한이 필요

                .and()
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler()) // 사용자의 권한 거부시 해당 핸들러 작동

                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // 인증되지 않은 사용자의 접근 시 해당 핸들러 작동

                .and() // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter의 이전 위치에 등록
                .addFilterBefore(new JwtAuthenticationFilter(tokenService, userDetailsService), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // PasswordEncoder의 구현체로 DelegatingPasswordEncoder를 사용
    }

}
