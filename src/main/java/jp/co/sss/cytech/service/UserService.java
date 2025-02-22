package jp.co.sss.cytech.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.sss.cytech.DTO.UserProfileDTO;
import jp.co.sss.cytech.entity.UserEntity;
import jp.co.sss.cytech.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // プロフィール情報取得
    public UserProfileDTO getProfile(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToProfileDTO)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません"));
    }

    // プロフィール更新
    @Transactional
    public void updateProfile(String username, UserProfileDTO dto) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません"));
        
        updateEntityFromDTO(user, dto);
        userRepository.save(user);
    }

    // Entity → DTO変換
    private UserProfileDTO convertToProfileDTO(UserEntity entity) {
        return UserProfileDTO.builder()
                .username(entity.getUsername())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .userAddress(entity.getUserAddress())
                .build();
    }

    // DTO → Entity更新
    private void updateEntityFromDTO(UserEntity entity, UserProfileDTO dto) {
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setUserAddress(dto.getUserAddress());
    }
}
