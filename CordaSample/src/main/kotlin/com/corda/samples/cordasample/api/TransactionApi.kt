package com.corda.samples.cordasample.api

import com.corda.samples.cordasample.flow.CreateTransaction.CreateTransactionFlow
import com.corda.samples.cordasample.state.TransactionState
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.startTrackedFlow
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.utilities.getOrThrow
import net.corda.core.utilities.loggerFor
import org.slf4j.Logger
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

// GET: http://localhost:12002/api/transaction/all
// PUT: http://localhost:12002/api/transaction/create
// This API is accessible from /api/transaction. All paths specified below are relative to it.

@Path("transaction")
class TransactionApi(private val rpcOps: CordaRPCOps) {

    companion object {
        private val logger: Logger = loggerFor<TransactionApi>()
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    fun getPeople(): Response {
        return Response.status(Response.Status.OK).entity(rpcOps.vaultQueryBy<TransactionState>().states).build()
    }

    @PUT
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    fun createTransaction(data: CreateRequestModelTransaction): Response {

        if (data.origin.isEmpty() || data.destiny.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity( ResponseModelTransaction("Documentos inválido") ).build()
        }

        return try {

            val signedTx = rpcOps.startTrackedFlow(::CreateTransactionFlow, data.origin, data.destiny, data.amount).returnValue.getOrThrow()

            val tx = signedTx.tx
            val outputs = tx.outputs

            logger.debug(outputs.toString())

//            val res = "Ok"
//            val res = outputs.single()

            Response.status(Response.Status.CREATED).entity(outputs).build()

        } catch (ex: Throwable) {

            logger.error(ex.message, ex)
            Response.status(Response.Status.BAD_REQUEST).entity( ResponseModelTransaction(ex.message.toString()) ).build()

        }
    }
}

data class CreateRequestModelTransaction (
    val origin : String,
    val destiny : String,
    val amount : Double
)

data class ResponseModelTransaction (
    val message : String
)