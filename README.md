# 과제 A — 수강 신청 시스템
프로젝트명: `liveklass-enrollment-system`


## 목차
- [프로젝트 개요](#프로젝트-개요)
- [기술 스택](#기술-스택)
- [실행방법](#실행방법)
- [요구사항 해석 및 가정](#요구사항-해석-및-가정)
- [설계 결정과 이유](#설계-결정과-이유)
- [미구현 및 제약사항](#미구현--제약사항)
- [AI 활용 범위](#ai-활용-범위)
- [API 목록 및 예시](#api-목록-및-예시)
- [데이터 모델 설명](#데이터-모델-설명)
- [테스트 실행 방법](#테스트-실행-방법)

<br>

# 프로젝트 개요 

본 프로젝트는 **수강 신청, 결제 확정, 수강 취소, 정원 관리**를 구현한 수강 신청 시스템입니다.

사용자는 강의를 조회하고 수강 신청을 진행하며, 결제 확정을 통해 최종 수강 상태로 변경됩니다.  
강의는 정원 제한을 가지며, 동시성 상황에서도 안정적으로 처리되도록 설계되었습니다.

# 기술 스택

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

<br><br>

# 실행방법

## 1. DB 생성 (MySQL)

### DB 생성 및 계정 권한 설정
- MySQL에서 데이터베이스 생성 : 테이블 생성 및 샘플 데이터는 `src/main/resources/수강신청시스템테이블생성문및샘플데이터.sql'을 참고하시면 됩니다.
- +) DB 생성 및 계정 권한 설정 생성문 예시:
  ```sql
  CREATE DATABASE liveklass;
  
  CREATE USER '계정명'@'localhost'
  IDENTIFIED BY '비밀번호';
  
  GRANT ALL PRIVILEGES
  ON liveklass.*
  TO '계정명'@'localhost';
  
  FLUSH PRIVILEGES;
  ```
- `계정명`과 `비밀번호`는 원하는 값으로 변경하여 사용합니다.

### 테이블 생성 및 샘플 데이터 추가
- `src/main/resources/수강신청시스템테이블생성문및샘플데이터.sql`을 참고하시면 됩니다.

<br>

## 2. application.properties 설정
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
<br>

## 3. 프로젝트 실행 
IDE에서 프로젝트 import 후 Boot Dashboard에서 서버 시작 <br>
 (또는 프로젝트 우클릭 → Run As → Spring Boot App)

<br>
 
## 4. 접속 URL(localhost)
`http://localhost:포트번호/liveklass`


<br><br>

# 요구사항 해석 및 가정 

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

### 정원 관리 규칙
- 실제 정원 증가는 결제 완료(수강확정) 시점에 반영했습니다.
- 결제 대기(PENDING)는 정원에 포함하지 않앗습니다.
- 정원 초과 상황은 DB 조건문과 트랜잭션(`@Transactional`)으로 제어했습니다.
- 정원이 다 찼을 때는 해당 강의의 상태를 모집마감으로 변경합니다.

<br><br>

# 설계 결정과 이유 
### 1. 결제 확정 시 정원 증가
수강 신청 시점이 아닌 결제 확정 시점에만 현재 수강 인원을 증가시키도록 설계하였습니다.

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

<br><br>

# 미구현 / 제약사항 
- 인증/인가는 구현하지 않았으며, memId를 하드코딩하여 테스트했습니다.
- 실제 결제 연동은 구현되지 않았으며, 상태 변경으로 대체했습니다.
- 선택 구현 항목 중 대기열(waitlist) 기능은 구현하지 않았습니다.

<br><br>

# AI 활용 범위
본 과제에서는 AI(ChatGPT, Claude)를 다음 범위에서 보조 도구로 사용했습니다.

- 구조 설계 보조
- SQL/MyBatis 문법 및 쿼리 작성 보조
- 트랜잭션 및 동시성 처리 개념 설명
- 예외 및 오류 해결 지원
- AJAX/JSP 연동 구현 보조
- 테스트 코드 작성 도움
- README 문서 작성 도움

최종 요구사항 해석, 기능 구현 및 코드 수정은 직접 수행하였습니다.

<br><br>

# API 목록 및 예시

## 강의 관리 API
| Method | URL                              | 설명                 |
| ------ | -------------------------------- | ------------------ |
| GET    | `/lecture/list.page`             | 강의 관리 페이지 이동       |
| POST   | `/lecture/filterLecture.do`      | 강의 목록 필터링 및 페이징 조회 |
| GET    | `/lecture/lecturRegistForm.page` | 강의 등록 페이지 이동       |
| POST   | `/lecture/lectureRegist.do`      | 강의 등록              |
| GET    | `/lecture/detail.do`             | 강의 상세 조회           |
| POST   | `/lecture/modify.do`             | 강의 상태 변경           |

<br>

## 수강 신청 관리 API
| Method | URL                              | 설명                 |
| ------ | -------------------------------- | ------------------ |
| GET    | `/lecture/list.page`             | 강의 관리 페이지 이동       |
| POST   | `/lecture/filterLecture.do`      | 강의 목록 필터링 및 페이징 조회 |
| GET    | `/lecture/lecturRegistForm.page` | 강의 등록 페이지 이동       |
| POST   | `/lecture/lectureRegist.do`      | 강의 등록              |
| GET    | `/lecture/detail.do`             | 강의 상세 조회           |
| POST   | `/lecture/modify.do`             | 강의 상태 변경           |

<br>

## 페이징 정보 객체 (PageInfoDto, pi)

| 필드명         | 설명           |
| ----------- | ------------ |
| listCount   | 전체 게시글 수     |
| currentPage | 현재 페이지       |
| pageLimit   | 페이지 번호 표시 개수 |
| boardLimit  | 한 페이지당 게시글 수 |
| maxPage     | 전체 마지막 페이지   |
| startPage   | 시작 페이지 번호    |
| endPage     | 끝 페이지 번호     |

<br>

## 1. 강의 목록 필터링 및 페이징 조회

### 요청 URL
```http
POST /lecture/filterLecture.do
```

### 요청 파라미터
| 파라미터      | 타입     | 설명                               |
| --------- | ------ | -------------------------------- |
| page      | int    | 요청 페이지 번호                        |
| condition | String | 강의 상태 조건 (DRAFT / OPEN / CLOSED) |


### 요청 예시
```
page=1&condition=OPEN
```
### 응답 예시

```json
{
  "list": [
    {
      "lecId": 1,
      "lecTitle": "Spring Boot 입문",
      "price": 100000,
      "capacity": 30,
      "currentEnrollmentCount": 10,
      "status": "OPEN",
      "startDate": "2026-06-01",
      "endDate": "2026-07-31"
    }
  ],
  "pi": {
    "currentPage": 1,
    "maxPage": 3,
    "startPage": 1,
    "endPage": 3
  }
}
```

<br>

## 2. 신청 가능한 강의 목록 조회

### 요청 URL
```http
POST /enrollment/lectureList.do
```

### 응답 예시

```json
{
  "list": [
    {
      "lecId": 1,
      "memName": "홍길동",
      "lecTitle": "MySQL 실전",
      "price": 80000,
      "capacity": 30,
      "currentEnrollmentCount": 5,
      "status": "OPEN"
    }
  ]
}
```

<br>

## 3. 내 수강 신청 목록 조회

### 요청 URL
```http
POST /enrollment/enrollmentList.do
```

### 요청 파라미터
| 파라미터 | 타입  | 설명        |
| ---- | --- | --------- |
| page | int | 요청 페이지 번호 |



### 요청 예시
```
page=1
```
### 응답 예시

```json
{
  "list": [
    {
      "lecTitle": "Spring Boot 입문",
      "enrollStatus": "PENDING",
      "createdAt": "2026-05-22 13:10",
      "capacity": 30,
      "currentEnrollmentCount": 1
    }
  ],
  "pi": {
    "currentPage": 1,
    "maxPage": 3,
    "startPage": 1,
    "endPage": 3
  }
}
```

<br>

## 4. 수강 신청

### 요청 URL
```http
POST /enrollment/enroll.do
```

### 요청 파라미터
| 파라미터  | 타입  | 설명    |
| ----- | --- | ----- |
| lecId | int | 강의 번호 |
| memId | int | 회원 번호 |


### 요청 예시
```
lecId=1&memId=3
```
### 응답 예시

**성공**

```json
{
  "alertMsg": "수강 신청에 성공하였습니다. 결제 확정을 진행해주세요."
}
```

**실패(정원초과)**
```json
{
  "alertMsg": "정원이 초과되었습니다."
}
```

<br>

## 5. 결제 확정

### 요청 URL
```http
POST /enrollment/confirm.do
```

### 요청 파라미터
| 파라미터  | 타입  | 설명    |
| ----- | --- | ----- |
| lecId | int | 강의 번호 |
| memId | int | 회원 번호 |


### 요청 예시
```
lecId=1&memId=3
```
### 응답 예시

**성공**

```json
{
  "alertMsg": "결제가 완료되어 수강 확정되었습니다."
}
```

**실패(정원초과)**
```json
{
  "alertMsg": "정원이 초과되었습니다."
}
```

<br>

## 6. 수강 취소

### 요청 URL
```http
POST /enrollment/cancel.do
```

### 요청 파라미터
| 파라미터         | 타입     | 설명       |
| ------------ | ------ | -------- |
| lecId        | int    | 강의 번호    |
| memId        | int    | 회원 번호    |
| enrollStatus | String | 수강 신청 상태 |
| confirmedAt  | String | 결제 확정 시간 |


### 요청 예시
```
lecId=1&memId=3&enrollStatus=CONFIRMED&confirmedAt=2026-05-20 09:35
```
### 응답 예시

**성공**

```json
{
  "alertMsg": "수강이 정상적으로 취소되었습니다."
}
```

**실패(취소 기한 만료)**
```json
{
  "alertMsg": "결제 취소 기간이 지났습니다."
}
```

<br>

## 7. 강의 등록

### 요청 URL
```http
POST /lecture/lectureRegist.do
```

### 요청 파라미터
| 파라미터        | 타입     | 설명       |
| ----------- | ------ | -------- |
| creatorId   | int    | 강사 회원 번호 |
| lecTitle    | String | 강의 제목    |
| description | String | 강의 설명    |
| price       | int    | 강의 가격    |
| capacity    | int    | 최대 정원    |
| startDate   | String | 수강 시작일   |
| endDate     | String | 수강 종료일   |


### 요청 예시
```
creatorId=1
&lecTitle=Spring Boot 입문
&description=Spring Boot 기초 강의
&price=100000
&capacity=30
&startDate=2026-06-01
&endDate=2026-07-31
```
### 응답 처리

- 등록 성공 시 강의 목록 페이지로 이동
- 실패 시 이전 페이지로 이동 및 alert 메시지 출력

### 유효성 검사
- 강의 제목 필수 입력
- 강의 설명 필수 입력
- 가격은 1 이상
- 최대 정원은 1 이상
- 수강 종료일은 시작일 이후여야 함

<br>

## 8. 강의 상세 조회

### 요청 URL
```http
GET /lecture/detail.do
```

### 요청 파라미터
| 파라미터  | 타입  | 설명    |
| ----- | --- | ----- |
| lecId | int | 강의 번호 |


### 요청 예시
```
/lecture/detail.do?lecId=1
```
### 응답 데이터

**강의 정보**
```json
{
  "lecId": 1,
  "lecTitle": "Spring Boot 입문",
  "description": "Spring Boot 기초 강의",
  "price": 100000,
  "capacity": 30,
  "currentEnrollmentCount": 10,
  "status": "OPEN",
  "startDate": "2026-06-01",
  "endDate": "2026-07-31"
}
```

**수강생 목록**
```json
[
  {
    "memName": "홍길동",
    "confirmedAt": "2026-05-20 09:35"
  }
]
```

<br>

## 9. 강의 상태 변경

### 요청 URL
```http
POST /lecture/modify.do
```

### 요청 파라미터
| 파라미터   | 타입     | 설명        |
| ------ | ------ | --------- |
| lecId  | int    | 강의 번호     |
| status | String | 변경할 강의 상태 |


### 요청 예시
```
lecId=1&status=OPEN
```
### 상태 변경 규칙
| 현재 상태  | 변경 가능 상태 |
| ------ | -------- |
| DRAFT  | OPEN     |
| OPEN   | CLOSED   |
| CLOSED | 변경 불가    |


### 응답 처리

강의 목록 페이지 이동 후 alert 메세지 출력

- 성공 시 : `강의 상태가 변경되었습니다.`
- 실패 시 : `강의 상태 변경에 실패하였습니다.`
- 잘못된 요청 시 : `잘못된 상태 변경 입니다.`


<br><br>

# 데이터 모델 설명 

## ERD
<img src="src/main/resources/static/수강 신청 시스템 과제.png" height="400px;" >

<br>

## 테이블 구조
### MEMBER
회원 정보를 저장하는 테이블

| 컬럼명        | 타입          | 설명                    |
| ---------- | ----------- | --------------------- |
| MEM_ID     | BIGINT      | 회원 ID (PK)            |
| MEM_NAME   | VARCHAR(50) | 회원 이름                 |
| MEM_ROLE   | CHAR(1)     | 회원 역할 (C: 강사 / S: 학생) |
| CREATED_AT | DATETIME    | 생성일                   |

<br>

### LECTURE

강의 정보를 저장하는 테이블

| 컬럼명                | 타입           | 설명         |
| ------------------ | ------------ | ---------- |
| LEC_ID             | BIGINT       | 강의 ID (PK) |
| CREATOR_ID         | BIGINT       | 강사 ID (FK) |
| LEC_TITLE          | VARCHAR(100) | 강의 제목      |
| DESCRIPTION        | TEXT         | 강의 설명      |
| PRICE              | INT          | 강의 가격      |
| CAPACITY           | INT          | 최대 정원      |
| CUR_ENROLLMENT_CNT | INT          | 현재 신청 인원   |
| LEC_STATUS         | VARCHAR(20)  | 강의 상태      |
| START_DATE         | DATE         | 수강 시작일     |
| END_DATE           | DATE         | 수강 종료일     |
| CREATED_AT         | DATETIME     | 생성일        |
| UPDATED_AT         | DATETIME     | 수정일        |

<br>

**강의 상태 값**
| 상태값    | 설명   |
| ------ | ---- |
| DRAFT  | 초안   |
| OPEN   | 모집중  |
| CLOSED | 모집마감 |

<br>

### ENROLLMENT

수강 신청 정보를 저장하는 테이블

| 컬럼명           | 타입          | 설명            |
| ------------- | ----------- | ------------- |
| ENROLL_ID     | BIGINT      | 수강 신청 ID (PK) |
| LEC_ID        | BIGINT      | 강의 ID (FK)    |
| MEM_ID        | BIGINT      | 수강생 ID (FK)   |
| ENROLL_STATUS | VARCHAR(20) | 수강 신청 상태      |
| CONFIRMED_AT  | DATETIME    | 결제 완료 시간      |
| CANCELLED_AT  | DATETIME    | 취소 시간         |
| CREATED_AT    | DATETIME    | 신청 시간         |

<br>

**수강 신청 상태 값**
| 상태값       | 설명    |
| --------- | ----- |
| PENDING   | 결제 대기 |
| CONFIRMED | 수강 확정 |
| CANCELLED | 수강 취소 |


<br>

## 테이블 관계
- MEMBER ↔ LECTURE
  - 한 명의 강사는 여러 개의 강의를 등록할 수 있다.
  - LECTURE.CREATOR_ID → MEMBER.MEM_ID
- MEMBER ↔ ENROLLMENT
  - 한 명의 회원은 여러 강의를 신청할 수 있다.
  - ENROLLMENT.MEM_ID → MEMBER.MEM_ID
- LECTURE ↔ ENROLLMENT
  - 하나의 강의는 여러 개의 수강 신청 정보를 가질 수 있다.
  - ENROLLMENT.LEC_ID → LECTURE.LEC_ID

## 제약 조건
- UNIQUE (LEC_ID, MEM_ID)
  - 동일 회원의 중복 수강 신청 방지


<br><br>

# 테스트 실행 방법

## 1. 테스트 실행

JUnit 테스트 코드 실행

### 강의 서비스 테스트

- `src/test/java/com/example/liveklass/LectureServiceMockTest.java`
- 실행 방법:
  - 해당 파일 우클릭
  - `Run As > JUnit Test`

<br>

### 수강 신청 서비스 테스트

- `src/test/java/com/example/liveklass/EnrollmentServiceMockTest.java`
- 실행 방법:
  - 해당 파일 우클릭
  - `Run As > JUnit Test`
 
<br>

## 2. 테스트 항목

### LectureServiceMockTest

- 강의 등록 테스트
- 강의 상태 변경 테스트
- 강의 목록 조회 테스트
- 강의 상세 조회 테스트

### EnrollmentServiceMockTest

- 수강 신청 테스트
- 결제 확정 테스트
- 수강 취소 테스트
- 신청 가능 강의 목록 조회 테스트
- 내 수강 신청 목록 조회 테스트
