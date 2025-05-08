package com.aura.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.R
import com.aura.databinding.ActivityHomeBinding
import com.aura.ui.domain.model.UserModel
import com.aura.ui.login.LoginActivity
import com.aura.ui.states.State
import com.aura.ui.transfer.TransferActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()

    /**
     * The binding for the home layout.
     */
    private lateinit var binding: ActivityHomeBinding

    /**
     * A callback for the result of starting the TransferActivity.
     */
    private val startTransferActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            viewModel.getUserId(intent.getStringExtra(USER_ID).toString())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEventsObserver()
        setupUiStateObserver()
        setupRetryButton()
        setupTransferButton()
    }

    private fun setupTransferButton() {
        var mainUser: UserModel? = null
        binding.transfer.setOnClickListener {
            val balance = mainUser?.balance ?: 0.0
            val userId = intent.getStringExtra(USER_ID) ?: return@setOnClickListener
            val intent = TransferActivity.newIntent(this, userId, balance)
            startTransferActivityForResult.launch(intent)
        }
    }

    private fun setupRetryButton() {
        binding.tryAgain.setOnClickListener {
            val userId = intent.getStringExtra(USER_ID)
            if (userId != null) {
                viewModel.getUserId(userId)
                binding.amount.visibility = View.VISIBLE
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupUiStateObserver() {
        viewModel.getUserId(intent.getStringExtra(USER_ID).toString())
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.loading.visibility =
                        if (state.result == State.Loading) View.VISIBLE else View.GONE
                    binding.tryAgain.visibility = View.VISIBLE
                    when (state.result) {
                        State.Success -> {
                            updateCurrentBalance(state.balance)
                            binding.loading.visibility = View.GONE
                            binding.tryAgain.visibility = View.INVISIBLE
                        }

                        State.Error.NoInternet ->
                            showToast(R.string.no_internet.toString())

                        State.Error.Server ->
                            showToast(R.string.error_server.toString())

                        State.Loading -> {
                            binding.loading.visibility = View.VISIBLE
                        }

                        else -> {
                            binding.loading.visibility = View.GONE
                        }
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
                        is HomeEvent.ShowToast -> {
                            showToast(getString(event.message))
                        }
                    }
                }
            }
        }
    }

    private fun updateCurrentBalance(userDetails: List<UserModel>) {
        val mainAccount = userDetails.find { it.main }
        val balance = mainAccount?.balance ?: 0.0
        binding.balance.text
        binding.amount.text = getString(R.string.balance_display, balance)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.disconnect -> {
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finish()
                true
            }

            R.id.reload -> {
                val userId = intent.getStringExtra(USER_ID)
                if (userId != null) {
                    viewModel.getUserId(userId)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun startActivity(context: Context, userId: String, balance: Double) {
            val intent = Intent(context, HomeActivity::class.java).apply {
                putExtra(USER_ID, userId)
                putExtra(BALANCE, balance)
            }
            context.startActivity(intent)
        }

        const val USER_ID = "userId"
        const val BALANCE = "balance"
    }

}
