package com.example.chat

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chat.feature.auth.signin.SignInScreen
import com.example.chat.feature.auth.signup.SignUpScreen
import com.example.chat.feature.chat.ChatScreen
import com.example.chat.feature.home.HomeScreen
import com.example.chat.feature.signInGoogle.GoogleAuthUiClient
import com.example.chat.feature.signInGoogle.SignInGoogleViewModel
import com.example.chat.ui.theme.ChatTheme
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatTheme {
                Surface(modifier = Modifier.fillMaxSize()) {


                    val navController = rememberNavController()
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val start = if (currentUser != null) "home" else "login"
                    NavHost(navController = navController, startDestination = start) {

                        composable("login") {
                            val viewModel = viewModel<SignInGoogleViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = Unit) {
                                if(googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate("home")
                                }
                            }

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if(result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                        }
                                    }
                                }
                            )

                            val context = LocalContext.current
                            LaunchedEffect(key1 = state.signInError) {
                                state.signInError?.let { error ->
                                    Toast.makeText(
                                        context,
                                        error,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }


                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if(state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("home")
                                    viewModel.resetState()
                                }
                            }

                            SignInScreen(navController,onSignInClick = {

                                lifecycleScope.launch {
                                    val signInIntentSender = googleAuthUiClient.signIn()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }

                            })
                        }
                        composable("signup") {
                            SignUpScreen(navController)
                        }
                        composable("home") {
                            HomeScreen(navController)
                        }
                        composable("chat/{channelId}", arguments = listOf(
                            navArgument("channelId") {
                                type = NavType.StringType
                            }
                        )){
                            val channelId = it.arguments?.getString("channelId")?:""
                            ChatScreen(navController,channelId)
                        }
                    }
                }
            }
        }
    }
}

