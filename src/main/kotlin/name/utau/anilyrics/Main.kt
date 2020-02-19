package name.utau.anilyrics

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.deploymentOptionsOf
import io.vertx.kotlin.core.eventbus.deliveryOptionsOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

fun main(vararg args:String){
    println("Hello world..")
    registerKotlinJacksonModules()


    val vertx=Vertx.vertx()

    vertx.deployVerticle(WebVerticle(), deploymentOptionsOf(config = WebVerticleOptions().let { JsonObject.mapFrom(it) })){
        println(it)
    }

}

data class User(val avatarUrl:String)