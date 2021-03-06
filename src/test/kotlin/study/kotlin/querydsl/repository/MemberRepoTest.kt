package study.kotlin.querydsl.repository

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import study.kotlin.querydsl.dto.MemberSearchCondition
import study.kotlin.querydsl.entity.Member
import study.kotlin.querydsl.entity.QMember.member
import study.kotlin.querydsl.entity.Team
import javax.persistence.EntityManager

@SpringBootTest
@Transactional
class MemberRepoTest {
    @Autowired
    lateinit var em : EntityManager

    @Autowired
    lateinit var memberRepo : MemberRepo

    @Test
    fun basicTest() {
        val member = Member("member1", 10)
        memberRepo.save(member)

        val findMember = memberRepo.findById(member.id!!).get()
        Assertions.assertThat(findMember).isEqualTo(member)

        val findAll = memberRepo.findAll()
        Assertions.assertThat(findAll).containsExactly(member)

        val findByUsername = memberRepo.findByUsername("member1")
        Assertions.assertThat(findByUsername).containsExactly(member)
    }

    @Test
    fun searchTest2() {
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

        val condition= MemberSearchCondition(teamName = "teamB", ageGoe = 35, ageLoe = 40)

        val result = memberRepo.search(condition)

        Assertions.assertThat(result).extracting("username").containsExactly("member4")
    }

    @Test
    fun searchPageSimple() {
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

        val condition= MemberSearchCondition()
        val pageRequest = PageRequest.of(0,3)

        val result = memberRepo.searchPageSimple(condition, pageRequest)

        assertThat(result.size).isEqualTo(3)
        assertThat(result.content).extracting("username")
            .containsExactly("member1","member2","member3")
    }

    @Test
    fun queryDslPredicateExecutorTest() {
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

        val findAll =
            memberRepo.findAll(member.age.between(10, 40).and(member.username.eq("member1")))
        findAll.forEach {
            println(it)
        }
    }
}