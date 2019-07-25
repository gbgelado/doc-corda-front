package com.corda.samples.cordasample.schema

import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table

/**
 * The family of schemas for DebtState.
 */
object DebtSchema

/**
 * An DebtState schema.
 */
object DebtSchemaV1 : MappedSchema(
    schemaFamily = DebtSchema.javaClass,
    version = 1,
    mappedTypes = listOf(PersistentDebt::class.java)) {
    @Entity
    @Table(name = "debt_states", indexes = [Index(name = "node1_idx", columnList="node1"), Index(name = "node2_idx", columnList="node2")])
    class PersistentDebt(

        @Column(name = "node1")
        var node1: Party,

        @Column(name = "node2")
        var node2: Party

    ) : PersistentState() {}

}