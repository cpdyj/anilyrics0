package name.utau.anilyrics

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import name.utau.anilyrics.webhandler.logoutHandler


fun newRouteMap(vertx: Vertx) = Router.router(vertx).apply {
    route("/logout").handler(logoutHandler())
}


