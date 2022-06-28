# JPA 영속성 관리
### 영속성 컨텍스트
- JPA를 이해하는데 가장 중요한 용어
- 엔티티를 영구 저장하는 환경
- `EntityManager.persist(Entity);` > 엔티티 매니저를 통해 영속성 컨텍스트에 접근

### 엔티티의 생명주기
- 비영속
```java
//객체를 생성한 상태 (비영속)
Member member = new Member();
member.setId(10L);
member.setName("Kim");
```

- 영속
```java
//객체를 생성한 상태 (비영속)
Member member = new Member();
member.setId(10L);
member.setName("Kim");

//객체를 저장한 상태 (영속)
em.persist(member);
```
	- 객체가 영속되었다고 DB에 저장되진 않는다.
	- 트랜잭션을 commit해야 쿼리가 생성된다.

- 준영속
```java
//객체를 영속성 컨텍스트에서 분리 (준영속)
em.detach(member);
```
	- 준영속(detach)를 하면 더이상 해당 객체를 영속성 컨텍스트에서 관리하지 않는다.
	- `em.detach(Entity)`, `em.close()`, `em.clear()` 를 이용하여 준영속할 수 있다.

- 삭제
```java
//객체를 삭제
em.remove(member);
```

### 영속성 컨텍스트의 이점
- 1차 캐시
	- 엔티티를 생성하여 영속을 하게되면 DB에 저장되는 것이 아닌 1차 캐시에 저장된다.
	- 해당 데이터를 조회하면 JPA는 우선 1차 캐시를 조회한다.
	- 1차 캐시에 데이터가 존재하는 경우 DB를 조회하지 않고 반환한다.
	- 1차 캐시에 데이터가 존재하지 않는 경우 DB를 조회하고 해당 데이터를 1차 캐시에 저장 후 반환한다.
	- 엔티티 매니저는 트랜잭션 단위로 생성되고 종료되므로 1차 캐시를 사용한다해서 성능적으로 큰 이득을 보는 것은 아니다. 좀더 객채지향적으로 설계할 수 있는 컨셉적 이점이 있는 것이다.

- 동일성 보장
```java
Member member1 = em.find(Member.class, 10L);
Member member2 = em.find(Member.class, 10L);
System.out.println(member1 == member2);
// true로 조회 됨
```
	- 1차 캐시로 반복가능한 읽기(Repeatable Read) 등급의 트랜잭션 격리 수준을 데이터베이스가 아닌 애플리케이션 차원에서 제공
	- 같은 트랜잭션 내에서 확인

- 트랜잭션을 지원하는 쓰기 지연 (엔티티 등록)
	- 객체를 persist 하면 1차 캐시에 저장을 하면서, 쓰기 지연 SQL 저장소에 쿼리를 저장한다.
	- `tx.commit();`을 실행하면 SQL 저장소에 모아둔 쿼리를 실행하며(`flush`) db commit도 실행한다.

- 변경 감지
```java
Member memberA = em.find(Member.class, "memberA");
memberA.setName("change");
tx.commit();
// EntityManager에 별도의 명령을 하지 않아도 update 쿼리가 실행된다.
```
	- 1차 캐시에 데이터를 저장하는 순간 스냅샷도 저장한다.
	- commit 실행시 1차 캐시의 데이터와 스냅샷의 데이터를 비교하여 변경된 부분이 있으면 update 쿼리를 생성하여 쓰기 지연 SQL저장소에 저장한다. (더티체킹)
	- 쓰지 지연 SQL 저장소들의 쿼리를 실행한다.

### 플러시 (flush)
- 영속성 컨텍스트의 변경된 내용을 DB에 반영하는 작업
- 플러시 발생
	- 변경 감지 (더티 체킹)
	- 수정된 엔티티를 쓰기 지연 SQL 저장소에 등록
	- 쓰기 지연 SQL 저장소의 쿼리들을 데이터베이스에 전송
	- 플러시를 발생한다고 1차 캐시 내의 데이터가 사라지는 것은 아님
- 영속성 컨텍스트를 플러시 하는 방법
	- `em.flush()` : 직접 호출
	- 트랜잭션 커밋 : 자동 호출
	- JPQL 쿼리 실행 : 자동 호출