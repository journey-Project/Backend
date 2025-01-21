# Base 이미지로 톰캣 사용
FROM tomcat:9.0

# 톰캣의 웹앱 디렉토리 설정
WORKDIR /usr/local/tomcat/webapps

# WAR 파일 복사
COPY build/libs/Journey-0.0.1-SNAPSHOT.war Journey-0.0.1-SNAPSHOT.war

# 톰캣 서버 실행
CMD ["catalina.sh", "run"]