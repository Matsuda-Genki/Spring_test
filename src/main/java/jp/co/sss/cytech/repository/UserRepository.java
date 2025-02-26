package jp.co.sss.cytech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.cytech.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer>{
	//	ユーザー名検索
	Optional<UserEntity> findByUsername(String username);
	
	// ユーザー名の存在チェック
    boolean existsByUsername(String username);
    
    // メールアドレスの存在チェック
    boolean existsByEmail(String email);
}
