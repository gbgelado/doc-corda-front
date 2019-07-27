package com.corda.samples.cordasample.model

import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import java.time.Instant

@CordaSerializable
data class TransactionModel(
    val createTime : Instant,
    val origin : EntityModel,
    val destiny : EntityModel,
    val amount : Double
)