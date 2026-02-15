package com.example.firebaselogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebaselogin.presentation.Dashboard
import com.example.firebaselogin.presentation.LoginScreen
import com.example.firebaselogin.ui.theme.FirebaseLoginTheme
import com.example.firebaselogin.ui.theme.OrangeFB
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        setContent {
            val isDark = isSystemInDarkTheme()
            LaunchedEffect(isDark) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { isDark },
                    navigationBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { isDark }
                )
            }

            var toastMessage by remember { mutableStateOf<String?>(null) }
            var toastIcon by remember { mutableStateOf<Int?>(null) }

            FirebaseLoginTheme {
                var isLoggedIn by remember { 
                    mutableStateOf(FirebaseAuth.getInstance().currentUser != null) 
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (isLoggedIn) {
                        Dashboard(onLogout = {
                            isLoggedIn = false
                            toastMessage = "Successfully logged out"
                        })
                    } else {
                        LoginScreen(onLoginSuccess = {
                            isLoggedIn = true
                            toastMessage = "Welcome back!"
                        })
                    }
                    CustomToast(
                        message = toastMessage,
                        iconRes = toastIcon,
                        onDismiss = { toastMessage = null }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomToast(
    message: String?,
    iconRes: Int? = null,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .background(Color(0xFF1F1F1F), RoundedCornerShape(25.dp))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = OrangeFB,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = message ?: "",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        LaunchedEffect(message) {
            if (message != null) {
                delay(3000)
                onDismiss()
            }
        }
    }
}