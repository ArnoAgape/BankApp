package com.aura.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.aura.ui.login.LoginViewModel
import com.aura.ui.transfer.TransferActivity
import kotlinx.coroutines.launch

/**
 * The home activity for the app.
 */
class HomeActivity : AppCompatActivity()
{

  private val homeViewModel: HomeViewModel by viewModels()
  private val loginViewModel: LoginViewModel by viewModels()

  /**
   * The binding for the home layout.
   */
  private lateinit var binding: ActivityHomeBinding

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

    val balance = binding.balance
    val transfer = binding.transfer

    balance.text = "2654,54€"

    transfer.setOnClickListener {
      startTransferActivityForResult.launch(Intent(this@HomeActivity, TransferActivity::class.java))
    }
  }

  private fun updateCurrentWeather(forecast: List<UserModel>) {
    customAdapter.submitList(forecast)
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
      else            -> super.onOptionsItemSelected(item)
    }
  }

}
