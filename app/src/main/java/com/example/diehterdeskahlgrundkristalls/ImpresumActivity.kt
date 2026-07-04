package com.example.diehterdeskahlgrundkristalls

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diehterdeskahlgrundkristalls.databinding.ActivityImpresumBinding

class ImpresumActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImpresumBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding initialisieren
        binding = ActivityImpresumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Die Toolbar als Action-Bar setzen
        setSupportActionBar(binding.toolbar)

        // Einen "Zurück"-Button in der Toolbar aktivieren
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Was passiert, wenn man auf den Zurück-Pfeil klickt
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Spielstand zurücksetzen
        binding.btnReset.setOnClickListener {
            val prefs = getSharedPreferences("KahlgrundSpielstand", MODE_PRIVATE)
            prefs.edit().putInt("aktuelles_kapitel", 0).apply()
            
            // Optional: Dem Nutzer Feedback geben
            Toast.makeText(this, "Spielstand wurde zurückgesetzt", Toast.LENGTH_SHORT).show()
        }
    }
}