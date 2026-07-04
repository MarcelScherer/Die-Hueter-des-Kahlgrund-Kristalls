package com.example.diehterdeskahlgrundkristalls

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView // WICHTIG: Der richtige Import!
import android.widget.ScrollView
import android.widget.TextView
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
    private var spiellstandActuell : Spielstand = Spielstand.INTRO


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // DIESE ZEILE HINZUFÜGEN: Verhindert das Abdunkeln des Bildschirms
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // Fixiert die Orientierung auf Hochkant
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        spiellstandActuell = Spielstand.ausInt(prefs.getInt(KEY_KAPITEL, 0))


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

        Log.d("Die Hüter des Kahlgrund Kristalls", "aktueller Spielstand: $spiellstandActuell")
        if (spiellstandActuell == Spielstand.QUIZ_ST_KATARINA ||
            spiellstandActuell == Spielstand.QUIZ_SACKHAUS ||
            spiellstandActuell == Spielstand.QUIZ_MARKTPLATZ ||
            spiellstandActuell == Spielstand.QUIZ_LUCASKAPELLE ||
            spiellstandActuell == Spielstand.QUIZ_SCHWIMMBAD) {
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
            Log.d("Die Hüter des Kahlgrund Kristalls", "Klick Weiter. Stand vor Änderung: $spiellstandActuell")
            if(spiellstandActuell == Spielstand.INTRO ||
               spiellstandActuell == Spielstand.START_ST_KATARINA ||
               spiellstandActuell == Spielstand.ERGEBNIS_ST_KATARINA ||
               spiellstandActuell == Spielstand.START_SACKHAUS ||
               spiellstandActuell == Spielstand.ERGEBNIS_SACKHAUS ||
               spiellstandActuell == Spielstand.START_MARKTPLATZ ||
               spiellstandActuell == Spielstand.ERGEBNIS_MARKTPLATZ ||
               spiellstandActuell == Spielstand.START_LUCASKAPELLE ||
               spiellstandActuell == Spielstand.ERGEBNIS_LUCASKAPELLE ||
               spiellstandActuell == Spielstand.START_SCHWIMMBAD ||
               spiellstandActuell == Spielstand.ERGEBNIS_SCHWIMMBAD ){
                    spiellstandActuell = Spielstand.ausInt(spiellstandActuell.wert + 1)
            }
            else{
                spiellstandActuell = Spielstand.ausInt(0)
            }
            saveProgress(spiellstandActuell.wert)
            mediaPlayer?.stop()

            if(spiellstandActuell == Spielstand.INTRO ||
               spiellstandActuell == Spielstand.START_ST_KATARINA ||
               spiellstandActuell == Spielstand.ERGEBNIS_ST_KATARINA ||
               spiellstandActuell == Spielstand.START_SACKHAUS ||
               spiellstandActuell == Spielstand.ERGEBNIS_SACKHAUS ||
               spiellstandActuell == Spielstand.START_MARKTPLATZ ||
               spiellstandActuell == Spielstand.ERGEBNIS_MARKTPLATZ ||
               spiellstandActuell == Spielstand.START_LUCASKAPELLE ||
               spiellstandActuell == Spielstand.ERGEBNIS_LUCASKAPELLE ||
               spiellstandActuell == Spielstand.START_SCHWIMMBAD ||
               spiellstandActuell == Spielstand.ERGEBNIS_SCHWIMMBAD ){
                setEnvironment()
            }
            else if (spiellstandActuell == Spielstand.QUIZ_ST_KATARINA ||
                     spiellstandActuell == Spielstand.QUIZ_SACKHAUS ||
                     spiellstandActuell == Spielstand.QUIZ_MARKTPLATZ ||
                     spiellstandActuell == Spielstand.QUIZ_LUCASKAPELLE ||
                     spiellstandActuell == Spielstand.QUIZ_SCHWIMMBAD) {
                val intent = Intent(this, QuizActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setEnvironment(){
        val (bildIdle, bildMund1, bildMund2) = getAnimationFrames(spiellstandActuell)
        headerImage.setImageResource(bildIdle)
        updateUIForChapter()
        startAudioLogic()
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
        val (bildIdle, bildMund1, bildMund2) = getAnimationFrames(spiellstandActuell)

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
                animationHandler.postDelayed(this, 200)
            }
        }
        animationHandler.post(animationRunnable)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        isAnimating = false
        saveProgress(spiellstandActuell.wert)
    }

    override fun onResume() {
        super.onResume()
        // Spielstand neu laden, falls er im Impressum zurückgesetzt wurde
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val neuerStand = Spielstand.ausInt(prefs.getInt(KEY_KAPITEL, 0))
        
        if (neuerStand != spiellstandActuell) {
            spiellstandActuell = neuerStand
            setEnvironment()
        }

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

    private fun getAnimationFrames(kapitel: Spielstand): Triple<Int, Int, Int> {
        return when (kapitel) {
            Spielstand.INTRO                -> Triple(R.drawable.start_bild_1, R.drawable.start_bild_2, R.drawable.start_bild_3)
            Spielstand.START_ST_KATARINA    ->  Triple(R.drawable.kapitel_1_bild_1, R.drawable.kapitel_1_bild_2, R.drawable.kapitel_1_bild_3)
            Spielstand.ERGEBNIS_ST_KATARINA ->  Triple(R.drawable.kapitel_1_bild_4, R.drawable.kapitel_1_bild_5, R.drawable.kapitel_1_bild_6)
            Spielstand.START_SACKHAUS       ->  Triple(R.drawable.kapitel_2_bild_1, R.drawable.kapitel_2_bild_2, R.drawable.kapitel_2_bild_3)
            Spielstand.ERGEBNIS_SACKHAUS    ->  Triple(R.drawable.kapitel_2_bild_4, R.drawable.kapitel_2_bild_5, R.drawable.kapitel_2_bild_6)
            Spielstand.START_MARKTPLATZ     ->  Triple(R.drawable.kapitel_3_bild_1, R.drawable.kapitel_3_bild_2, R.drawable.kapitel_3_bild_3)
            Spielstand.ERGEBNIS_MARKTPLATZ  ->  Triple(R.drawable.kapitel_3_bild_4, R.drawable.kapitel_3_bild_5, R.drawable.kapitel_3_bild_6)
            Spielstand.START_LUCASKAPELLE   ->  Triple(R.drawable.kapitel_4_bild_1, R.drawable.kapitel_4_bild_2, R.drawable.kapitel_4_bild_2)
            Spielstand.ERGEBNIS_LUCASKAPELLE->  Triple(R.drawable.kapitel_4_bild_4, R.drawable.kapitel_4_bild_5, R.drawable.kapitel_4_bild_6)
            Spielstand.START_SCHWIMMBAD     ->  Triple(R.drawable.kapitel_5_bild_1, R.drawable.kapitel_5_bild_2, R.drawable.kapitel_5_bild_3)
            Spielstand.ERGEBNIS_SCHWIMMBAD  ->  Triple(R.drawable.kapitel_5_bild_4, R.drawable.kapitel_5_bild_5, R.drawable.kapitel_5_bild_4)
            else -> Triple(R.drawable.start_bild_1, R.drawable.start_bild_2, R.drawable.start_bild_3)
        }
    }

    private fun updateUIForChapter() {
        // Text zurücksetzen
        scrollView.scrollTo(0, 0)

        // Text basierend auf Kapitel setzen
        storyTextView.text = when (spiellstandActuell) {
            Spielstand.INTRO                -> getString(R.string.story_intro)
            Spielstand.START_ST_KATARINA    -> getString(R.string.kapitel_1_frage)
            Spielstand.ERGEBNIS_ST_KATARINA -> getString(R.string.kapitle_1_antwort)
            Spielstand.START_SACKHAUS       -> getString(R.string.kapitel_2_frage)
            Spielstand.ERGEBNIS_SACKHAUS    -> getString(R.string.kapitel_2_antwort)
            Spielstand.START_MARKTPLATZ     -> getString(R.string.kapitel_3_frage)
            Spielstand.ERGEBNIS_MARKTPLATZ  -> getString(R.string.kapitel_3_antwort)
            Spielstand.START_LUCASKAPELLE   -> getString(R.string.kapitel_4_frage)
            Spielstand.ERGEBNIS_LUCASKAPELLE-> getString(R.string.kapitel_4_antwort)
            Spielstand.START_SCHWIMMBAD     -> getString(R.string.kapitel_5_frage)
            Spielstand.ERGEBNIS_SCHWIMMBAD  -> getString(R.string.kapitel_5_antwort)
            else -> getString(R.string.story_intro)
        }

        // Auto-Scroll neu starten
        startAutoScroll()
    }

    private fun getStoryAudio(kapitel: Spielstand): Int {
        return when (kapitel) {
            Spielstand.INTRO                -> R.raw.mp3_1
            Spielstand.START_ST_KATARINA    -> R.raw.mp3_kapitel_1_frage
            Spielstand.ERGEBNIS_ST_KATARINA -> R.raw.mp3_kapitel_1_ergebnis
            Spielstand.START_SACKHAUS       -> R.raw.mp3_kapitel_2_frage
            Spielstand.ERGEBNIS_SACKHAUS    -> R.raw.mp3_kapitel_2_ergenis
            Spielstand.START_MARKTPLATZ     -> R.raw.mp3_kapitel_3_frage
            Spielstand.ERGEBNIS_MARKTPLATZ  -> R.raw.mp3_kapitel_3_ergebnis
            Spielstand.START_LUCASKAPELLE   -> R.raw.mp3_kapitel_4_frage
            Spielstand.ERGEBNIS_LUCASKAPELLE-> R.raw.mp3_kapitel_4_ergebnis
            Spielstand.START_SCHWIMMBAD     -> R.raw.mp3_kapitel_5_frage
            Spielstand.ERGEBNIS_SCHWIMMBAD  -> R.raw.mp3_kapitel_5_ergebnis
            else -> R.raw.mp3_1
        }
    }
    private fun startAudioLogic() {
        // Falls schon Musik spielt, stoppen und freigeben
        mediaPlayer?.stop()
        mediaPlayer?.release()

        // 1. Die richtige MP3 für das Kapitel laden
        val audioRes = getStoryAudio(spiellstandActuell)
        mediaPlayer = MediaPlayer.create(this, audioRes)
        mediaPlayer?.isLooping = false

        // 2. Verzögerung für Musik (3 Sekunden)
        canStartMusic = false
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) {
                canStartMusic = true
                mediaPlayer?.start()

                // 3. NEU: Verzögerung für Animation (Zusätzlich 1 Sekunde warten)
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!isFinishing && mediaPlayer != null && mediaPlayer!!.isPlaying) {
                        startTalkingAnimation()
                    }
                }, 1000) // 1000ms = 1 Sekunde nach Musikstart
            }
        }, 3000) // Musikstart nach 3 Sekunden
    }
}