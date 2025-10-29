package com.aiia.hospital.aiia

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aiia.hospital.aiia.databinding.ActivitySelectPatientBinding

class SelectPatientActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectPatientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomBar(binding.bottomActions)

        val patients = listOf(
            Patient("p001", "Ana Gómez", R.drawable.ana_gomez),
            Patient("p002", "Carlos Pérez", R.drawable.carlos_perez),
            Patient("p003", "María Rodríguez", R.drawable.maria_rodriguez),
            Patient("p004", "Julián Restrepo", R.drawable.julian_restrepo)
        )

        if (binding.rvPatients.layoutManager == null) {
            binding.rvPatients.layoutManager = LinearLayoutManager(this)
        }

        val adapter = PatientAdapter(patients) { p ->
            val i = Intent(this, FaceScanActivity::class.java)
                .putExtra("patient_id", p.id)
                .putExtra("patient_name", p.name)
            startActivity(i)
        }

        binding.rvPatients.adapter = adapter

        binding.rvPatients.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
    }
}