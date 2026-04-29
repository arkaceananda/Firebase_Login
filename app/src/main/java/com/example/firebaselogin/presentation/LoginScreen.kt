package com.example.firebaselogin.presentation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.firebaselogin.R
import com.example.firebaselogin.repository.getGoogleCredential
import com.google.firebase.auth.FirebaseAuth
import com.example.firebaselogin.ui.theme.*
import com.example.firebaselogin.utils.ExceptionHelper
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
                contentDescription = stringResource(R.string.firebase_logo_desc),
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
                        text = stringResource(R.string.greeting),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp,
                        color = PrimaryText,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(text = stringResource(R.string.email)) },
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
                        label = { Text(text = stringResource(R.string.password)) },
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
                                    contentDescription = stringResource(R.string.toggle_password_desc),
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
                                            Toast.makeText(context, context.getString(R.string.rpw), Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, context.getString(R.string.error_prefix) + it.message, Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(context, context.getString(R.string.fpw), Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = !isLoading
                        ) {
                            Text(
                                text = stringResource(R.string.fpassword),
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
                                        Toast.makeText(context, context.getString(R.string.error_prefix) + it.message, Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(context, context.getString(R.string.validationepw), Toast.LENGTH_SHORT).show()
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
                                val result = getGoogleCredential(context)
                                result.onSuccess { firebaseCredential ->
                                    if (firebaseCredential != null) {
                                        try {
                                            FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await()
                                            isLoading = false
                                            onLoginSuccess()
                                        } catch (e: Exception) {
                                            isLoading = false
                                            Toast.makeText(context, context.getString(R.string.error_prefix) + e.message, Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        isLoading = false
                                    }
                                }.onFailure { error ->
                                    isLoading = false
                                    val message = when(error) {
                                        is ExceptionHelper.NetworkError -> context.getString(R.string.validationepw) // Anda bisa sesuaikan stringnya
                                        else -> context.getString(R.string.error_prefix) + error.message
                                    }
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(listOf(OrangeFB, Color.Yellow))
                        ),
                        enabled = !isLoading
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(R.drawable.logo_google_icon_png), contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(stringResource(R.string.google), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                        }
                    }
                }
            }
        }
    }
}
