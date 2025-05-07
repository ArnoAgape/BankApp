package com.aura.ui.transfer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.databinding.ActivityTransferBinding
import com.aura.ui.states.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

/**
 * The transfer activity for the app.
 */

@AndroidEntryPoint
class TransferActivity : AppCompatActivity() {

    private val viewModel: TransferViewModel by viewModels()
    private lateinit var binding: ActivityTransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra(USER_ID) ?: ""
        val balance = intent.getDoubleExtra(BALANCE, 0.0)

        setupTextWatchers()
        setupUiStateObserver()
        setupEventsObserver()
        setupTransferButton(userId, balance)
    }

    private fun setupTextWatchers() {
        val update = {
            viewModel.onLoginFieldsChanged(
                binding.recipient.text.toString(),
                binding.amount.text.toString()
            )
        }
        binding.recipient.doAfterTextChanged { update() }
        binding.amount.doAfterTextChanged { update() }
    }

    private fun setupUiStateObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.transfer.isEnabled = state.isTransferEnabled
                    binding.loading.visibility = if (state.result == State.Loading) View.VISIBLE else View.GONE

                    if (state.result == State.Success) {
                        setResult(RESULT_OK)
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
                        is LoginEvent.ShowToast -> {
                            showToast(getString(event.message))
                        }
                    }
                }
            }
        }
    }

    private fun setupTransferButton(userId: String, balance: Double) {
        binding.transfer.setOnClickListener {
            hideKeyboard()
            viewModel.transferData(
                userId,
                binding.recipient.text.toString(),
                binding.amount.text.toString(),
                balance.toString()
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

    companion object {
        const val USER_ID = "userId"
        const val BALANCE = "balance"

        fun newIntent(context: Context, userId: String, balance: Double): Intent {
            return Intent(context, TransferActivity::class.java).apply {
                putExtra(USER_ID, userId)
                putExtra(BALANCE, balance)
            }
        }
    }
}
