package study.kotlin.querydsl.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class Member(username: String?, age: Int=0) {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    var id : Long? = null
    var age : Int = age
    var username : String? = username

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var team : Team? = null

    constructor(username: String, age: Int, team: Team): this(username, age){
        this.changeTeam(team)
    }

    fun changeTeam(team : Team) {
        this.team?.members?.remove(this)

        this.team = team
        team.members.add(this)
    }
}