package com.example.biometricpractice

import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.biometricpractice.databinding.ActivityMainBinding
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    companion object {
        const val INTENT_KEY = "intent_key"
        const val INTENT_VALUE = 1055
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var executor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = getBiometricPrompt()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toast.makeText(this, "For lower than Android 6", Toast.LENGTH_SHORT).show()
            val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            if (keyguardManager.isKeyguardSecure) {
                val dCIntent = keyguardManager.createConfirmDeviceCredentialIntent(
                    "Confirm your credentials please!",
                    "Need to verify if it's you"
                )
                Timer().schedule(
                    object: TimerTask() {
                        override fun run() {
                            runOnUiThread {
                                startActivityForResult(dCIntent, INTENT_VALUE)
                            }
                        }
                    },
                    2000
                )
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(this, "For Android 6 to Android 10", Toast.LENGTH_SHORT).show()
            val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            if (keyguardManager.isDeviceSecure) {
                val dCIntent = keyguardManager.createConfirmDeviceCredentialIntent(
                    "Confirm your credentials please!",
                    "Need to verify if it's you"
                )
                Timer().schedule(
                    object: TimerTask() {
                        override fun run() {
                            runOnUiThread {
                                startActivityForResult(dCIntent, INTENT_VALUE)
                            }
                        }
                    },
                    2000
                )
            }
        }

        else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            Toast.makeText(this, "For Android 10", Toast.LENGTH_SHORT).show()
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication for your app")
                .setDescription("Need to verify your credentials")
                .setDeviceCredentialAllowed(true)
                .build()
            Timer().schedule(
                object: TimerTask() {
                    override fun run() {
                        runOnUiThread {
                            biometricPrompt.authenticate(promptInfo)
                        }
                    }
                },
                2000
            )
        }

        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Toast.makeText(this, "For Android 11 or higher", Toast.LENGTH_SHORT).show()
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication for your app")
                .setDescription("Need to verify your credentials")
                .setAllowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build()
            Timer().schedule(
                object: TimerTask() {
                    override fun run() {
                        runOnUiThread {
                            biometricPrompt.authenticate(promptInfo)
                        }
                    }
                },
                2000
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INTENT_VALUE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Yoohoo! Verified credentials!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getBiometricPrompt(): BiometricPrompt {
        return BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
    }
}