package com.aura.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.aura.ui.domain.model.UserModel
import com.aura.ui.login.LoginActivity
import com.aura.ui.transfer.TransferActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The home activity for the app.
 */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), BalanceAdapter.OnItemClickListener {

  private val homeViewModel: HomeViewModel by viewModels()

  /**
   * The binding for the home layout.
   */
  private lateinit var binding: ActivityHomeBinding

  /**
   * The adapter for the recycler view.
   */
  private val customAdapter = BalanceAdapter(this)

  /**
   * A callback for the result of starting the TransferActivity.
   */
  private val startTransferActivityForResult =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
      //TODO
    }

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityHomeBinding.inflate(layoutInflater)
    setContentView(binding.root)
    defineRecyclerView()

    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.STARTED) {
        homeViewModel.uiState.collect {
          updateCurrentBalance(it.balance)
        }
      }
    }

    val transfer = binding.transfer

    transfer.setOnClickListener {
      startTransferActivityForResult.launch(Intent(this@HomeActivity, TransferActivity::class.java))
    }
  }

  private fun updateCurrentBalance(balance: List<UserModel>) {
    customAdapter.submitList(balance)
  }

  private fun defineRecyclerView() {
    val layoutManager = LinearLayoutManager(applicationContext)
    binding.recyclerView.layoutManager = layoutManager
    binding.recyclerView.adapter = customAdapter
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean
  {
    menuInflater.inflate(R.menu.home_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean
  {
    return when (item.itemId)
    {
      R.id.disconnect ->
      {
        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
        finish()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onItemClick(item: UserModel) {
    Toast.makeText(this, "Ceci est le compte ${item.main}", Toast.LENGTH_SHORT)
      .show()
  }

}
