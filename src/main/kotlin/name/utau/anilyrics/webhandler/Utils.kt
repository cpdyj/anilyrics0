package name.utau.anilyrics.webhandler

import io.vertx.ext.web.RoutingContext

fun newHandler(lambda:(RoutingContext)->Unit)=lambda