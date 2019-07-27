package com.corda.samples.cordasample.api

import com.corda.samples.cordasample.flow.CashOutEntity.CashOutEntityFlow
import com.corda.samples.cordasample.state.EntityState
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

// GET: http://localhost:12002/api/cashout/all
// PUT: http://localhost:12002/api/cashout/create
// This API is accessible from /api/cashout. All paths specified below are relative to it.

@Path("cashout")
class CashOutApi(private val rpcOps: CordaRPCOps) {

    companion object {
        private val logger: Logger = loggerFor<CashOutApi>()
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    fun getPeople(): Response {
        return Response.status(Response.Status.OK).entity(rpcOps.vaultQueryBy<EntityState>().states).build()
    }

    @PUT
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    fun createCashOut(data: CreateRequestModelCashOut): Response {

        if (data.document.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity( ResponseModelCashOut("Documento inválido") ).build()
        }

        return try {

            val signedTx = rpcOps.startTrackedFlow(::CashOutEntityFlow, data.document, data.amount).returnValue.getOrThrow()
            Response.status(Response.Status.CREATED).entity(signedTx.tx.outputs.single()).build()

        } catch (ex: Throwable) {

            logger.error(ex.message, ex)
            Response.status(Response.Status.BAD_REQUEST).entity( ResponseModelCashOut(ex.message.toString()) ).build()

        }
    }
}

data class CreateRequestModelCashOut (
    val document : String,
    val amount : Double
)

data class ResponseModelCashOut (
    val message : String
)