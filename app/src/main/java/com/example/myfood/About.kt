package com.example.myfood

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.ComponentActivity

class About : ComponentActivity() {
    private val bugReport by lazy { findViewById<LinearLayout>(R.id.bugReport) }
    private val website by lazy { findViewById<LinearLayout>(R.id.website) }
    private val support by lazy { findViewById<LinearLayout>(R.id.support) }
    private val closeButton by lazy { findViewById<Button>(R.id.closeButton)  }

    companion object {
        private const val BUG_REPORT_URL = "https://github.com/Fidasek009/MyFood/issues"
        private const val WEBSITE_URL = "https://filipkrasa.kenolas.xyz"
        private const val SUPPORT_URL = "https://www.paypal.com/paypalme/Fidasek009"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        bugReport.setOnClickListener() {
            openUrl(BUG_REPORT_URL)
        }

        website.setOnClickListener() {
            openUrl(WEBSITE_URL)
        }

        support.setOnClickListener() {
            openUrl(SUPPORT_URL)
        }

        closeButton.setOnClickListener() {
            finish()
        }
    }

    private fun openUrl(url: String) {
        intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}