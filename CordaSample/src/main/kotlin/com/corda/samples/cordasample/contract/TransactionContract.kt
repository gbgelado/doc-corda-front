package com.corda.samples.cordasample.contract

import com.corda.samples.cordasample.model.EntityModel
import com.corda.samples.cordasample.state.EntityState
import com.corda.samples.cordasample.state.PersonState
import com.corda.samples.cordasample.state.TransactionState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

class TransactionContract : Contract {

    override fun verify(tx: LedgerTransaction) {

        val command = tx.commandsOfType<Commands>().single()

        when (command.value) {
            is Commands.CreateTransaction -> verifyCreateTransaction(tx)
            else -> throw IllegalArgumentException("Command not found")
        }

    }

    private fun verifyCreateTransaction(tx: LedgerTransaction){

        requireThat {
            val inputs = tx.inputsOfType<EntityState>()
            " Transacao deve ter um documento de origem e um de destino " using ( inputs.size == 2 )

            val outputs = tx.outputsOfType<EntityState>()
            " Transacao deve resultar em um documento de origem e um de destino " using ( outputs.size == 3 )

            val origin = inputs[0].entity
            " Origem tem saldo " using ( origin.amount > 0 )

            // val destiny = inputs[1].entity
        }

    }

    interface Commands : CommandData {
        class CreateTransaction : Commands
    }
}