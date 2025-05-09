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
import com.aura.ui.home.HomeActivity

/**
 * `TransferActivity` allows the user to send money to another account.
 *
 * This screen is launched from the `HomeActivity`, receiving the current user's ID and balance.
 * It lets the user input a recipient ID and transfer amount, and initiate a money transfer
 * via the `TransferViewModel`.
 *
 * The activity observes:
 * - `uiState`: to control UI elements and respond to the result of the transfer operation
 *
 * Transfer success, errors (insufficient balance, server error, invalid recipient), and network issues
 * are all handled and shown to the user via toasts and progress indicators.
 *
 * @see TransferViewModel
 * @see HomeActivity
 */
@AndroidEntryPoint
class TransferActivity : AppCompatActivity() {

    private val viewModel: TransferViewModel by viewModels()

    /**
     * The binding for the transfer layout.
     */
    private lateinit var binding: ActivityTransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra(USER_ID) ?: ""

        setupTextWatchers()
        setupUiStateObserver()
        setupEventsObserver()
        setupTransferButton(userId)
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
                binding.recipient.text.toString(),
                binding.amount.text.toString()
            )
        }
        binding.recipient.doAfterTextChanged { update() }
        binding.amount.doAfterTextChanged { update() }
    }

    /**
     * Observes the UI state flow from the ViewModel.
     * Updates the screen based on the current state: loading, success, or error.
     */
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

    /**
     * Observes one-time events from the ViewModel such as toast messages.
     */
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

    /**
     * Sets up the click listener for the "Transfer" button.
     *
     * When the button is clicked:
     * - The keyboard is hidden
     * - The recipient ID and amount are extracted from the input fields
     * - The `transferData(...)` method in the ViewModel is called to initiate the transfer
     *
     * @param userId The ID of the user performing the transfer (sender).
     */
    private fun setupTransferButton(userId: String) {
        binding.transfer.setOnClickListener {
            hideKeyboard()
            viewModel.transferData(
                userId,
                binding.recipient.text.toString(),
                binding.amount.text.toString()
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
     * Static method to start this activity from another context,
     * passing the user ID and their balance as extras.
     */
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
