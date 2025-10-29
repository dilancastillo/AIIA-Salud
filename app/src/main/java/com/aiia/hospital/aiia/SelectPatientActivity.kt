package com.aiia.hospital.aiia

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.aiia.hospital.aiia.databinding.ActivitySelectPatientBinding

class SelectPatientActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectPatientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val patients = listOf(
            Patient("p001", "Ana Gómez", R.mipmap.ic_launcher),
            Patient("p002", "Carlos Pérez", R.mipmap.ic_launcher),
            Patient("p003", "María Rodríguez", R.mipmap.ic_launcher),
            Patient("p004", "Julián Restrepo", R.mipmap.ic_launcher)
        )

        val adapter = PatientAdapter(patients) { p ->
            Toast.makeText(this, "Seleccionado: ${p.name}", Toast.LENGTH_SHORT).show()
        }

        binding.rvPatients.adapter = adapter
        binding.rvPatients.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        if (patients.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_patients), Toast.LENGTH_LONG).show()
        }
    }
}