package com.example.povertystopconnectcare.DonationModule.addDonation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.povertystopconnectcare.DonationModule.donationHome
import com.example.povertystopconnectcare.R

class AddDonationRequest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_donation_request)

        val toBankDetails: Button = findViewById(R.id.btnBankDetails)
        toBankDetails.setOnClickListener { navBankDetails() }
    }

    private fun navBankDetails() {
        val intent = Intent(this,bankDetails::class.java)
        startActivity(intent)
    }
}