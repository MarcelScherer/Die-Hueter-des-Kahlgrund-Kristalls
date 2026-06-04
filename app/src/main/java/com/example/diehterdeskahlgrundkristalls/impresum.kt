package com.example.diehterdeskahlgrundkristalls

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.diehterdeskahlgrundkristalls.databinding.ActivityImpresumBinding

class impresum : AppCompatActivity() {

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
    }
}