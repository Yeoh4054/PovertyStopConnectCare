package com.example.povertystopconnectcare.DonationModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.povertystopconnectcare.R

class donationHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_home)

        val toDonorActivity : Button = findViewById(R.id.btnToDonor)
        val toDoneeActivity : Button = findViewById(R.id.btnToDonee)

        toDonorActivity.setOnClickListener { navDonorActivity() }
        toDoneeActivity.setOnClickListener { navDoneeActivity() }
    }

    private fun navDonorActivity(){
        val intent = Intent(this,donorHome::class.java )
        startActivity(intent)
    }

    private fun navDoneeActivity(){
        val intent = Intent(this,doneeHome::class.java )
        startActivity(intent)
    }
}