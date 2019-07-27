package com.corda.samples.cordasample.api

import com.corda.samples.cordasample.flow.CreateEntity.CreateEntityFlow
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

// GET: http://localhost:12002/api/entity/all
// PUT: http://localhost:12002/api/entity/create
// This API is accessible from /api/entity. All paths specified below are relative to it.

@Path("entity")
class EntityApi(private val rpcOps: CordaRPCOps) {

    companion object {
        private val logger: Logger = loggerFor<EntityApi>()
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
    fun createEntity(data: CreateRequestModelEntity): Response {

        if (data.document.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity( ResponseModelEntity("Documento inválido") ).build()
        }

        return try {

            val signedTx = rpcOps.startTrackedFlow(::CreateEntityFlow, data.name, data.document, data.amount).returnValue.getOrThrow()
            Response.status(Response.Status.CREATED).entity(signedTx.tx.outputs.single()).build()

        } catch (ex: Throwable) {

            logger.error(ex.message, ex)
            Response.status(Response.Status.BAD_REQUEST).entity( ResponseModelEntity(ex.message.toString()) ).build()

        }
    }
}

data class CreateRequestModelEntity (
    val name : String,
    val document : String,
    val amount : Double
)

data class ResponseModelEntity (
    val message : String
)