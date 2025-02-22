package jp.co.sss.cytech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jp.co.sss.cytech.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCryptでパスワードをハッシュ化
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(auth -> auth
                .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**").permitAll() // ログインと登録ページは全員アクセス可能
                .anyRequest().authenticated() // 他のリクエストは認証が必要
            )
            .formLogin(login -> login
                .loginPage("/login") // ログインページのURLを指定
                .loginProcessingUrl("/login") // ログインフォームのPOSTリクエストを処理
                .defaultSuccessUrl("/", true) // 明示的にリダイレクト先を指定
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")	
                .permitAll()
            )
           	.sessionManagement(session -> session
                .maximumSessions(1)  // 同時に1セッションまでしか許可しない設定（オプション）
                .expiredUrl("/login?expired")  // セッションが期限切れの場合にリダイレクトするURL
            );
        return http.build(); // セキュリティフィルターチェーンを返す
    }
    
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
            .and()
            .build();
    }
}
