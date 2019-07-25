package com.corda.samples.cordasample.contract

import com.corda.samples.cordasample.state.DebtState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

class DebtContract : Contract {

    override fun verify(tx: LedgerTransaction) {

        val command = tx.commandsOfType<Commands>().single()

        when (command.value) {
            is Commands.CreateDebt -> verifyCreateDebt(tx)
            else -> throw IllegalArgumentException("Command not found")
        }

    }

    private fun verifyCreateDebt(tx: LedgerTransaction){

        requireThat {
            "Para criação de uma dívida nenhum estado deve ser consumido" using (tx.inputs.isEmpty())
            "Apenas uma dívida deve ser criada" using (tx.outputs.size == 1)
            val out = tx.outputsOfType<DebtState>().single()
            "Valor da dívida deve ser maior que zero" using (out.debt.debtAmount > 0.00)
        }

    }

    interface Commands : CommandData {
        class CreateDebt : Commands
    }
}