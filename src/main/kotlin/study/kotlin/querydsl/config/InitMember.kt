package study.kotlin.querydsl.config

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import study.kotlin.querydsl.entity.Member
import study.kotlin.querydsl.entity.Team
import javax.annotation.PostConstruct
import javax.persistence.EntityManager

@Profile("local")
@Component
class InitMember(
    private val initMemberService: InitMemberService
){

    @PostConstruct
    fun init() {
        initMemberService.init()
    }

    @Component
    class InitMemberService(
        private val em : EntityManager
    ) {
        @Transactional
        fun init() {
            val teamA = Team("teamA")
            val teamB = Team("teamB")
            em.persist(teamA)
            em.persist(teamB)

            for(i in 1..100){
                lateinit var selectedTeam : Team
                if(i %2 ==0){
                    selectedTeam = teamA
                }else{
                    selectedTeam = teamB
                }
                em.persist(Member("member$i", i, selectedTeam))
            }
        }
    }
}