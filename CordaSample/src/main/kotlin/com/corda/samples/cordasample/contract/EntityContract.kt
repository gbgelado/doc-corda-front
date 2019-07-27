package com.corda.samples.cordasample.contract

import com.corda.samples.cordasample.state.EntityState
import com.corda.samples.cordasample.state.PersonState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

class EntityContract : Contract {

    override fun verify(tx: LedgerTransaction) {

        val command = tx.commandsOfType<Commands>().single()

        when (command.value) {
            is Commands.CreateEntity -> verifyCreateEntity(tx)
            else -> throw IllegalArgumentException("Command not found")
        }

    }

    private fun verifyCreateEntity(tx: LedgerTransaction){

        requireThat {
            "Para criação de pessoa nenhum estado deve ser consumido" using (tx.inputs.isEmpty())
            "Apenas uma pessoa deve ser criada" using (tx.outputs.size == 1)
            val out = tx.outputsOfType<EntityState>().single()
            "Documento é obrigatório" using (out.entity.document.isNotEmpty())
            "Valor deve ser 0" using ( out.entity.amount.equals(0.0) )
        }

    }

    interface Commands : CommandData {
        class CreateEntity : Commands
    }
}