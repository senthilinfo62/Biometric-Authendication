package com.example.mybiomerticauthentication.ui.ViewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mybiomerticauthentication.ui.utils.Constants
import com.example.mybiomerticauthentication.ui.utils.Utilities
import java.util.concurrent.Executor

@RequiresApi(Build.VERSION_CODES.P)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context: Context = application.applicationContext
    private var executor: Executor = ContextCompat.getMainExecutor(context)
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private val _authenticationResult = MutableLiveData<String>()
    val authenticationResult: LiveData<String>
        get() = _authenticationResult

    fun authenticateWithBiometrics(activity: FragmentActivity) {
        initBiometricPrompt(activity)
        biometricPrompt.authenticate(promptInfo)
    }

    fun checkDeviceSecurityFeatures(): Boolean {
        return Utilities.deviceHasPasswordPinLock(context)
    }

    fun isBiometricHardwareAvailable(): Boolean {
        return Utilities.isBiometricHardWareAvailable(context)
    }

    private fun initBiometricPrompt(activity: FragmentActivity) {
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                _authenticationResult.postValue(Constants.AUTHENTICATION_ERROR + " " + errString)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                _authenticationResult.postValue(Constants.AUTHENTICATION_SUCCEEDED)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                _authenticationResult.postValue(Constants.AUTHENTICATION_FAILED)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val authFlag = DEVICE_CREDENTIAL or BIOMETRIC_STRONG
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(Constants.BIOMETRIC_AUTHENTICATION)
                .setSubtitle(Constants.BIOMETRIC_AUTHENTICATION_SUBTITLE)
                .setDescription(Constants.BIOMETRIC_AUTHENTICATION_DESCRIPTION)
                .setAllowedAuthenticators(authFlag)
                .build()
        } else {
            @Suppress("DEPRECATION")
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(Constants.PASSWORD_PIN_AUTHENTICATION)
                .setSubtitle(Constants.PASSWORD_PIN_AUTHENTICATION)
                .setDescription(Constants.PASSWORD_PIN_AUTHENTICATION_DESCRIPTION)
                .setDeviceCredentialAllowed(true)
                .build()
        }

        biometricPrompt = BiometricPrompt(activity, executor, callback)
    }

    override fun onCleared() {
        // Clean up resources if needed
        super.onCleared()
    }
}