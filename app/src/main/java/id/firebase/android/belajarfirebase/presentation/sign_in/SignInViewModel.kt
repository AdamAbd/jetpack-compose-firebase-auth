package id.firebase.android.belajarfirebase.presentation.sign_in

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel that represents the state of a sign-in operation.
 */
class SignInViewModel: ViewModel() {
    // MutableStateFlow that holds the current sign-in state
    private val _state = MutableStateFlow(SignInState())
    // Immutable version of _state that is exposed to other classes
    val state = _state.asStateFlow()

    /**
     * Updates the state based on a SignInResult.
     *
     * @param result The result of a sign-in operation.
     */
    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInErrorMessage = result.errorMessage,
        ) }
    }

    /**
     * Resets the state to its initial state.
     */
    fun resetState() {
        _state.update { SignInState() }
    }
}