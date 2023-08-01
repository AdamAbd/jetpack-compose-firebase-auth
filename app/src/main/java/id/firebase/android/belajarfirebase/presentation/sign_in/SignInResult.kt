package id.firebase.android.belajarfirebase.presentation.sign_in

/**
 * Represents the result of a sign-in operation.
 *
 * @property data The user's data, if the sign-in was successful.
 * @property errorMessage The error message, if the sign-in failed.
 */
data class SignInResult(
    val data: UserData?,
    val errorMessage: String?,
)

/**
 * Holds data about a user.
 *
 * @property userId The unique ID of the user.
 * @property username The username of the user. It can be null if the user did not set a username.
 * @property profilePicture The URL of the user's profile picture. It can be null if the user did not set a profile picture.
 */
data class UserData(
    val userId: String,
    val username: String?,
    val profilePicture: String?
)
