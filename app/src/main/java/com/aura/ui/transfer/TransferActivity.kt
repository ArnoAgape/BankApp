package com.aura.ui.transfer

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.aura.databinding.ActivityTransferBinding
import com.aura.ui.home.HomeActivity
import com.aura.ui.home.HomeActivity.Companion.USER_ID
import com.aura.ui.login.LoginActivity
import com.aura.ui.login.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

/**
 * The transfer activity for the app.
 */

@AndroidEntryPoint
class TransferActivity : AppCompatActivity() {

    private val transferViewModel: TransferViewModel by viewModels()

    /**
     * The binding for the transfer layout.
     */
    private lateinit var binding: ActivityTransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recipient = binding.recipient
        val amount = binding.amount
        val transfer = binding.transfer
        val loading = binding.loading

        transfer.isEnabled = false

        val userId = intent.getStringExtra(USER_ID) ?: ""
        Log.d("fetch", userId)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Réactive le bouton selon les champs
                launch {
                    transferViewModel.uiState.collect { state ->
                        transfer.isEnabled = state.isTransferEnabled
                    }
                }

                // Écoute l'état de connexion
                launch {
                    transferViewModel.uiState.collect { state ->
                        when (state.result) {
                            State.Success -> {
                                Toast.makeText(
                                    this@TransferActivity,
                                    getString(R.string.transfer_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                val userId = USER_ID
                                HomeActivity.startActivity(this@TransferActivity, userId)
                                setResult(RESULT_OK)
                                finish()
                            }

                            State.Error.InsufficientBalance -> {
                                loading.visibility = View.GONE
                                Toast.makeText(
                                    this@TransferActivity,
                                    getString(R.string.transfer_failed),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            State.Error.UnknownId -> {
                                loading.visibility = View.GONE
                                Toast.makeText(
                                    this@TransferActivity,
                                    getString(R.string.id_recipient_fail),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            State.Error.Server -> {
                                loading.visibility = View.GONE
                                Toast.makeText(
                                    this@TransferActivity,
                                    getString(R.string.error_server),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            State.Error.NoInternet -> {
                                loading.visibility = View.GONE
                                Toast.makeText(
                                    this@TransferActivity,
                                    getString(R.string.no_internet),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            State.Loading -> loading.visibility = View.VISIBLE

                            else -> {
                                loading.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

        transfer.setOnClickListener {
            loading.visibility = View.VISIBLE

            // cache le clavier
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            // appel de la fonction de transfert
            transferViewModel.transferData(
                userId,
                recipient.text.toString(),
                amount.text.toString()
            )

            setResult(RESULT_OK)
            finish()
        }
        val updateTransferButton = {
            transferViewModel.onLoginFieldsChanged(
                recipient.text.toString(),
                amount.text.toString()
            )
        }

        recipient.doAfterTextChanged { updateTransferButton() }
        amount.doAfterTextChanged { updateTransferButton() }

    }
    companion object {
        const val USER_ID = "userId"
    }
}
