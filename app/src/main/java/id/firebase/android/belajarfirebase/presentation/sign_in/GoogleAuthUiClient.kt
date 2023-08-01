package id.firebase.android.belajarfirebase.presentation.sign_in

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import id.firebase.android.belajarfirebase.R
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.concurrent.CancellationException

/**
 * Handles Google sign-in operations.
 *
 * @property context The application context.
 * @property oneTapClient The Google sign-in client.
 */
class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    // Firebase authentication instance
    private val auth = Firebase.auth

    /**
     * Initiates the sign-in process.
     *
     * @return The intent sender for the sign-in activity, or null if an error occurred.
     */
    suspend fun signIn(): IntentSender? {
        val result = try {
            // Begin the sign-in request
            oneTapClient.beginSignIn(
                beginSignInRequest()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        // Return the pending intent for the sign-in activity
        return result?.pendingIntent?.intentSender
    }

    /**
     * Handles the result of a sign-in intent.
     *
     * @param intent The sign-in intent.
     * @return The result of the sign-in operation.
     */
    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

        return try {
            // Sign in with the Google credential
            val user = auth.signInWithCredential(googleCredential).await().user
            // Return the sign-in result with the user's data
            SignInResult(
                data = user?.run {
                    UserData(
                        userId = uid,
                        username = displayName,
                        profilePicture = photoUrl?.toString(),
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            // Return the sign-in result with the error message
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    /**
     * Gets the currently signed-in user.
     *
     * @return The user's data, or null if no user is currently signed in.
     */
    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            username = displayName,
            profilePicture = photoUrl?.toString(),
        )
    }

    /**
     * Signs out the current user.
     */
    suspend fun signOut() {
        try {
            // Sign out from Google and Firebase
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    /**
     * Creates a sign-in request.
     *
     * @return The sign-in request.
     */
    private fun beginSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}
