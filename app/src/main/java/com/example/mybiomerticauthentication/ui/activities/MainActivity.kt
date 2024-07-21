package com.example.mybiomerticauthentication.ui.activities
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mybiomerticauthentication.ui.utils.Constants
import com.example.mybiomerticauthentication.ui.ViewModel.MainViewModel
import com.example.mybiomerticauthentication.R
import com.example.mybiomerticauthentication.databinding.ActivityMainBinding
import com.example.mybiomerticauthentication.ui.utils.Utilities


class MainActivity : AppCompatActivity() {
    private lateinit var activityContext: Context
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        activityContext = this

        if (Utilities.deviceHasPasswordPinLock(activityContext))
            binding.DeviceHasPINPasswordLock.text = Constants.TRUE
        else
            binding.DeviceHasPINPasswordLock.text = Constants.FALSE

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupUI()

        observeAuthenticationResult()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setupUI() {
        if (viewModel.checkDeviceSecurityFeatures())
           binding.DeviceHasPINPasswordLock.text = Constants.TRUE
        else
            binding.DeviceHasPINPasswordLock.text = Constants.FALSE

        if (viewModel.isBiometricHardwareAvailable()) {
            binding.DeviceHasBiometricFeatures.text = Constants.AVAILABLE
            binding.DeviceHasFingerPrint.text = Constants.TRUE
            binding.authenticatefingerprintbutton.isEnabled = true
        } else {
            binding.DeviceHasBiometricFeatures.text = Constants.UNAVAILABLE
            binding.DeviceHasFingerPrint.text = Constants.FALSE
            binding.authenticatefingerprintbutton.isEnabled = false

            if (viewModel.checkDeviceSecurityFeatures()) {
                binding.authenticatefingerprintbutton.isEnabled = true
                binding.authenticatefingerprintbutton.text = Constants.AUTHENTICATE_OTHER
            }
        }

        binding.authenticatefingerprintbutton.setOnClickListener {
            viewModel.authenticateWithBiometrics(this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun observeAuthenticationResult() {
        viewModel.authenticationResult.observe(this) { result ->
            when (result) {
                Constants.AUTHENTICATION_SUCCEEDED -> {
                    findViewById<TextView>(R.id.textViewAuthResult).visibility = View.VISIBLE
                    Utilities.showSnackBar(result, this)
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                Constants.AUTHENTICATION_FAILED, Constants.AUTHENTICATION_ERROR -> {
                    Utilities.showSnackBar(result, this)
                }

                else -> {
                    // Handle other cases as needed
                }
            }
        }
    }
}