# Todolist-api

## 도메인별 핵심 MVP

### 회원 도메인
- 사용자는 생성된 계정으로 로그인할  수 있다.
- 사용자는 로그인된 상태로 로그아웃할 수 있다.

### Todo 도메인
- 상위 및 하위 Todo 항목을 등록할 수 있다.
- <b>Todo는 상위 Todo 하나만을 가질 수 있다.</b>
- 하위 Todo 항목을 Todo 상태별로 필터링 및 페이징을 적용하여 조회할 수 있다.
- Todo 항목을 완료 상태로 변경할 수 있다.
- <b>모든 하위 Todo 항목이 완료 상태인 경우에만</b> 상위 Todo항목을 완료 상태로 변경할 수 있다.
- 상위 및 하위 Todo 항목을 삭제할 수 있다.

### Workspace 도메인
- workspace 를 새로 생성할 수 있으며, 회원은 여러 workspace에 포함될 수 있다.
- 회원의 모든 workspace들을 조회할 수 있다.
- workspace에 새로운 회원을 추가할 수 있다.
- workspace에 기존 회원을 삭제할 수 있다.
- workspace에 포함된 구성원들을 조회할 수 있다.
- 같은 workspace에 포함된 회원들은 서로의 Todo를 함께 조회할 수 있다.
- <b>workspace에 속한 사람만 다른 참가자를 추가하거나 삭제할 수 있다.</b>

## 도메인 설계

![백엔드파일럿프로젝트_도메인설계](https://user-images.githubusercontent.com/44339530/145668762-a989722d-a796-40ce-b679-63c0b07a9543.png)

## 테이블 설계

![백엔드파일럿프로젝트_테이블설계](https://user-images.githubusercontent.com/44339530/145668765-4ab9d6a4-ce79-48a3-ae21-7e97b800c5e8.png)

## 개발 요구사항
- Swagger 모듈을 적용하여 REST API에 대한 API를 조회하고 테스트 할 수 있다.
- 회원 로그인을 통한 인증/인가 과정을 Spring Security를 이용하여 처리한다.
- QueryDSL을 이용한 Query Repository를 구현한다.
- Request 모델에 대해 유효성 검증을 수행한다.(Spring Validator)
- Junit 기반 테스트 코드