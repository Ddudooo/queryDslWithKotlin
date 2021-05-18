package study.kotlin.querydsl

import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import study.kotlin.querydsl.entity.Member
import study.kotlin.querydsl.entity.QMember
import study.kotlin.querydsl.entity.QMember.member
import study.kotlin.querydsl.entity.QTeam.team
import study.kotlin.querydsl.entity.Team
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit

@SpringBootTest
@Transactional
class QueryDslBasicTest(
    @Autowired
    private val em : EntityManager
) {
    val queryFactory = JPAQueryFactory(em)

    @BeforeEach
    fun before() {
        val teamA = Team("teamA")
        val teamB = Team("teamB")
        em.persist(teamA)
        em.persist(teamB)

        val member1 = Member("member1", 10, teamA)
        val member2 = Member("member2", 20, teamA)

        val member3 = Member("member3", 30, teamB)
        val member4 = Member("member4", 40, teamB)
        em.persist(member1)
        em.persist(member2)
        em.persist(member3)
        em.persist(member4)
    }

    @Test
    fun startJPQL(){
        //member1찾기
        val findMember1 = em.createQuery(
            "select m from Member m where m.username = :username",
            Member::class.java
        )
            .setParameter("username", "member1")
            .singleResult

        assertThat(findMember1.username).isEqualTo("member1")
    }

    @Test
    fun startQueryDsl(){
        val fetchOne = queryFactory.select(member)
            .from(member)
            .where(member.username.eq("member1"))
            .fetchOne()

        assertThat(fetchOne?.username).isEqualTo("member1")
    }

    @Test
    fun search() {
        val fetchOne = queryFactory.selectFrom(member)
            .where(
                member.username.eq("member1"),
                member.age.eq(10)
            )
            .fetchOne()

        assertThat(fetchOne?.username).isEqualTo("member1")
        assertThat(fetchOne?.age).isEqualTo(10)
    }

    @Test
    fun resultFetch() {
        /*val fetch = queryFactory
            .selectFrom(member)
            .fetch()

        val fetchOne = queryFactory
            .selectFrom(member)
            .fetchOne()

        val fetchFirst = queryFactory
            .selectFrom(member)
            .fetchFirst()*/

        /*val results = queryFactory
            .selectFrom(member)
            .fetchResults()

        results.total
        results.results*/

        val total = queryFactory
            .selectFrom(member)
            .fetchCount()
    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 2-1. 이름이 없을시 마지막 출력 (null last)
     */
    @Test
    fun sort(){
        em.persist(Member(null, 100))
        em.persist(Member("member5", 100))
        em.persist(Member("member6", 100))

        val result = queryFactory
            .selectFrom(member)
            .where(member.age.eq(100))
            .orderBy(member.age.desc(), member.username.asc().nullsLast())
            .fetch()

        for((index, find) in result.withIndex()){
            if(index==0){
                //member5
                assertThat(find.username).isEqualTo("member5")
                assertThat(find.age).isEqualTo(100)
            }
            if(index==1){
                //member6
                assertThat(find.username).isEqualTo("member6")
                assertThat(find.age).isEqualTo(100)
            }
            if(index==2){
                //nullMember
                assertThat(find.username).isNull()
                assertThat(find.age).isEqualTo(100)
            }
        }
    }

    @Test
    fun paging1() {
        val result = queryFactory
            .selectFrom(member)
            .orderBy(member.username.desc())
            .offset(1)
            .limit(2)
            .fetch()

        assertThat(result.size).isEqualTo(2)
    }

    @Test
    fun paging2() {
        val result = queryFactory
            .selectFrom(member)
            .orderBy(member.username.desc())
            .offset(1)
            .limit(2)
            .fetchResults()

        assertThat(result.total).isEqualTo(4)
        assertThat(result.limit).isEqualTo(2)
        assertThat(result.offset).isEqualTo(1)
        assertThat(result.results.size).isEqualTo(2)
    }

    @Test
    fun aggregation(){
        val results = queryFactory
            .select(
                member.count(),
                member.age.sum(),
                member.age.avg(),
                member.age.max(),
                member.age.min()
            )
            .from(member)
            .fetch()

        val tuple = results[0]
        assertThat(tuple[member.count()]).isEqualTo(4)
        assertThat(tuple[member.age.sum()]).isEqualTo(100)
        assertThat(tuple[member.age.avg()]).isEqualTo(25.0)
        assertThat(tuple[member.age.max()]).isEqualTo(40)
        assertThat(tuple[member.age.min()]).isEqualTo(10)

    }

    /**
     * 팀의 이름과 각 팀의 평균 연령
     */
    @Test
    fun group() {
        val result = queryFactory
            .select(team.name, member.age.avg())
            .from(member)
            .join(member.team, team)
            .groupBy(team.name)
            .fetch()

        val teamA = result[0]
        val teamB = result[1]
        assertThat(teamA[team.name]).isEqualTo("teamA")
        assertThat(teamA[member.age.avg()]).isEqualTo(15.0)

        assertThat(teamB[team.name]).isEqualTo("teamB")
        assertThat(teamB[member.age.avg()]).isEqualTo(35.0)
    }

    @Test
    fun join() {
        val fetch = queryFactory
            .selectFrom(member)
            .join(member.team, team)
            .where(team.name.eq("teamA"))
            .fetch()

        assertThat(fetch)
            .extracting("username")
            .containsExactly("member1","member2")
    }

    @Test
    fun theta_join(){
        em.persist(Member("teamA"))
        em.persist(Member("teamB"))
        em.persist(Member("teamC"))

        val result = queryFactory
            .select(member)
            .from(member, team)
            .where(member.username.eq(team.name))
            .fetch()

        assertThat(result)
            .extracting("username")
            .containsExactly("teamA","teamB")
    }

    @Test
    fun join_on_filtering(){
        val result = queryFactory
            .select(member, team)
            .from(member)
            .join(member.team, team)
            //.on(team.name.eq("teamA"))
            .where(team.name.eq("teamA"))
            .fetch()

        for(tuple in result){
            println("tuple = ${tuple}")
        }
    }

    @Test
    fun join_on_no_releation(){
        em.persist(Member("teamA"))
        em.persist(Member("teamB"))
        em.persist(Member("teamC"))

        val result = queryFactory
            .select(member, team)
            .from(member)
            .leftJoin(team).on(member.username.eq(team.name))
            .where(member.username.eq(team.name))
            .fetch()

        for(tuple in result){
            println("tuple = ${tuple}")
        }
    }
    @PersistenceUnit
    lateinit var emf : EntityManagerFactory

    @Test
    fun fetchJoinNo() {
        em.flush()
        em.clear()

        val findMember = queryFactory
            .selectFrom(member)
            .where(member.username.eq("member1"))
            .fetchOne()

        val loaded = emf.persistenceUnitUtil.isLoaded(findMember?.team)
        assertThat(loaded).isFalse
    }

    @Test
    fun fetchJoin() {
        em.flush()
        em.clear()

        val findMember = queryFactory
            .selectFrom(member)
            .join(member.team, team).fetchJoin()
            .where(member.username.eq("member1"))
            .fetchOne()

        val loaded = emf.persistenceUnitUtil.isLoaded(findMember?.team)
        assertThat(loaded).isTrue
    }

    @Test
    fun subQuery() {
        val memberSub = QMember("memberSub")
        val result = queryFactory
            .selectFrom(member)
            .where(
                member.age.eq(
                    JPAExpressions
                        .select(memberSub.age.max())
                        .from(memberSub)
                )
            )
            .fetch()
        assertThat(result).extracting("age")
            .containsExactly(40)
    }

    @Test
    fun subQueryGoe() {
        val memberSub = QMember("memberSub")
        val result = queryFactory
            .selectFrom(member)
            .where(
                member.age.goe(
                    JPAExpressions
                        .select(memberSub.age.avg())
                        .from(memberSub)
                )
            )
            .fetch()
        assertThat(result).extracting("age")
            .containsExactly(30,40)
    }

    @Test
    fun subQueryIn() {
        val memberSub = QMember("memberSub")
        val result = queryFactory
            .selectFrom(member)
            .where(
                member.age.`in`(
                    JPAExpressions
                        .select(memberSub.age)
                        .from(memberSub)
                        .where(memberSub.age.gt(10))
                )
            )
            .fetch()
        assertThat(result).extracting("age")
            .containsExactly(20,30,40)
    }

    @Test
    fun selectSubQuery(){
        val memberSub = QMember("memberSub")
        val result = queryFactory
            .select(
                member.username,
                JPAExpressions
                    .select(memberSub.age.avg())
                    .from(memberSub)
            )
            .from(member)
            .fetch()

        for(tuple in result){
            println("tuple = ${tuple}")
        }
    }

    @Test
    fun basicCase() {
        val result = queryFactory
            .select(
                member.age
                    .`when`(10).then("열살")
                    .`when`(20).then("스무살")
                    .otherwise("기타")
            )
            .from(member)
            .fetch()

        for(string in result){
            println("string = ${string}")
        }
    }

    @Test
    fun complexCase(){
        val result = queryFactory
            .select(
                CaseBuilder()
                    .`when`(member.age.between(0, 20)).then("0~20살")
                    .`when`(member.age.between(21, 30)).then("21~30살")
                    .otherwise("기타")
            )
            .from(member)
            .fetch()

        for(string in result){
            println("string = ${string}")
        }
    }

    @Test
    fun constant() {
        val result = queryFactory
            .select(member.username, Expressions.constant("A"))
            .from(member)
            .fetch()

        for(tuple in result) {
            println("tuple = ${tuple}")
        }
    }

    @Test
    fun concat() {
        val result = queryFactory
            .select(member.username.concat("_").concat(member.age.stringValue()))
            .from(member)
            .fetch()

        for(string in result) {
            println("tuple = ${string}")
        }
    }
}