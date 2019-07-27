package com.corda.samples.cordasample.plugin

import com.corda.samples.cordasample.api.EntityApi
import net.corda.webserver.services.WebServerPluginRegistry
import java.util.function.Function

class EntityPlugin : WebServerPluginRegistry {

    /**
     * A list of classes that expose web APIs.
     */
    override val webApis = listOf(Function(::EntityApi))

    /**
     * A list of directories in the resources directory that will be served by Jetty under /web.
     */
    override val staticServeDirs = mapOf(
        // This will serve the accountWeb2 directory in resources to /web/example
        "entity" to javaClass.classLoader.getResource("entityWeb").toExternalForm()
    )

}