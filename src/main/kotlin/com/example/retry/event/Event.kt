package com.example.retry.event

import javax.persistence.*


@Entity
class Event() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column
    var payload: String? = null

    constructor(payload: String) : this() {
        this.payload = payload
    }

    override fun toString(): String {
        return "Event(id=$id, payload=$payload)"
    }
}
