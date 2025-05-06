package com.aura.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.R
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import com.aura.ui.states.State
import com.aura.ui.transfer.TransferActivity.Companion.BALANCE
import com.aura.ui.transfer.TransferEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The login activity for the app.
 */

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    /**
     * The binding for the login layout.
     */
    private lateinit var binding: ActivityLoginBinding

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.isEnabled = false

        setupTextWatchers()
        setupUiStateObserver()
        setupEventsObserver()
        setupLoginButton()
    }

    private fun setupLoginButton() {
        binding.login.setOnClickListener {
            hideKeyboard()
            viewModel.loginData(
                binding.identifier.text.toString(),
                binding.password.text.toString()
            )
        }
    }

    private fun hideKeyboard() {
        currentFocus?.let {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupTextWatchers() {
        val update = {
            viewModel.onLoginFieldsChanged(
                binding.identifier.text.toString(),
                binding.password.text.toString()
            )
        }
        binding.identifier.doAfterTextChanged { update() }
        binding.password.doAfterTextChanged { update() }
    }

    private fun setupUiStateObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.login.isEnabled = state.isLoginEnabled
                    binding.loading.visibility = if (state.result == State.Loading) View.VISIBLE else View.GONE

                    if (state.result == State.Success) {
                        val userId = binding.identifier.text.toString()
                        val balance = intent.getDoubleExtra(BALANCE, 0.0)
                        HomeActivity.startActivity(this@LoginActivity, userId, balance)
                        finish()
                    }
                }
            }
        }
    }

    private fun setupEventsObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventsFlow.collect { event ->
                    when (event) {
                        is TransferEvent.ShowToast -> {
                            showToast(getString(event.message))
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.login_menu, menu)
        return true
    }
}