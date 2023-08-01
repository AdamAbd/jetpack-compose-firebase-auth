package id.firebase.android.belajarfirebase

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import id.firebase.android.belajarfirebase.presentation.sign_in.GoogleAuthUiClient
import id.firebase.android.belajarfirebase.presentation.sign_in.SignInScreen
import id.firebase.android.belajarfirebase.presentation.sign_in.SignInViewModel
import id.firebase.android.belajarfirebase.profile.ProfileScreen
import id.firebase.android.belajarfirebase.ui.theme.BelajarFirebaseTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    // Lazily initialize GoogleAuthUiClient
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Apply the custom theme for the app
            BelajarFirebaseTheme {
                // A surface container that applies material design styles
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Create and remember a NavController
                    val navController = rememberNavController()
                    // A composable that hosts the navigation graph
                    NavHost(navController = navController, startDestination = "sign_in") {
                        // Define the sign_in destination
                        composable("sign_in") {
                            // Instantiate the ViewModel
                            val viewModel = viewModel<SignInViewModel>()
                            // Collect the ViewModel state
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            // Launch a Coroutine to check if user is already signed in
                            LaunchedEffect(key1 = Unit) {
                                if(googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate("profile")
                                }
                            }

                            // Hook into the Activity Result API
                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if(result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            // Handle the sign-in result
                                            viewModel.onSignInResult(signInResult)
                                        }
                                    }
                                }
                            )

                            // Launch a Coroutine to check if sign-in is successful
                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if(state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("profile")
                                    // Reset the ViewModel state
                                    viewModel.resetState()
                                }
                            }

                            // Display the SignInScreen with ViewModel state and onSignInClick function
                            SignInScreen(
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        // Launch the activity for result
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                        }

                        // Define the profile destination
                        composable("profile") {
                            // Display the ProfileScreen with the user's data and a function to handle sign-outs
                            ProfileScreen(
                                // Get the signed-in user's data
                                userData = googleAuthUiClient.getSignedInUser(),
                                // Define what happens when the user signs out
                                onSignOut = {
                                    lifecycleScope.launch {
                                        // Sign out the user
                                        googleAuthUiClient.signOut()
                                        // Show a toast message to confirm sign-out
                                        Toast.makeText(
                                            applicationContext,
                                            "Signed out",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        // Navigate back to the previous screen
                                        navController.popBackStack()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
