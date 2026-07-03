package com.example.diehterdeskahlgrundkristalls

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class QuizActivity : AppCompatActivity() {

    private val PREFS_NAME = "KahlgrundSpielstand"
    private val KEY_KAPITEL = "aktuelles_kapitel"
    private var spiellstandActuell : Spielstand = Spielstand.INTRO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)

        // Insets für die Statusbar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        spiellstandActuell = Spielstand.ausInt(prefs.getInt(KEY_KAPITEL, 0))

        val questionText = findViewById<TextView>(R.id.questionTextView)
        val btn1 = findViewById<Button>(R.id.btnAnswer1)
        val btn2 = findViewById<Button>(R.id.btnAnswer2)
        val btn3 = findViewById<Button>(R.id.btnAnswer3)
        val btn4 = findViewById<Button>(R.id.btnAnswer4)
        val quizImage = findViewById<ImageView>(R.id.quizImage)

        if(spiellstandActuell == Spielstand.QUIZ_ST_KATARINA){
            quizImage.setImageResource(R.drawable.raetsel_1)
            questionText.text = "Was steht auf einer der Türen?"
            btn1.text = "Gott schütze dich"
            btn2.text = "Sankt Katharina"
            btn3.text = "Erwecke deine Kirche und fang bei mir an"
            btn4.text = "Tritt ein in mein Haus"
        }
        else if(spiellstandActuell == Spielstand.QUIZ_SACKHAUS){
            quizImage.setImageResource(R.drawable.raetsel_2)
            questionText.text = "Wann wurde der erste Bauteil des Sackhauses erbaut?"
            btn1.text = "1473"
            btn2.text = "1572"
            btn3.text = "1682"
            btn4.text = "1996"
        }
        else if(spiellstandActuell == Spielstand.QUIZ_MARKTPLATZ){
            quizImage.setImageResource(R.drawable.raetsel_3)
            questionText.text = "Wie viele Morgensterne sind auf dem richtigen Wappen von Schöllkrippen zu sehen?"
            btn1.text = "1"
            btn2.text = "2"
            btn3.text = "3"
            btn4.text = "4"
        }
        else if(spiellstandActuell == Spielstand.QUIZ_LUCASKAPELLE){
            quizImage.setImageResource(R.drawable.raetsel_4)
            questionText.text = "Was ist an der Turmspitze der Lucas Kapelle"
            btn1.text = "Reichsapfel"
            btn2.text = "Sonne"
            btn3.text = "Kreuz"
            btn4.text = "Wappen"
        }

        // Klick-Logik
        btn1.setOnClickListener {
            if(spiellstandActuell == Spielstand.QUIZ_ST_KATARINA) {
                handleAnswer(false)
            }
            else if(spiellstandActuell == Spielstand.QUIZ_SACKHAUS) {
                handleAnswer(true)
                saveProgress(spiellstandActuell.wert + 1)
                val intent = android.content.Intent(this, MainActivity::class.java)
                // Flags sorgen dafür, dass die MainActivity sauber neu startet
                intent.flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            else if(spiellstandActuell == Spielstand.QUIZ_MARKTPLATZ) {
                handleAnswer(false)
            }
            else if(spiellstandActuell == Spielstand.QUIZ_LUCASKAPELLE) {
                handleAnswer(false)
            }
        }
        btn2.setOnClickListener {
            if(spiellstandActuell == Spielstand.QUIZ_ST_KATARINA) {
                handleAnswer(true)
                saveProgress(spiellstandActuell.wert + 1)
                val intent = android.content.Intent(this, MainActivity::class.java)
                // Flags sorgen dafür, dass die MainActivity sauber neu startet
                intent.flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            else if(spiellstandActuell == Spielstand.QUIZ_SACKHAUS) {
                handleAnswer(false)
            }
            else if(spiellstandActuell == Spielstand.QUIZ_MARKTPLATZ) {
                handleAnswer(false)
            }
            else if(spiellstandActuell == Spielstand.QUIZ_LUCASKAPELLE) {
                handleAnswer(false)
            }
        }
        btn3.setOnClickListener {
            if(spiellstandActuell == Spielstand.QUIZ_ST_KATARINA) {
                handleAnswer(false)
            }
            else if(spiellstandActuell == Spielstand.QUIZ_SACKHAUS) {
                handleAnswer(false)
            }
            if(spiellstandActuell == Spielstand.QUIZ_MARKTPLATZ) {
                handleAnswer(true)
                saveProgress(spiellstandActuell.wert + 1)
                val intent = android.content.Intent(this, MainActivity::class.java)
                // Flags sorgen dafür, dass die MainActivity sauber neu startet
                intent.flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            if(spiellstandActuell == Spielstand.QUIZ_LUCASKAPELLE) {
                handleAnswer(true)
                saveProgress(spiellstandActuell.wert + 1)
                val intent = android.content.Intent(this, MainActivity::class.java)
                // Flags sorgen dafür, dass die MainActivity sauber neu startet
                intent.flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
        btn4.setOnClickListener {
            if(spiellstandActuell == Spielstand.QUIZ_ST_KATARINA) {
                handleAnswer(false)
            }
            else if(spiellstandActuell == Spielstand.QUIZ_SACKHAUS) {
                handleAnswer(false)
            }
            else if(spiellstandActuell == Spielstand.QUIZ_MARKTPLATZ) {
                handleAnswer(false)
            }
            else if(spiellstandActuell == Spielstand.QUIZ_LUCASKAPELLE) {
                handleAnswer(false)
            }
        }
    }

    private fun handleAnswer(isCorrect: Boolean) {
        if (isCorrect) {
            Toast.makeText(this, "Richtig! Weiter geht's!", Toast.LENGTH_SHORT).show()
            // Hier Spielstand speichern und zur nächsten Story-Activity wechseln
        } else {
            Toast.makeText(this, "Leider falsch, versuch es nochmal!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProgress(kapitelWert: Int) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putInt(KEY_KAPITEL, kapitelWert).apply()
    }
}
