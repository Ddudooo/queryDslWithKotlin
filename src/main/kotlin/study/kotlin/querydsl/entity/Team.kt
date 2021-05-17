package study.kotlin.querydsl.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class Team(name : String) {
    @Id
    @GeneratedValue
    @Column(name= "team_id")
    var id : Long? = null
    var name : String = name
    @OneToMany(mappedBy = "team")
    var members : MutableList<Member> = mutableListOf()
}