// src/main/kotlin/ui/Theme.kt
package ui

import androidx.compose.ui.graphics.Color

enum class ThemeType {
    CLASSIC,
    MODERN,
    DARK
}

data class CheckersTheme(
    val name: String,
    val lightSquare: Color,
    val darkSquare: Color,
    val whitePiece: Color,
    val blackPiece: Color,
    val whitePieceBorder: Color,
    val blackPieceBorder: Color,
    val queenMarker: Color,
    val selectedHighlight: Color,
    val possibleMoveHighlight: Color,
    val backgroundColor: Color,
    val textColor: Color,
    val buttonColor: Color
)

object Themes {
    val CLASSIC = CheckersTheme(
        name = "Classique",
        lightSquare = Color(0xFFDEB887),      // Beige clair
        darkSquare = Color(0xFF8B4513),       // Marron
        whitePiece = Color.White,
        blackPiece = Color.Black,
        whitePieceBorder = Color.Black,
        blackPieceBorder = Color.White,
        queenMarker = Color.Red,
        selectedHighlight = Color.Blue.copy(alpha = 0.3f),
        possibleMoveHighlight = Color.Green.copy(alpha = 0.5f),
        backgroundColor = Color(0xFFF5F5F5),
        textColor = Color(0xFF2C3E50),
        buttonColor = Color(0xFF3498DB)
    )

    val MODERN = CheckersTheme(
        name = "Moderne",
        lightSquare = Color(0xFFECF0F1),      // Gris très clair
        darkSquare = Color(0xFF34495E),       // Bleu-gris foncé
        whitePiece = Color(0xFFE74C3C),       // Rouge vif
        blackPiece = Color(0xFF2ECC71),       // Vert vif
        whitePieceBorder = Color(0xFFC0392B),
        blackPieceBorder = Color(0xFF27AE60),
        queenMarker = Color(0xFFF39C12),      // Orange
        selectedHighlight = Color(0xFF9B59B6).copy(alpha = 0.4f),
        possibleMoveHighlight = Color(0xFF1ABC9C).copy(alpha = 0.6f),
        backgroundColor = Color(0xFFBDC3C7),
        textColor = Color(0xFF2C3E50),
        buttonColor = Color(0xFF9B59B6)
    )

    val DARK = CheckersTheme(
        name = "Sombre",
        lightSquare = Color(0xFF3A3A3A),      // Gris foncé
        darkSquare = Color(0xFF1A1A1A),       // Noir grisâtre
        whitePiece = Color(0xFFE0E0E0),       // Gris clair
        blackPiece = Color(0xFF424242),       // Gris moyen
        whitePieceBorder = Color(0xFFBDBDBD),
        blackPieceBorder = Color(0xFF757575),
        queenMarker = Color(0xFFFFD700),      // Or
        selectedHighlight = Color(0xFF64B5F6).copy(alpha = 0.4f),
        possibleMoveHighlight = Color(0xFF81C784).copy(alpha = 0.6f),
        backgroundColor = Color(0xFF212121),
        textColor = Color(0xFFE0E0E0),
        buttonColor = Color(0xFF1976D2)
    )

    fun getTheme(type: ThemeType): CheckersTheme {
        return when (type) {
            ThemeType.CLASSIC -> CLASSIC
            ThemeType.MODERN -> MODERN
            ThemeType.DARK -> DARK
        }
    }

    fun getAllThemes(): List<Pair<ThemeType, CheckersTheme>> {
        return listOf(
            ThemeType.CLASSIC to CLASSIC,
            ThemeType.MODERN to MODERN,
            ThemeType.DARK to DARK
        )
    }
}