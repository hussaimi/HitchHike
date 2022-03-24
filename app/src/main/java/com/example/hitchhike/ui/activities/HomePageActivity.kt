package com.example.hitchhike.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.hitchhike.R
import com.example.hitchhike.databinding.ActivityHomePageBinding
import com.google.firebase.auth.FirebaseAuth


class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDriver.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("userIs", "Driver")
            startActivity(intent)
        }

        binding.btnRider.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("userIs", "Rider")
            startActivity(intent)
        }
    }

    // added logout option in homepage.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.appbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logoutMenu -> {
                logout()
                true
            }
        }
        return true
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}