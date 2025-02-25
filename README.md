# 📌 Delivery 9zo

## 📖 프로젝트 목적 및 상세
Delivery 9zo는 **광화문 근처 음식점들의 배달 및 포장 주문 관리, 결제, 그리고 주문 내역 관리** 기능을 제공하기 위해 개발되었습니다.  
사용자는 온라인 주문과 매장 내 직접 주문을 모두 받을 수 있으며, 상품 등록 시 **AI 문구 추천** 기능을 활용할 수 있습니다.  
또한, 초기에는 광화문 근처로 한정하여 운영되지만 **향후 확장성을 고려한 지역 분류 시스템**을 설계할 예정입니다.

### 주요 기능
- **주문 관리**: 배달 및 포장 주문을 관리하고 주문 상태를 실시간으로 업데이트
- **결제 관리**: 결제 정보 처리 및 주문 내역 확인
- **상품 관리**: 상품 등록 및 수정, AI 기반 문구 추천 기능 포함
- **사용자 관리**: 고객 타입에 따른 권한 분리 (가게, 손님, 관리자)
- **지역 관리 및 확장성**: 광화문 근처 지역을 시작으로 향후 확장이 가능하도록 설계



## 👥 팀원 역할분담
| 이름   | 역할           | 담당 업무                           |
|--------|----------------|-------------------------------------|
| 김기훈 👑 | 팀장/백엔드 개발 | 상점, 카테고리                       |
| 박종민 | 백엔드 개발      | 보안/유저, AI, 주문상세               |
| 최해인 | 백엔드 개발      | CI/CD, 리뷰, 메뉴                    |
| 김민지 | 백엔드 개발      | 주문, 결제                          |



## 🔧 기술 스택

### 백엔드
![Java](https://img.shields.io/badge/java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white)
![Spring Boot](https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=Spring%20Security&logoColor=white)

### 데이터베이스
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)

### API 문서화
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)

### CI/CD 및 배포
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Nginx](https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=GitHub%20Actions&logoColor=white)
![Amazon EC2](https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=Amazon%20EC2&logoColor=white)

### 협업 및 프로젝트 관리
![Jira](https://img.shields.io/badge/jira-%230A0FFF.svg?style=for-the-badge&logo=jira&logoColor=white)

### 빌드 및 의존성 관리
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)



## 🚀 서비스 구성 및 실행 방법

### 배포 스크립트 
```bash
#!/usr/bin/env bash
DEPLOY_PATH=/home/ec2-user/app/
DOCKER_COMPOSE_NAME=docker-compose.yml
docker compose -f $DEPLOY_PATH$DOCKER_COMPOSE_NAME up --build -d
```

### Yaml
```yaml
version: 0.0
os: linux

files:
  - source: /
    destination: /home/ec2-user/app

hooks:
  AfterInstall:
    - location: deploy.sh
      timeout: 60
      runas: root
```



## 🗃️ ERD (Entity Relationship Diagram)
아래는 주요 테이블 간의 관계를 나타낸 ERD입니다.

![image](https://github.com/user-attachments/assets/873de1c7-7f69-4f2c-8cbc-66231a4c936e)




## 🗃️ 서비스 아키텍처 (Service Architecture)
서비스 전체 아키텍처는 다음과 같습니다.

![image](https://github.com/user-attachments/assets/528962e7-b511-49ea-9eb2-4f8720e6ef8a)


- **API Gateway**: Nginx를 사용하여 클라이언트 요청 라우팅
- **Application Server**: Spring Boot 애플리케이션 서버
- **Database**: PostgreSQL
- **CI/CD Pipeline**: GitHub Actions 및 Docker를 통한 배포 자동화



## 📚 API 문서 (Swagger)
- **Swagger**: 추후 업데이트 예정
- **Jira Board**: [Jira 보기](https://develop-cloud.atlassian.net/jira/software/projects/SCRUM/boards/1/timeline)



