package com.example.diehterdeskahlgrundkristalls

enum class Spielstand(val wert: Int) {
    INTRO(0),
    START_ST_KATARINA(1),
    QUIZ_ST_KATARINA(2),
    ERGEBNIS_ST_KATARINA(3),
    START_SACKHAUS(4),
    QUIZ_SACKHAUS(5),
    ERGEBNIS_SACKHAUS(6),
    START_MARKTPLATZ(7),
    QUIZ_MARKTPLATZ(8),
    ERGEBNIS_MARKTPLATZ(9);

    companion object {
        // Hilfsfunktion, um aus einer Zahl wieder ein Enum zu machen
        fun ausInt(wert: Int): Spielstand {
            return values().find { it.wert == wert } ?: INTRO
        }
    }
}