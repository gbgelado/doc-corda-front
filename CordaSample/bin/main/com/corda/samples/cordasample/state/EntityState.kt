package com.corda.samples.cordasample.state

import com.corda.samples.cordasample.model.EntityModel
import com.corda.samples.cordasample.schema.EntitySchemaV1
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState

data class EntityState(val entity: EntityModel,
                       override val linearId: UniqueIdentifier = UniqueIdentifier()
) : LinearState, QueryableState {

    override val participants: List<AbstractParty> = listOf(entity.node)

    override fun generateMappedObject(schema: MappedSchema): PersistentState {

        return when (schema) {
            is EntitySchemaV1 -> EntitySchemaV1.PersistentEntity(
                this.entity.name,
                this.entity.document,
                this.entity.amount
            )
            else -> throw IllegalArgumentException("Unrecognised schema $schema")
        }

    }

    override fun supportedSchemas(): Iterable<MappedSchema> {
        return listOf(EntitySchemaV1)
    }

}