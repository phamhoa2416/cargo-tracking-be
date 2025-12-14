package com.example.cargotracker.domain.common.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import java.util.UUID
import java.time.Instant

abstract class BaseEntity(
    @Id
    @Column("id")
    open val id: UUID = UUID.randomUUID(),

    @CreatedDate
    @Column("created_at")
    val createdAt: Instant? = null,

    @LastModifiedDate
    @Column("updated_at")
    val updatedAt: Instant? = null
) {
    abstract fun validate()
}

