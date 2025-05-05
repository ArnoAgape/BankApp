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
import androidx.recyclerview.widget.LinearLayoutManager
import com.aura.R
import com.aura.databinding.ActivityHomeBinding
import com.aura.ui.adapter.BalanceAdapter
import com.aura.ui.domain.model.UserModel
import com.aura.ui.login.LoginActivity
import com.aura.ui.states.State
import com.aura.ui.transfer.TransferActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    /**
     * The binding for the home layout.
     */
    private lateinit var binding: ActivityHomeBinding

    /**
     * The adapter for the recycler view.
     */
    private val customAdapter = BalanceAdapter()

    /**
     * A callback for the result of starting the TransferActivity.
     */
    private val startTransferActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            //TODO
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loading = binding.loading
        val retry = binding.tryAgain
        val transfer = binding.transfer
        val errorMessage = binding.errorMessage

        defineRecyclerView()
        homeViewModel.getUserId(intent.getStringExtra(USER_ID).toString())

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.uiState.collect { state ->
                    when (state.result) {
                        State.Success -> {
                            updateCurrentBalance(state.balance)
                            loading.visibility = View.GONE
                            retry.visibility = View.INVISIBLE
                            errorMessage.visibility = View.INVISIBLE
                        }

                        State.Error.NoInternet -> {
                            loading.visibility = View.GONE
                            retry.visibility = View.VISIBLE
                            errorMessage.visibility = View.VISIBLE
                            Toast.makeText(
                                this@HomeActivity,
                                getString(R.string.no_internet),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        State.Error.Server -> {
                            loading.visibility = View.GONE
                            retry.visibility = View.VISIBLE
                            errorMessage.visibility = View.VISIBLE
                            Toast.makeText(
                                this@HomeActivity,
                                getString(R.string.error_server),
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

        retry.setOnClickListener {
            val userId = intent.getStringExtra(USER_ID)
            if (userId != null) {
                homeViewModel.getUserId(userId)
            }
        }


        transfer.setOnClickListener {
            val balance = mainUser?.balance ?: 0.0
            val userId = intent.getStringExtra(USER_ID) ?: return@setOnClickListener
            val intent = TransferActivity.newIntent(this, userId, balance)
            startTransferActivityForResult.launch(intent)
        }
    }

    private var mainUser: UserModel? = null
    private fun updateCurrentBalance(userDetails: List<UserModel>) {
        mainUser = userDetails.find { it.main }
        val mainAccounts = userDetails.filter { it.main }
        customAdapter.submitList(mainAccounts)
    }

    private fun defineRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = customAdapter
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
                    homeViewModel.getUserId(userId)
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
