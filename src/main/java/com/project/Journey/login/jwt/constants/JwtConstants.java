package com.project.Journey.login.jwt.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtConstants {
    public static final String SECRET_KEY = "ThisIsServerSecretKey"; // 임시
    public static final long ACCESS_EXP_TIME = 60000 * 1; // 1분 설정
    public static final long REFRESH_EXP_TIME = 60000 * 5; // 5분 설정

    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_TYPE = "Bearer";

    public static final String ACCESS = "AccessToken";
    public static final String REFRESH = "RefreshToken";
}
