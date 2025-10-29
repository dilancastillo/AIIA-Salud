package com.aiia.hospital.aiia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.aiia.hospital.aiia.databinding.IncludeBottomActionsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun AppCompatActivity.setupBottomBar(bar: IncludeBottomActionsBinding) {

    bar.btnBack.setOnClickListener {
        onBackPressedDispatcher.onBackPressed()
    }

    bar.btnHome.setOnClickListener {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    bar.btnExit.setOnClickListener {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.confirm_exit_title))
            .setMessage(getString(R.string.confirm_exit_msg))
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.exit)) { _, _ ->
                finishAffinity()
            }
            .show()
    }
}