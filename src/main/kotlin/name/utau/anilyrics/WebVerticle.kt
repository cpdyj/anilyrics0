package name.utau.anilyrics

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import name.utau.anilyrics.utils.put
import org.thymeleaf.TemplateEngine


class WebVerticle : AbstractVerticle() {
    private val options by lazy { config().mapTo(WebVerticleOptions::class.java)!! }
    private val httpServer by lazy { vertx.createHttpServer()!! }
    private val internalCoroutineScope by lazy { CoroutineScope(SupervisorJob()+vertx.dispatcher()) }

    override fun start(startPromise: Promise<Void>?) {
/*
        newRouteMap(vertx).also { httpServer.requestHandler(it);context.put(it) }
        httpServer.listen(options.port, options.host)
            .onSuccess { startPromise?.tryComplete() }
            .onFailure { startPromise?.tryFail(it) }

        context.put(internalCoroutineScope)

        val t=TemplateEngine()
*/
        startPromise?.tryComplete()
        vertx.eventBus().consumer<String>("awsl"){msg->
            println("start: ${msg.body()}")
            vertx.setTimer(5000){
                println(msg.body())
            }
        }

    }

    override fun stop(stopPromise: Promise<Void>?) {
        internalCoroutineScope.cancel("Verticle stop.")
        httpServer.close()
            .onSuccess { stopPromise?.tryComplete() }
            .onFailure { stopPromise?.tryFail(it) }
    }
}


data class WebVerticleOptions(
    val port: Int = 8080,
    val host: String = "0.0.0.0"
)

