package com.example.firebaselogin.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebaselogin.R
import com.example.firebaselogin.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

@Composable
fun Dashboard(onLogout: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val displayName = user?.displayName ?: user?.email ?: stringResource(R.string.user_placeholder)

    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val timeGreeting = when (hour) {
        in 0..11 -> R.string.pagi
        in 12..14 -> R.string.siang
        in 15..18 -> R.string.sore
        else -> R.string.malam
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightCream)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = timeGreeting),
            fontSize = 18.sp,
            color = SecondaryText
        )
        Text(
            text = displayName,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = user?.email ?: "",
            fontSize = 14.sp,
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                auth.signOut()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RedAccent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.exit_logout_2857), 
                contentDescription = null, 
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.signout), 
                fontSize = 16.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color.White
            )
        }
    }
}
