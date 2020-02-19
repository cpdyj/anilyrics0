package name.utau.anilyrics

import com.fasterxml.jackson.module.kotlin.registerKotlinModule

internal fun registerKotlinJacksonModules(){
    io.vertx.core.json.jackson.DatabindCodec.mapper().registerKotlinModule()
    io.vertx.core.json.jackson.DatabindCodec.prettyMapper().registerKotlinModule()
}

