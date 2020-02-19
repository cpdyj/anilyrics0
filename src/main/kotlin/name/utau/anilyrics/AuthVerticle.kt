package name.utau.anilyrics

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.oauth2.OAuth2Auth
import io.vertx.ext.auth.oauth2.OAuth2ClientOptions
import io.vertx.kotlin.coroutines.CoroutineVerticle

class AuthVerticle : CoroutineVerticle() {

    private val oauthProviderMap = mutableMapOf<String, OAuthLoginProvider>()
    override suspend fun start() {
        val eb = vertx.eventBus()!!
        config.mapTo(AuthVerticleOptions::class.java).clients.forEach {
            oauthProviderMap[it.name] =
                OAuthLoginProvider(it.name, it.callback, OAuth2Auth.create(vertx, it.options))
            println("load OAuth2 login provider: ${it.name}")
        }
        TODO()
    }

    private data class OAuthLoginProvider(val name: String, val callback: String, val client: OAuth2Auth)
}

private data class AuthVerticleOptions(
    val clients: List<OAuthProvierOption> = emptyList()
)

private data class OAuthProvierOption(
    val name: String,
    val callback: String,
    val options: OAuth2ClientOptions
)