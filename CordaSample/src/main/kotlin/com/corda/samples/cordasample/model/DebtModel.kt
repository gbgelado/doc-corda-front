package com.corda.samples.cordasample.model

import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import java.time.Instant

// Nó 1 cria uma dívida com nó 2

@CordaSerializable
data class DebtModel(
    val date : Instant,
    val debtor : Party,
    val creditor : Party,
    val debtAmount : Double,
    val comment : String
)