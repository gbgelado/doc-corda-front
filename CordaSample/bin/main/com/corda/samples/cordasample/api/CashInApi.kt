package com.corda.samples.cordasample.api

import com.corda.samples.cordasample.flow.CashInEntity.CashInEntityFlow
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

// GET: http://localhost:12002/api/cashin/all
// PUT: http://localhost:12002/api/cashin/create
// This API is accessible from /api/cashin. All paths specified below are relative to it.

@Path("cashin")
class CashInApi(private val rpcOps: CordaRPCOps) {

    companion object {
        private val logger: Logger = loggerFor<CashInApi>()
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
    fun createCashIn(data: CreateRequestModelCashIn): Response {

        if (data.document.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity( ResponseModelCashIn("Documento inválido") ).build()
        }

        return try {

            val signedTx = rpcOps.startTrackedFlow(::CashInEntityFlow, data.document, data.amount).returnValue.getOrThrow()
            Response.status(Response.Status.CREATED).entity(signedTx.tx.outputs.single()).build()

        } catch (ex: Throwable) {

            logger.error(ex.message, ex)
            Response.status(Response.Status.BAD_REQUEST).entity( ResponseModelCashIn(ex.message.toString()) ).build()

        }
    }
}

data class CreateRequestModelCashIn (
    val name : String,
    val document : String,
    val amount : Double
)

data class ResponseModelCashIn (
    val message : String
)