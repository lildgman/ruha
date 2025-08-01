# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Ruha is a Spring Boot application for managing todos with social features (following system). Built with Spring Data JPA, MySQL, and JWT authentication.

## Common Commands

### Build & Run
```bash
./gradlew build          # Build the project
./gradlew bootRun        # Run the application
./gradlew test           # Run tests
./gradlew clean          # Clean build artifacts
```

### Running Individual Tests
```bash
./gradlew test --tests "com.ruha.service.MemberServiceTest"
./gradlew test --tests "com.ruha.repository.*"
./gradlew test --tests "com.ruha.repository.CommentRepositoryTest.탈퇴한_회원의_댓글도_삭제"
```

### Database Setup
- **Production**: MySQL database `ruhadb` on localhost:3306 (username: `root`, password: `1234`)
- **Testing**: H2 in-memory database with `REFERENTIAL_INTEGRITY=FALSE` for cascade testing
- JPA DDL mode: `update` for production, `create-drop` for tests

## Architecture

### Domain Model
Core entities with JPA relationships:
- **Member**: Users with nickname, password, name. Has todos, comments, followers/following
- **Todo**: Tasks with title, description, completion status, importance level, public/private visibility
- **Follow**: Self-referential many-to-many relationship between members (follower/following)
- **Comment**: Comments on todos
- **Category**: Classification for todos
- **TodoImage**: Images attached to todos

### Key Patterns
- **Entity Relationships**: Bidirectional with convenience methods (e.g., `Member.follow(Member)`, `Member.addTodo(Todo)`)
- **Base Entities**: `BaseTimeEntity` (createdAt, updatedAt), `BaseCreatedAtEntity` (createdAt only)
- **Builder Pattern**: All entities use Lombok `@Builder` with `@EqualsAndHashCode(onlyExplicitlyIncluded = true)`
- **Validation**: Jakarta validation annotations on entity fields
- **Exception Handling**: Domain-specific exceptions with custom error codes organized by domain
- **JPA Cascade**: Uses `CascadeType.ALL` with `orphanRemoval = true` - requires proper bidirectional relationship setup

### Security & JWT
- JWT-based stateless authentication via `JwtProvider`
- BCrypt password encoding
- Public endpoints: `/api/members/signup`, `/api/members/login`
- JWT secret and expiration configured in `application.yml`
- Authentication handled via `SecurityUtil.getLoginMemberId()` in services

### Package Structure
- `entity/`: JPA entities with relationships and business methods
- `dto/`: Request/response DTOs organized by domain
- `repository/`: Spring Data JPA repositories with performance indexes
- `service/`: Business logic layer with common methods for authentication
- `exception/`: Custom exceptions organized by domain (member, todo, follow, comment, auth)
- `config/`: Spring Security configuration
- `jwt/`: JWT token management
- `util/`: Utility classes (SecurityUtil)

### Testing
- Repository tests using `@DataJpaTest` with H2 database
- Service tests using `@SpringBootTest`
- Test database disables referential integrity for cascade testing
- Korean method names used for test descriptions

## Important Implementation Notes

### JPA Cascade Behavior
- Cascade deletion requires bidirectional relationships to be properly set in memory
- Use convenience methods like `member.addComment(comment)` to maintain both sides
- Test cascade operations by ensuring parent entity collections contain child entities

### Service Layer Patterns
- `MemberService` uses common methods: `getCurrentAuthenticatedMember()`, `validatePassword()`
- Authentication errors throw `UnauthorizedException`, not `MemberNotFoundException`
- Services use `@Transactional(readOnly = true)` by default with `@Transactional` for writes

### Performance Optimizations
- All entities have database indexes on foreign keys and frequently queried fields
- Repository methods include EntityGraph options for N+1 query prevention
- equals/hashCode implemented using only entity IDs for collection safety