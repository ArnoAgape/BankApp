package com.aura.ui.login

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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * `LoginActivity` is the login screen of the application.
 * This screen validates user credentials and triggers the login process through the `LoginViewModel`.
 * If login is successful, it navigates to the `HomeActivity`, passing along the user's ID and account balance.
 *
 * This activity observes two flows from the `LoginViewModel`:
 * - `uiState`: for tracking loading state and user data
 * - `eventsFlow`: for one-time events like showing toast messages
 *
 * @see LoginViewModel
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    /**
     * The binding for the login layout.
     */
    private lateinit var binding: ActivityLoginBinding

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

    /**
     * Sets up the "Login" button to connect with an id and a password.
     * It triggers the data fetching logic from the ViewModel.
     */
    private fun setupLoginButton() {
        binding.login.setOnClickListener {
            hideKeyboard()
            viewModel.loginData(
                binding.identifier.text.toString(),
                binding.password.text.toString()
            )
        }
    }

    /**
     * Hides the soft keyboard from the screen.
     */
    private fun hideKeyboard() {
        currentFocus?.let {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    /**
     * Displays a short toast message on the screen.
     *
     * @param message The message to show.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Sets up text watchers for the identifier and password input fields.
     *
     * Every time the text in either field changes, this method updates the ViewModel
     * by calling `onLoginFieldsChanged(...)` to determine whether the login button should be enabled.
     *
     * This ensures real-time validation of the input fields.
     */
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

    /**
     * Observes the UI state flow from the ViewModel.
     * Updates the screen based on the current state: loading, success, or error.
     */
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

    /**
     * Observes one-time events from the ViewModel such as toast messages.
     */
    private fun setupEventsObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventsFlow.collect { event ->
                    when (event) {
                        is LoginEvent.ShowToast -> {
                            showToast(getString(event.message))
                        }
                    }
                }
            }
        }
    }

    /**
     * Inflates the options menu for the login screen.
     *
     * This method is called by the Android framework to initialize the contents of the Activity's options menu.
     * The menu resource `login_menu.xml` is used to define the menu layout.
     *
     * @param menu The options menu in which items are placed.
     * @return true to display the menu; false otherwise.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.login_menu, menu)
        return true
    }
}