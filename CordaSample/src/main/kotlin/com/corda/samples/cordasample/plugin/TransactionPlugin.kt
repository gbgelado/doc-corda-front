package com.corda.samples.cordasample.plugin

import com.corda.samples.cordasample.api.TransactionApi
import net.corda.webserver.services.WebServerPluginRegistry
import java.util.function.Function

class TransactionPlugin : WebServerPluginRegistry {

    /**
     * A list of classes that expose web APIs.
     */
    override val webApis = listOf(Function(::TransactionApi))

    /**
     * A list of directories in the resources directory that will be served by Jetty under /web.
     */
    override val staticServeDirs = mapOf(
        // This will serve the accountWeb2 directory in resources to /web/example
        "entity" to javaClass.classLoader.getResource("entityWeb").toExternalForm()
    )

}