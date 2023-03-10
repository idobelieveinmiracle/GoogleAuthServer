package com.rose.routes

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.rose.common.Constants
import com.rose.domain.model.ApiRequest
import com.rose.domain.model.Endpoint
import com.rose.domain.model.User
import com.rose.domain.model.UserSession
import com.rose.domain.repository.UserDataSource
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*


fun Route.tokenVerificationRoute(app: Application, userDataSource: UserDataSource) {
    post(Endpoint.TokenVerification.path) {
        val request = call.receive<ApiRequest>()
        if (request.tokenId.isNotEmpty()) {
            val result = verifyGoogleTokenId(request.tokenId)
            if (result != null) {
                app.log.info("Received token $result")
                saveUserToDatabase(app, result, userDataSource)
            } else {
                app.log.info("Token verification failed")
                call.respondRedirect(Endpoint.Unauthorized.path)
            }
        } else {
            app.log.info("Token empty token")
            call.respondRedirect(Endpoint.Unauthorized.path)
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.saveUserToDatabase(
    app: Application,
    result: GoogleIdToken,
    userDataSource: UserDataSource
) {
    val sub = result.payload["sub"].toString()
    val name = result.payload["name"].toString()
    val email = result.payload["email"].toString()
    val profilePhoto = result.payload["picture"].toString()

    val user = User(
        id = sub,
        name = name,
        emailAddress = email,
        profilePhoto = profilePhoto,
    )

    val response = userDataSource.saveUserInfo(user)

    if (response) {
        app.log.info("USER SUCCESSFULLY SAVED/RETRIEVED")
        call.sessions.set(UserSession(sub, name))
        call.respondRedirect(Endpoint.Authorized.path)
    } else {
        app.log.error("ERROR SAVING USER")
        call.respondRedirect(Endpoint.Unauthorized.path)
    }

}

fun verifyGoogleTokenId(tokenId: String): GoogleIdToken? {
    return try {
        val verifier = GoogleIdTokenVerifier.Builder(
            NetHttpTransport(), GsonFactory()
        ).setAudience(listOf(Constants.AUDIENCE))
            .setIssuer(Constants.ISSUER)
            .build()

        verifier.verify(tokenId)
    } catch (e: Exception) {
        null
    }
}