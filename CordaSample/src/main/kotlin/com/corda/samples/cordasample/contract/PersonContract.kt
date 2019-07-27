package com.corda.samples.cordasample.contract

import com.corda.samples.cordasample.state.PersonState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

class PersonContract : Contract {

    override fun verify(tx: LedgerTransaction) {

        val command = tx.commandsOfType<Commands>().single()

        when (command.value) {
            is Commands.CreatePerson -> verifyCreatePerson(tx)
            else -> throw IllegalArgumentException("Command not found")
        }

    }

    private fun verifyCreatePerson(tx: LedgerTransaction){

        requireThat {
            "Para criação de pessoa nenhum estado deve ser consumido" using (tx.inputs.isEmpty())
            "Apenas uma pessoa deve ser criada" using (tx.outputs.size == 1)
            val out = tx.outputsOfType<PersonState>().single()
            "Documento é obrigatório" using (out.person.document.isNotEmpty())
        }

    }

    interface Commands : CommandData {
        class CreatePerson : Commands
    }
}