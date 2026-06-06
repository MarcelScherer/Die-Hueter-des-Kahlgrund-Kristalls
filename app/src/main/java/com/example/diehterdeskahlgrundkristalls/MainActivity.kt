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
    private lateinit var headerImage: ImageView
    private lateinit var storyTextView: TextView
    private val scrollHandler = Handler(Looper.getMainLooper())
    private lateinit var scrollRunnable: Runnable

    private var mediaPlayer: MediaPlayer? = null
    private val animationHandler = Handler(Looper.getMainLooper())
    private var currentFrame = 2
    private var isAnimating = false
    private var canStartMusic = false

    private val PREFS_NAME = "KahlgrundSpielstand"
    private val KEY_KAPITEL = "aktuelles_kapitel"
    private var gespeichertesKapitel : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        gespeichertesKapitel = prefs.getInt(KEY_KAPITEL, 0)


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

        if (gespeichertesKapitel == 2) {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }


        // 3. Views initialisieren
        scrollView = findViewById(R.id.textScrollView)
        storyTextView = findViewById(R.id.storyTextView)
        headerImage = findViewById<ImageView>(R.id.headerImage)
        val btnRepeat = findViewById<Button>(R.id.btnRepeat)
        val btnNext = findViewById<Button>(R.id.btnNext)

        // 4. Text setzen und Auto-Scroll starten
        setEnvironment()

        btnRepeat.setOnClickListener {
            scrollView.smoothScrollTo(0, 0)
            setEnvironment()
        }

        btnNext.setOnClickListener {
            if(gespeichertesKapitel == 0 || gespeichertesKapitel == 1){
                gespeichertesKapitel++
            }
            saveProgress(gespeichertesKapitel)
            mediaPlayer?.stop()

            if(gespeichertesKapitel == 0 || gespeichertesKapitel == 1){
                setEnvironment()
            }
            else if (gespeichertesKapitel == 2) {
                val intent = Intent(this, QuizActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setEnvironment(){
        val (bildIdle, bildMund1, bildMund2) = getAnimationFrames(gespeichertesKapitel)
        headerImage.setImageResource(bildIdle)
        updateUIForChapter()
        startAudioLogic()
        startAutoScroll()
    }

    private fun updateUIForChapter() {
        // Text zurücksetzen
        scrollView.scrollTo(0, 0)

        // Text basierend auf Kapitel setzen
        storyTextView.text = when (gespeichertesKapitel) {
            0 -> getString(R.string.story_intro)
            1 -> getString(R.string.kapitel_1_frage)
            else -> getString(R.string.story_intro)
        }

        // Auto-Scroll neu starten
        startAutoScroll()
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

        // Hol dir die richtigen Bilder für das aktuelle Kapitel
        val (bildIdle, bildMund1, bildMund2) = getAnimationFrames(gespeichertesKapitel)

        val animationRunnable = object : Runnable {
            override fun run() {
                // Stoppen, wenn Musik aus oder isAnimating false
                if (!isAnimating || mediaPlayer == null || !mediaPlayer!!.isPlaying) {
                    headerImage.setImageResource(bildIdle) // Nutzt bildIdle (Bild 1)
                    return
                }

                // Wechsel zwischen Bild 2 und 3 (Mund auf/zu)
                if (currentFrame == 2) {
                    headerImage.setImageResource(bildMund1) // Nutzt bildMund1
                    currentFrame = 3
                } else {
                    headerImage.setImageResource(bildMund2) // Nutzt bildMund2
                    currentFrame = 2
                }

                // Wiederholen alle 300ms
                animationHandler.postDelayed(this, 300)
            }
        }
        animationHandler.post(animationRunnable)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        isAnimating = false
        saveProgress(gespeichertesKapitel)
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

    private fun saveProgress(kapitel: Int) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt(KEY_KAPITEL, kapitel)
        editor.apply() // Speichert im Hintergrund
    }

    private fun getAnimationFrames(kapitel: Int): Triple<Int, Int, Int> {
        return when (kapitel) {
            0 -> Triple(R.drawable.start_bild_1, R.drawable.start_bild_2, R.drawable.start_bild_3)
            1 -> Triple(R.drawable.kapitel_1_bild_1, R.drawable.kapitel_1_bild_2, R.drawable.kapitel_1_bild_3)
            // Standardfall: Falls Kapitelnummer unbekannt, nimm die Startbilder
            else -> Triple(R.drawable.start_bild_1, R.drawable.start_bild_2, R.drawable.start_bild_3)
        }
    }

    private fun getStoryAudio(kapitel: Int): Int {
        return when (kapitel) {
            0 -> R.raw.mp3_1
            1 -> R.raw.mp3_kapitle_1
            // Weitere Kapitel hier ergänzen
            else -> R.raw.mp3_1
        }
    }
    private fun startAudioLogic() {
        // Falls schon Musik spielt, stoppen und freigeben
        mediaPlayer?.stop()
        mediaPlayer?.release()

        // 1. Die richtige MP3 für das Kapitel laden
        val audioRes = getStoryAudio(gespeichertesKapitel)
        mediaPlayer = MediaPlayer.create(this, audioRes)
        mediaPlayer?.isLooping = false

        // 2. Verzögerung starten
        canStartMusic = false // Zurücksetzen, bis die Verzögerung vorbei ist
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) {
                canStartMusic = true // Erlaubnis zum Abspielen erteilt
                mediaPlayer?.start()
                startTalkingAnimation()
            }
        }, 3000) // 3 Sekunden Verzögerung
    }

}