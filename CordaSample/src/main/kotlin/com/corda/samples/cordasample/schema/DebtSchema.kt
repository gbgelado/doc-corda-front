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
    @Table(name = "debt_states", indexes = [Index(name = "debtor_idx", columnList="debtor"), Index(name = "creditor_idx", columnList="creditor")])
    class PersistentDebt(

        @Column(name = "debtor")
        var debtor: Party,

        @Column(name = "creditor")
        var creditor: Party

    ) : PersistentState() {}

}