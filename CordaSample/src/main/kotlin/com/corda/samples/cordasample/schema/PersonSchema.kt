package com.corda.samples.cordasample.schema

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table

/**
 * The family of schemas for PersonState.
 */
object PersonSchema

/**
 * An PersonState schema.
 */
object PersonSchemaV1 : MappedSchema(
    schemaFamily = PersonSchema.javaClass,
    version = 1,
    mappedTypes = listOf(PersistentPerson::class.java)) {
    @Entity
    @Table(name = "person_states", indexes = [Index(name = "document_idx", columnList="document"), Index(name = "name_idx", columnList="name")])
    class PersistentPerson(

        @Column(name = "name")
        var name: String,

        @Column(name = "document")
        var document: String

    ) : PersistentState() {
        constructor(): this("", "")
    }

}