package com.example.vozi001.domain.model


import androidx.compose.ui.text.AnnotatedString

data class PracticeResult(
    val richTextResult: AnnotatedString,
    val accuracy: Float,
    val stars: Int,
    val playSuccessSound: Boolean,
    val firstFailedChar: Char?
)
