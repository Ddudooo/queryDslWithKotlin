package study.kotlin.querydsl.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@SpringBootTest
@Transactional
internal class MemberTest{

    @Autowired
    lateinit var em : EntityManager

    @Test
    fun testEntity(){
        //given
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

        //초기화
        em.flush()
        em.clear()

        //when
        val result = em.createQuery("select m from Member m join fetch m.team", Member::class.java)
            .resultList

        //then
        for(member in result){
            assertThat(member.username).contains("member")
            assertThat(member.team?.name).contains("team")
        }
    }
}