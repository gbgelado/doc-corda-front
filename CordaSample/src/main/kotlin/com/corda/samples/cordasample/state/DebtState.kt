package com.corda.samples.cordasample.state

import com.corda.samples.cordasample.model.DebtModel
import com.corda.samples.cordasample.schema.DebtSchemaV1
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState

data class DebtState(val debt: DebtModel,
                     override val linearId: UniqueIdentifier = UniqueIdentifier()
) : LinearState, QueryableState {

    override val participants: List<AbstractParty> = listOf(debt.debtor, debt.creditor)

    override fun generateMappedObject(schema: MappedSchema): PersistentState {

        return when (schema) {
            is DebtSchemaV1 -> DebtSchemaV1.PersistentDebt(
                this.debt.debtor,
                this.debt.creditor
            )
            else -> throw IllegalArgumentException("Unrecognised schema $schema")
        }

    }

    override fun supportedSchemas(): Iterable<MappedSchema> {
        return listOf(DebtSchemaV1)
    }

}