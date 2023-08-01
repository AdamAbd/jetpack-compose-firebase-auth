package id.firebase.android.belajarfirebase.presentation.sign_in

/**
 * Represents the state of a sign-in operation.
 *
 * @property isSignInSuccessful True if the sign-in operation was successful, false otherwise.
 * @property signInErrorMessage The error message if the sign-in operation failed, null otherwise.
 */
data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInErrorMessage: String? = null,
)
