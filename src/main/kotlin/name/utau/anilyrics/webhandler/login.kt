package name.utau.anilyrics.webhandler

import io.vertx.ext.web.RoutingContext


fun logoutHandler()= newHandler {
    it.setUser(null)
    // todo: revoke user
    it.redirect("/")
}
