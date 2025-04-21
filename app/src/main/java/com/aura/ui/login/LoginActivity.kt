package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import com.aura.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The login activity for the app.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()

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
            viewModel.isLoginEnabled.collect { isEnabled ->
                binding.login.isEnabled = isEnabled
            }
        }

        val updateLoginButton = {
            viewModel.onLoginFieldsChanged(
                identifier.text.toString(),
                password.text.toString()
            )
        }

        identifier.doAfterTextChanged { updateLoginButton() }
        password.doAfterTextChanged { updateLoginButton() }

        login.setOnClickListener {

            loading.visibility = View.VISIBLE

            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)

            finish()
        }

    }
}