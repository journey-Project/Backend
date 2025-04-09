//package com.project.Journey.login.jwt.repository;
//
//import com.project.Journey.login.jwt.constants.JwtConstants;
//import com.project.Journey.login.jwt.domain.RefreshToken;
//import org.springframework.data.redis.core.ValueOperations;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Repository;
//
//import java.util.Objects;
//import java.util.Optional;
//import java.util.concurrent.TimeUnit;
//
//
//@Repository
//@RequiredArgsConstructor
//public class JwtRepository {
//
//    private final RedisTemplate redisTemplate;
//
//    public RefreshToken save(RefreshToken refreshToken){
//        ValueOperations valueOperation = redisTemplate.opsForValue();
//        valueOperation.set(refreshToken.getToken(), refreshToken.getMemberId());
//        redisTemplate.expire(refreshToken.getToken(), JwtConstants.REFRESH_EXP_TIME, TimeUnit.MILLISECONDS);
//        return refreshToken;
//    }
//
//    public Optional<RefreshToken> findByToken(String refreshToken) {
//        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
//        String userId = valueOperations.get(refreshToken);
//
//        if(Objects.isNull(userId)){
//            return Optional.empty();
//        }
//        return Optional.of(new RefreshToken(refreshToken, userId));
//    }
//
//}
