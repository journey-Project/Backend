# 1. 베이스 이미지 설정 (Java 21)
FROM openjdk:21-jdk

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. WAR 파일 복사
COPY build/libs/Journey-0.0.1-SNAPSHOT.war Journey.war

# 4. 포트 노출
EXPOSE 8080

# 5. 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "Journey.war"]
