# 루하

Spring Boot 기반 Todo 관리 애플리케이션

## 환경 설정

### 1. 필수 설정 파일 생성

프로젝트 실행 전 민감정보 설정이 필요합니다.

#### application-secret.yml 파일 생성

```bash
# 템플릿 파일 복사
cp src/main/resources/application-secret.yml.example src/main/resources/application-secret.yml
```

`src/main/resources/application-secret.yml` 파일을 열어서 실제 값으로 수정:

```yaml
spring:
  datasource:
    password: 1234  # MySQL 비밀번호

jwt:
  secret-key: VmFsdWVCdWdHZXR0ZXJzQW5kU2V0dGVyc1ZhbHVlQnVnR2V0dGVyc0FuZFNldHRlcnM=  # JWT 비밀키
```

### 2. JWT Secret Key 생성 (선택사항)

새로운 비밀키가 필요한 경우:

```bash
# OpenSSL 사용
openssl rand -base64 64 | tr -d '\n'
```

### 3. 필수 환경변수

| 변수명 | 필수 | 설명 | 기본값 |
|--------|------|------|--------|
| `DB_PASSWORD` | ⭐ | MySQL 비밀번호 | - |
| `JWT_SECRET_KEY` | ⭐ | JWT 서명 비밀키 (Base64) | - |
| `DB_URL` | | 데이터베이스 URL | `jdbc:mysql://localhost:3306/ruhadb` |
| `DB_USERNAME` | | 데이터베이스 사용자명 | `root` |

> **참고**: `application-secret.yml`을 사용하면 환경변수 설정 불필요

## 실행 방법

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun
```

서버가 `http://localhost:8080` 에서 실행됩니다.

## 데이터베이스 설정

- **운영**: MySQL `ruhadb` (localhost:3306)
- **테스트**: H2 인메모리 데이터베이스

MySQL 데이터베이스 생성:
```sql
CREATE DATABASE ruhadb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

