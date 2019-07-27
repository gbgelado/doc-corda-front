package com.corda.samples.cordasample.model

import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable
import java.time.Instant

@CordaSerializable
data class EntityModel(
    val node : Party,
    val createTime : Instant,
    val name : String,
    val document : String,
    var amount : Double
)
