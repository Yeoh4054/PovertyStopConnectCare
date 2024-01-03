package com.example.povertystopconnectcare.DonationModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.povertystopconnectcare.DonationModule.addDonation.AddDonationRequest
import com.example.povertystopconnectcare.R

class doneeHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donee_home)

        val toAddDonationRequestActivity : Button = findViewById(R.id.btnAddDonationRequest)

        toAddDonationRequestActivity.setOnClickListener { navAddDonationRequest() }
    }

    private fun navAddDonationRequest() {
        val intent = Intent(this, AddDonationRequest::class.java)
        startActivity(intent)
    }

}