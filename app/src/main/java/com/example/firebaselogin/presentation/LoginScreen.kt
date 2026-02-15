package com.example.firebaselogin.presentation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.firebaselogin.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.firebaselogin.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val emailColor = if (email.isNotEmpty()) OrangeFB else SecondaryText
    val passwordColor = if (password.isNotEmpty()) OrangeFB else SecondaryText

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(LightCream, SoftPeach)))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Image(
                painter = painterResource(id = R.drawable.primary_vertical_lockup_full_color),
                contentDescription = "Firebase Logo",
                modifier = Modifier.size(140.dp),
            )
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Let's Sign In.",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        color = PrimaryText,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                tint = if (isLoading) SecondaryText else emailColor
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PrimaryText,
                            unfocusedTextColor = PrimaryText,
                            focusedBorderColor = OrangeFB,
                            unfocusedBorderColor = WarmGray,
                            focusedLabelColor = OrangeFB,
                            unfocusedLabelColor = SecondaryText,
                            disabledTextColor = PrimaryText,
                            disabledBorderColor = WarmGray
                        ),
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = if (isLoading) SecondaryText else passwordColor
                            )
                        },
                        trailingIcon = {
                            val iconImage = if (isPasswordVisible) {
                                painterResource(R.drawable.eye_solid_full)
                            } else {
                                painterResource(R.drawable.eye_low_vision_solid_full)
                            }

                            IconButton(
                                onClick = { isPasswordVisible = !isPasswordVisible },
                                enabled = !isLoading
                            ) {
                                Icon(
                                    painter = iconImage,
                                    contentDescription = "Toggle Password",
                                    modifier = Modifier.size(24.dp),
                                    tint = if (isLoading) SecondaryText else passwordColor
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PrimaryText,
                            unfocusedTextColor = PrimaryText,
                            focusedBorderColor = OrangeFB,
                            unfocusedBorderColor = WarmGray,
                            focusedLabelColor = OrangeFB,
                            unfocusedLabelColor = SecondaryText,
                            disabledTextColor = PrimaryText,
                            disabledBorderColor = WarmGray
                        ),
                        enabled = !isLoading
                    )

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        TextButton(
                            onClick = {
                                if (email.isNotEmpty()) {
                                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Reset email sent", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(context, "Enter your email first.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = !isLoading
                        ) {
                            Text(
                                text = stringResource(R.string.password),
                                color = if (isLoading) SecondaryText else OrangeFB,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if(email.isNotEmpty() && password.isNotEmpty()) {
                                isLoading = true
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        onLoginSuccess()
                                    }
                                    .addOnFailureListener {
                                        isLoading = false
                                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeFB,
                            disabledContainerColor = OrangeFB.copy(alpha = 0.6f)
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text(stringResource(R.string.signin), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                val firebaseCredential = getGoogleCredential(context)
                                if (firebaseCredential != null) {
                                    try {
                                        FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await()
                                        isLoading = false
                                        onLoginSuccess()
                                    } catch (e: Exception) {
                                        isLoading = false
                                        Toast.makeText(context, "Auth Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberFB),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryText)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = OrangeFB, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(
                                painterResource(id = R.drawable.logo_google_icon_png),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(20.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(stringResource(R.string.google), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .clickable(enabled = false) {}
            )
        }
    }
}

private suspend fun getGoogleCredential(context: Context): com.google.firebase.auth.AuthCredential? {
    try {
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("65917856408-qckfoibadlqevai0s7rhaso4forjouap.apps.googleusercontent.com")
            .setAutoSelectEnabled(true)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        val result = credentialManager.getCredential(context, request)
        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            return GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
        }
    } catch (e: Exception) {
        Log.e("Auth", "Error: ${e.message}")
    }
    return null
}