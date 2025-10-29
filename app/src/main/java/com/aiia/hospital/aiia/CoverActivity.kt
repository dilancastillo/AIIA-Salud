package com.aiia.hospital.aiia

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aiia.hospital.aiia.databinding.ActivityCoverBinding
import com.aiia.hospital.aiia.ui.MainActivity

class CoverActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEmpezar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}