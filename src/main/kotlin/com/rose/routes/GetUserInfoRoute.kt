package com.rose.routes

import com.rose.domain.model.ApiResponse
import com.rose.domain.model.Endpoint
import com.rose.domain.model.UserSession
import com.rose.domain.repository.UserDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getUserInfoRoute(
    app: Application,
    userDataSource: UserDataSource
) {
    authenticate("auth-session") {
        get(Endpoint.GetUserInfo.path) {
            val userSession = call.principal<UserSession>()
            if (userSession == null) {
                app.log.info("INVALID SESSION")
                call.respondRedirect(Endpoint.Unauthorized.path)
            } else {
                try {
                    call.respond(message = ApiResponse(
                        success = true,
                        user = userDataSource.getUserInfo(userSession.id)
                    ), status = HttpStatusCode.OK)
                } catch (e: Exception) {
                    app.log.error("UNAUTHORIZED ENDPOINT")
                    call.respondRedirect(Endpoint.Authorized.path)
                }
            }
        }
    }
}
