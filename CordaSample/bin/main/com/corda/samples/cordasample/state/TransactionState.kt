package com.corda.samples.cordasample.state

import com.corda.samples.cordasample.model.EntityModel
import com.corda.samples.cordasample.model.TransactionModel
import com.corda.samples.cordasample.schema.TransactionSchemaV1
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState

/*
    val createTime : Instant,
    val origin : WalletModel,
    val destiny : WalletModel,
    val amount : Float
 */

data class TransactionState(val transaction: TransactionModel,
                            override val linearId: UniqueIdentifier = UniqueIdentifier()
) : LinearState, QueryableState {

    override val participants: List<AbstractParty> = listOf(transaction.origin.node, transaction.destiny.node)

    override fun generateMappedObject(schema: MappedSchema): PersistentState {

        return when (schema) {
            is TransactionSchemaV1 -> TransactionSchemaV1.PersistentTransaction(
                this.transaction.origin.document,
                this.transaction.destiny.document,
                this.transaction.amount
            )
            else -> throw IllegalArgumentException("Unrecognised schema $schema")
        }

    }

    override fun supportedSchemas(): Iterable<MappedSchema> {
        return listOf(TransactionSchemaV1)
    }

}