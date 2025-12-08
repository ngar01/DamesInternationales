// src/main/kotlin/ui/GameOverDialog.kt
package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import core.GameStatus

@Composable
fun GameOverDialog(
    gameStatus: GameStatus,
    onPlayAgain: () -> Unit,
    onQuit: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Box(
            modifier = Modifier
                .size(400.dp, 250.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Titre
                Text(
                    text = "🎮 Fin de partie !",
                    fontSize = 28.sp,
                    color = Color(0xFF2C3E50)
                )

                // Message du vainqueur
                Text(
                    text = when (gameStatus) {
                        GameStatus.WHITE_WINS -> "🎉 Vous avez gagné ! 🎉"
                        GameStatus.BLACK_WINS -> "L'IA a gagné !"
                        GameStatus.DRAW -> "Match nul !"
                        else -> ""
                    },
                    fontSize = 22.sp,
                    color = when (gameStatus) {
                        GameStatus.WHITE_WINS -> Color(0xFF27AE60)
                        GameStatus.BLACK_WINS -> Color(0xFFE74C3C)
                        else -> Color(0xFF95A5A6)
                    }
                )

                // Boutons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onPlayAgain,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF3498DB)
                        ),
                        modifier = Modifier.height(48.dp).width(140.dp)
                    ) {
                        Text("🔄 Rejouer", color = Color.White, fontSize = 16.sp)
                    }

                    Button(
                        onClick = onQuit,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF95A5A6)
                        ),
                        modifier = Modifier.height(48.dp).width(140.dp)
                    ) {
                        Text("❌ Quitter", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}