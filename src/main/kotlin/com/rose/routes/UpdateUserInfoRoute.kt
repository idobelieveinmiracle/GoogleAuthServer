package com.rose.routes

import com.rose.domain.model.ApiResponse
import com.rose.domain.model.Endpoint
import com.rose.domain.model.UserSession
import com.rose.domain.model.UserUpdate
import com.rose.domain.repository.UserDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

fun Route.updateUserInfoRoute(
    app: Application,
    userDataSource: UserDataSource
) {
    authenticate("auth-session") {
        put(Endpoint.UpdateUserInfo.path) {
            call.principal<UserSession>()?.let { userSession ->
                app.log.info("UserSession $userSession")
                val userUpdate = call.receive<UserUpdate>()
                try {
                    updateUserInfo(userSession.id, userUpdate, app, userDataSource)
                } catch (e: Exception) {
                    app.log.error("UPDATE FAILED $e")
                    call.respondRedirect(Endpoint.Unauthorized.path)
                }
            } ?: run {
                app.log.error("INVALID SESSION")
                call.respondRedirect(Endpoint.Unauthorized.path)
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.updateUserInfo(
    userId: String,
    userUpdate: UserUpdate,
    app: Application,
    userDataSource: UserDataSource
) {
    val success = userDataSource.updateUserInfo(
        userId,
        userUpdate.firstName,
        userUpdate.lastName
    )
    app.log.info("Update result: $success")
    call.respond(
        message = ApiResponse(
            success = success,
            user = if (success) userDataSource.getUserInfo(userId) else null
        ),
        status = if (success) HttpStatusCode.OK else HttpStatusCode.BadRequest
    )
}
