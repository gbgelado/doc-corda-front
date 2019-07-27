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
object TransactionSchema

/**
 * An EntityState schema.
 */
object TransactionSchemaV1 : MappedSchema(
    schemaFamily = TransactionSchema.javaClass,
    version = 1,
    mappedTypes = listOf(PersistentTransaction::class.java)) {
    @Entity
    @Table(name = "transactions_states", indexes = [Index(name = "origin_document_idx", columnList="origin_document"), Index(name = "destiny_document_idx", columnList="destiny_document")])
    class PersistentTransaction(

        @Column(name = "origin_document")
        var origin: String,

        @Column(name = "destiny_document")
        var destiny: String,

        @Column(name = "amount")
        var amount: Double

    ) : PersistentState() {
        constructor(): this( "",  "", 0.0)
    }

}