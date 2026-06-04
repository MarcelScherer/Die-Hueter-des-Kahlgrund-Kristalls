package com.example.diehterdeskahlgrundkristalls

import android.content.Intent // WICHTIG
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton // WICHTIG
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var scrollView: ScrollView
    private lateinit var storyTextView: TextView
    private val scrollHandler = Handler(Looper.getMainLooper())
    private lateinit var scrollRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 1. Impressum Button Logik
        val btnImpressum = findViewById<ImageButton>(R.id.btnImpressum)
        btnImpressum.setOnClickListener {
            // Wechselt zur ImpressumActivity
            val intent = Intent(this, impresum::class.java)
            startActivity(intent)
        } // <--- Diese Klammer hat in deinem Code gefehlt!

        // 2. Window Insets für Edge-to-Edge Design
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. Views initialisieren
        scrollView = findViewById(R.id.textScrollView)
        storyTextView = findViewById(R.id.storyTextView)
        val btnRepeat = findViewById<Button>(R.id.btnRepeat)
        val btnNext = findViewById<Button>(R.id.btnNext)

        // 4. Text setzen und Auto-Scroll starten
        // Stelle sicher, dass "story_intro" in deiner strings.xml existiert!
        storyTextView.text = getString(R.string.story_intro)
        startAutoScroll()

        // 5. Button Klick-Events
        btnRepeat.setOnClickListener {
            scrollView.smoothScrollTo(0, 0)
            startAutoScroll()
        }

        btnNext.setOnClickListener {
            // Hier kommt später die Logik für die nächste Seite hin
        }
    }

    private fun startAutoScroll() {
        scrollHandler.removeCallbacksAndMessages(null)
        scrollRunnable = object : Runnable {
            override fun run() {
                scrollView.smoothScrollBy(0, 1)
                scrollHandler.postDelayed(this, 30)
            }
        }
        scrollHandler.postDelayed(scrollRunnable, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        scrollHandler.removeCallbacksAndMessages(null)
    }
}