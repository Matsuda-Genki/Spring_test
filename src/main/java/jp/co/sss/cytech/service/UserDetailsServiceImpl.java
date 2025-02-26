package jp.co.sss.cytech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jp.co.sss.cytech.entity.CustomUserDetails;
import jp.co.sss.cytech.entity.UserEntity;
import jp.co.sss.cytech.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません"));

        // ダミーの権限を付与（データベース変更不要）
        return new CustomUserDetails(user); // 固定値で設定
    }
}
