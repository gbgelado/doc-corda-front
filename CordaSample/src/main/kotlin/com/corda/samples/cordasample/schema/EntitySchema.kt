package com.corda.samples.cordasample.schema

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table

/**
 * The family of schemas for EntityState.
 */
object EntitySchema

/**
 * An EntityState schema.
 */
object EntitySchemaV1 : MappedSchema(
    schemaFamily = EntitySchema.javaClass,
    version = 1,
    mappedTypes = listOf(PersistentEntity::class.java)) {
    @Entity
    @Table(name = "entity_states", indexes = [Index(name = "document_idx", columnList="document"), Index(name = "name_idx", columnList="name")])
    class PersistentEntity(

        @Column(name = "name")
        var name: String,

        @Column(name = "document")
        var document: String,

        @Column(name = "amount")
        var amount: Double

    ) : PersistentState() {
        constructor(): this("", "", 0.00)
    }

}