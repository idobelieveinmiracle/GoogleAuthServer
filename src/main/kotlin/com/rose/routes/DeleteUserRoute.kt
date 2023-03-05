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
import io.ktor.server.sessions.*

fun Route.deleteUserRoute(
    app: Application,
    userDataSource: UserDataSource
) {
    authenticate("auth-session") {
        delete(Endpoint.DeleteUser.path) {
            call.principal<UserSession>()?.let { userSession ->
                val result = try {
                    call.sessions.clear<UserSession>()
                    userDataSource.deleteUser(userSession.id)
                } catch (e: Exception) {
                    app.log.error("DELETING USER ERROR $e")
                    false
                }
                call.respond(
                    message = ApiResponse(
                        success = result
                    ),
                    status = if (result) HttpStatusCode.OK else HttpStatusCode.BadRequest
                )
            } ?: run {
                app.log.error("INVALID SESSION")
                call.respondRedirect(Endpoint.Unauthorized.path)
            }
        }
    }
}
