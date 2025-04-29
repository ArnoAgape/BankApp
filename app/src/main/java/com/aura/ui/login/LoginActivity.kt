package com.aura.ui.login

import android.os.Bundle
import android.view.View
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The login activity for the app.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    /**
     * The binding for the login layout.
     */
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val login = binding.login
        val loading = binding.loading
        val password = binding.password
        val identifier = binding.identifier

        login.isEnabled = false

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Réactive le bouton selon les champs
                launch {
                    loginViewModel.uiState.collect { state ->
                        login.isEnabled = state.isLoginEnabled
                    }
                }

                // Écoute l'état de connexion
                launch {
                    loginViewModel.uiState.collect { state ->
                        when (state.result) {
                            LoginState.Success -> {
                                loading.visibility = View.VISIBLE
                                Toast.makeText(
                                    this@LoginActivity,
                                    getString(R.string.login_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                val userId = identifier.text.toString()
                                HomeActivity.startActivity(this@LoginActivity, userId)
                                finish()
                            }

                            LoginState.Error -> {
                                loading.visibility = View.GONE
                                Toast.makeText(
                                    this@LoginActivity,
                                    getString(R.string.login_fail),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            LoginState.NoInternet -> {
                                loading.visibility = View.GONE
                                Toast.makeText(
                                    this@LoginActivity,
                                    getString(R.string.no_internet),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            LoginState.Loading -> loading.visibility = View.VISIBLE

                            else -> {
                                loading.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

        val updateLoginButton = {
            loginViewModel.onLoginFieldsChanged(
                identifier.text.toString(),
                password.text.toString()
            )
        }

        identifier.doAfterTextChanged { updateLoginButton() }
        password.doAfterTextChanged { updateLoginButton() }

        login.setOnClickListener {
            loginViewModel.loginData(identifier.text.toString(), password.text.toString())
        }
    }
}