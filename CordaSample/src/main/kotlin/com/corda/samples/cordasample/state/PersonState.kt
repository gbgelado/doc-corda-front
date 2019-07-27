package com.corda.samples.cordasample.state

import com.corda.samples.cordasample.model.PersonModel
import com.corda.samples.cordasample.schema.PersonSchemaV1
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState

data class PersonState(val person: PersonModel,
                       override val linearId: UniqueIdentifier = UniqueIdentifier()
) : LinearState, QueryableState {

    override val participants: List<AbstractParty> = listOf(person.node)

    override fun generateMappedObject(schema: MappedSchema): PersistentState {

        return when (schema) {
            is PersonSchemaV1 -> PersonSchemaV1.PersistentPerson(
                this.person.name,
                this.person.document
            )
            else -> throw IllegalArgumentException("Unrecognised schema $schema")
        }

    }

    override fun supportedSchemas(): Iterable<MappedSchema> {
        return listOf(PersonSchemaV1)
    }

}