package com.example.diehterdeskahlgrundkristalls

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView // WICHTIG: Der richtige Import!
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

    private var mediaPlayer: MediaPlayer? = null
    private val animationHandler = Handler(Looper.getMainLooper())
    private var currentFrame = 2
    private var isAnimating = false
    private var canStartMusic = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // MP3 initialisieren
        mediaPlayer = MediaPlayer.create(this, R.raw.mp3_1)
        mediaPlayer?.isLooping = false

        // Musik und Animation verzögert nach 3 Sekunden starten
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) {
                canStartMusic = true // Jetzt ist die Erlaubnis da
                mediaPlayer?.start()
                startTalkingAnimation()
            }
        }, 3000)

        // 1. Impressum Button
        val btnImpressum = findViewById<ImageButton>(R.id.btnImpressum)
        btnImpressum.setOnClickListener {
            val intent = Intent(this, ImpresumActivity::class.java)
            startActivity(intent)
        }

        // 2. Window Insets
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
        storyTextView.text = getString(R.string.story_intro)
        startAutoScroll()

        btnRepeat.setOnClickListener {
            scrollView.smoothScrollTo(0, 0)
            startAutoScroll()
        }

        btnRepeat.setOnClickListener {
            // 1. Scroll-Position sofort auf Anfang setzen
            scrollView.scrollTo(0, 0)

            // 2. Auto-Scroll neu starten (mit der üblichen Verzögerung)
            startAutoScroll()

            // 3. Musik von vorne starten
            mediaPlayer?.let { player ->
                player.pause()           // Erst stoppen
                player.seekTo(0)         // An den Anfang springen

                // Wir nutzen einen kleinen Handler, damit der Neustart der Musik
                // mit der Animation synchronisiert wird
                canStartMusic = true
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!isFinishing) {
                        canStartMusic = true // Jetzt ist die Erlaubnis da
                        mediaPlayer?.start()
                        startTalkingAnimation()
                    }
                }, 3000)
            }
        }
    }


    private fun startAutoScroll() {
        scrollHandler.removeCallbacksAndMessages(null)
        scrollRunnable = object : Runnable {
            override fun run() {
                scrollView.smoothScrollBy(0, 1)
                scrollHandler.postDelayed(this, 60)
            }
        }
        // Das Scrollen beginnt nach 15 Sekunden
        scrollHandler.postDelayed(scrollRunnable, 15000)
    }

    private fun startTalkingAnimation() {
        isAnimating = true
        val headerImage = findViewById<ImageView>(R.id.headerImage)

        val animationRunnable = object : Runnable {
            override fun run() {
                // Stoppen, wenn Musik aus oder isAnimating false
                if (!isAnimating || mediaPlayer == null || !mediaPlayer!!.isPlaying) {
                    headerImage.setImageResource(R.drawable.start_bild_1)
                    return
                }

                // Wechsel zwischen Bild 2 und 3 (Mund auf/zu)
                if (currentFrame == 2) {
                    headerImage.setImageResource(R.drawable.start_bild_2)
                    currentFrame = 3
                } else {
                    headerImage.setImageResource(R.drawable.start_bild_3)
                    currentFrame = 2
                }

                // Wiederholen alle 200ms
                animationHandler.postDelayed(this, 300)
            }
        }
        animationHandler.post(animationRunnable)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        isAnimating = false
    }

    override fun onResume() {
        super.onResume()
        // Wenn die Musik beim Zurückkehren wieder starten soll:
        if (canStartMusic && mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
            startTalkingAnimation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scrollHandler.removeCallbacksAndMessages(null)
        animationHandler.removeCallbacksAndMessages(null)
        mediaPlayer?.release()
        mediaPlayer = null
        isAnimating = false
    }
}