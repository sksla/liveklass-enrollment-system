# liveklass-enrollment-system
# 과제 A — 수강 신청 시스템

## 프로젝트 개요 

본 프로젝트는 **수강 신청, 결제 확정, 수강 취소, 정원 관리**를 구현한 수강 신 시스템입니다.

사용자는 강의를 조회하고 수강 신청을 진행하며, 결제 확정을 통해 최종 수강 상태로 변경됩니다.  
강의는 정원 제한을 가지며, 동시성 상황에서도 안정적으로 처리되도록 설계되었습니다.

## 기술 스택

### Backend
- Java 17
- Spring Boot
- Spring MVC
- MyBatis
- MySQL
- JUnit4

### Frontend
- JSP
- HTML5/CSS3/JavaScript
- JSTL
- jQuery
- Bootstrap 4

## 실행방법

### 1. DB 생성
MySQL에서 데이터베이스 생성

### 2. application.properties 설정
- `src/main/resources/application.properties.example`을 참조해서 `application.properties`에 DB정보 입력
- `application.properties` 예시 :
  ```properties
   spring.application.name=liveklass-enrollment-system
  
  # 서버포트번호 수정
  server.port=포트번호 입력
  
  # viewResolver 관련 세팅
  spring.mvc.view.prefix=/WEB-INF/views/
  spring.mvc.view.suffix=.jsp
  
  # contextPath 별도로 지정
  server.servlet.context-path=/liveklass
  
  # db연결정보 작성 (connection pool 작업)
  spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
  spring.datasource.url=jdbc:mysql://localhost:3306/liveklass?serverTimezone=Asia/Seoul
  spring.datasource.username=여기에_유저명
  spring.datasource.password=여기에 비밀번호
  
  # mybatis 관련 설정
  mybatis.mapper-locations=mybatis/mappers/*.xml
  mybatis.type-aliases-package=com.example.liveklass.dto
  mybatis.configuration.jdbc-type-for-null=NULL
  ```
### 3. 프로젝트 실행 
IDE에서 프로젝트 import 후 Boot Dashboard에서 서버 시작 <br>
 (또는 프로젝트 우클릭 → Run As → Spring Boot App)

 <br>
 
### 4. 접속 URL(localhost)
`https://localhost:포트번호/liveklass`


## 요구사항 해석 및 가정 

### 사용자
- 별도의 회원가입/로그인 구현은 요구사항에 없으므로, 사용자는 DB에 저장된 데이터로 가정합니다.
- 강사: `mem_id = 1`, 수강생 : `mem_id=3`으로 고정하여 테스트했습니다.

### 강의 상태 전환
- 상태 전환(DRAFT → OPEN → CLOSED)은 순서대로만 가능하며, 역방향 전환은 불가능하게 구현했습니다

### 수강 신청
- OPEN 상태인 강의에만 신청 가능합니다.
- 동일 사용자가 같은 강의에 중복 신청하는 것은 불가능하게 처리했습니다.
- CANCELLED 상태인 신청은 정원 계산에서 제외됩니다.
- 취소처리된 강의는 재신청 가능합니다.

### 결제 완료(수강 확정)
- 외부 결제 연동 없이 `PENDING` → `CONFIRMED` 상태 변경으로 대체했습니다.
- 
### 정원 관리 규칙
- 실제 정원 증가는 결제 완료(수강확정) 시점에 반영했습니다.
- 결제 대기(PENDING)는 정원에 포함하지 않앗습니다.
- 정원 초과 상황은 DB 조건문과 트랜잭션(`@Transactional`)으로 제어했습니다.

## 설계 결정과 이유 
### 1. 결제 확정 시 정원 증가
수강 시점이 아닌 결제 확정 시점에만 현재 수강 인원을 증가시키도록 설계하였습니다.

<br>

이유:
- 실제 결제가 완료된 사용자만 정원에 포함하기 위함
- 결제 대기 상태로 인한 불필요한 자리 선점을 방지하기 위함


### 2. 동시성 처리
동시에 여러 사용자가 마지막 정원에 신청하는 상황을 고려하여 다음과 같이 처리하였습니다.

- `@Transactional` 사용
- DB 조건 업데이트 적용
- UNIQUE 제약 조건 활용
- 정원 증가 쿼리에서 조건 검사 수행 

### 3. 강의 상태 변경 순서 강제
- DRAFT → OPEN → CLOSED 순서만 허용하여 잘못된 상태 변경으로 인한 데이터 오류를 방지했습니다.


## 미구현 / 제약사항 
- 인증/인가는 구현하지 않았으며, memId를 하드코딩하여 테스트했습니다.
- 실제 결제 연동은 구현되지 않았으며, 상태 변경으로 대체했습니다.
- 선택 구현 항목 중 대기열(waitlist) 기능은 구현하지 않았습니다.

## AI 활용 범위
본 과제에서는 AI(ChatGPT, Claude)를 다음 범위에서 보조 도구로 사용했습니다.

- 구조 설계 보조
- SQL/MyBatis 문법 및 쿼리 작성 보조
- 트랜잭션 및 동시성 처리 개념 설명
- 예외 및 오류 해결 지원
- AJAX/JSP 연동 구현 보조
- 테스트 코드 예시 작성
- README 문서 작성 도움

최종 요구사항 해석, 기능 구현 및 코드 수정은 직접 수행하였습니다

