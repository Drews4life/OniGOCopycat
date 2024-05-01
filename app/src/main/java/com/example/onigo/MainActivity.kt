package com.example.onigo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object AppColors {
    val Default: Color = Color(0xFFEB6419)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnigoCopycat()
        }
    }
}

@Composable
fun OnigoCopycat() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "phoneNumber") {
        composable("phoneNumber") {
            PhoneNumberScreen(navController)
        }
        composable("verificationCode") {
            VerificationCodeScreen(navController)
        }
        composable("successScreen") {
            SuccessScreen()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PhoneNumberScreen(navController: NavHostController) {
    var phoneNumber by remember { mutableStateOf("") }
    val isValid by remember(phoneNumber) { derivedStateOf { phoneNumber.length == 10 } }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isValid) AppColors.Default else Color.Gray
                ),
                onClick = {
                    if (isValid) {
                        navController.navigate("verificationCode")
                        keyboardController?.hide()
                    }
                },
                enabled = isValid
            ) {
                Text(
                    text = "Submit",
                    color = if (isValid) Color.White else Color.Black
                )
            }
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OniGOTextInput(
                value = phoneNumber,
                onValueChange = { phoneNumber = sanitizePhone(it) },
                keyboardType = KeyboardType.Phone,
                label = "Phone number",
            )
        }
    }
}

@Composable
fun VerificationCodeScreen(navController: NavHostController) {
    var smsCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OniGOTextInput(
            value = smsCode,
            onValueChange = {
                smsCode = it
                if (it.length == 6) {
                    navController.navigate("successScreen")
                }
            },
            label = "Enter SMS Code",
            keyboardType = KeyboardType.NumberPassword
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OniGOTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    imeAction: ImeAction = ImeAction.Default,
    onImeAction: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
                keyboardController?.hide()
            }
        ),
        singleLine = true,
        label = { Text(label) },
        modifier = Modifier.padding(horizontal = 16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = AppColors.Default,
            cursorColor = AppColors.Default,
            focusedLabelColor = AppColors.Default,
            focusedIndicatorColor = AppColors.Default,
        )
    )
}

@Composable
fun SuccessScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Default)
    ) {
        Text("Welcome to OniGO!", fontSize = 24.sp, color = Color.Black)
    }
}

fun sanitizePhone(phone: String): String {
    val cleanedNumber = phone.filter { it.isDigit() }

    return cleanedNumber
}
