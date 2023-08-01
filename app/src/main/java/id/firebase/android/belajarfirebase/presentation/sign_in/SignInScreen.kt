package id.firebase.android.belajarfirebase.presentation.sign_in

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Represents the sign-in screen.
 *
 * @param state The state of the sign-in operation.
 * @param onSignInClick A function to be invoked when the Sign In button is clicked.
 */
@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit,
) {
    // Get the current context
    val context = LocalContext.current

    // Launch a side effect to show a Toast message whenever state.signInErrorMessage changes
    LaunchedEffect(key1 = state.signInErrorMessage) {
        state.signInErrorMessage?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    // Create a Box layout with a Sign In button in the center
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Create a Sign In button
        Button(onClick = onSignInClick) {
            Text(text = "Sign In")
        }
    }
}
