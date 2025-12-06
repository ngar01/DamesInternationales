import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.*
import ui.*


@Composable
@Preview
fun App() {
    val scope = rememberCoroutineScope()
    val gameState = remember { GameState(scope) }
    val theme = Themes.getTheme(gameState.currentTheme)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titre et Sélecteur de thème
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dames Internationales",
                    fontSize = 28.sp,
                    color = theme.textColor
                )

                ThemeDropdown(
                    currentTheme = gameState.currentTheme,
                    onThemeSelected = { gameState.setTheme(it) },
                    theme = theme
                )
            }

            // Boutons Undo/Redo
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { gameState.undo() },
                    enabled = gameState.canUndo && !gameState.isAITurn,
                    colors = ButtonDefaults.buttonColors(backgroundColor = theme.buttonColor),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("↶ Annuler", color = Color.White)
                }

                Button(
                    onClick = { gameState.redo() },
                    enabled = gameState.canRedo && !gameState.isAITurn,
                    colors = ButtonDefaults.buttonColors(backgroundColor = theme.buttonColor)
                ) {
                    Text("↷ Refaire", color = Color.White)
                }
            }

            // Plateau de jeu
            key(gameState.refreshTrigger) {
                CheckersBoard(
                    board = gameState.board,
                    onPieceSelected = { pos -> gameState.handlePieceSelection(pos) },
                    selectedPiece = gameState.selectedPiece,
                    possibleMoves = gameState.possibleMoves,
                    theme = theme
                )
            }

            // Indicateur de tour
            Text(
                text = when {
                    gameState.isAITurn -> "⏳ Tour de l'IA..."
                    gameState.board.currentPlayer == PieceColor.WHITE -> "▶ Votre tour (Blancs)"
                    else -> "▶ Tour des Noirs"
                },
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 18.sp,
                color = theme.textColor
            )
        }

        // Dialog de fin de partie
        if (gameState.gameStatus != GameStatus.PLAYING) {
            GameOverDialog(
                gameStatus = gameState.gameStatus,
                onPlayAgain = { gameState.resetGame() },
                onQuit = { /* Gérer la fermeture */ }
            )
        }
    }
}

@Composable
fun ThemeSelector(
    themeName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    themePreview: CheckersTheme,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) themePreview.buttonColor else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Aperçu du thème (mini damier)
        Row {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(themePreview.lightSquare)
            )
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(themePreview.darkSquare)
            )
        }

        Text(
            text = themeName,
            fontSize = 12.sp,
            color = themePreview.textColor,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Dames Internationales") {
        MaterialTheme {
            App()
        }
    }
}