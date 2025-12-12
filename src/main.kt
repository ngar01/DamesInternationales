// src/main/kotlin/Main.kt
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import core.*
import ui.*

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Dames Internationales") {
        MaterialTheme {
            App()
        }
    }
}

@Composable
fun ScoreCard(
    label: String,
    score: Int,
    color: Color,
    isActive: Boolean,
    theme: CheckersTheme,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                if (isActive) theme.buttonColor.copy(alpha = 0.2f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = theme.textColor
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(color, shape = RoundedCornerShape(12.dp))
            )

            Text(
                text = score.toString(),
                fontSize = 24.sp,
                color = theme.textColor
            )
        }
    }
}

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
            // Titre et Sélecteurs
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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GameModeDropdown(
                        currentMode = gameState.gameMode,
                        onModeSelected = { gameState.changeGameMode(it) },
                        theme = theme,
                        enabled = !gameState.isAITurn
                    )

                    ThemeDropdown(
                        currentTheme = gameState.currentTheme,
                        onThemeSelected = { gameState.setTheme(it) },
                        theme = theme
                    )
                }
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

            // Panneau de scores
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreCard(
                    label = if (gameState.gameMode == GameMode.PLAYER_VS_AI) "Vous" else "Joueur 1",
                    score = gameState.whiteScore,
                    color = theme.whitePiece,
                    isActive = gameState.board.currentPlayer == PieceColor.WHITE && !gameState.isAITurn,
                    theme = theme,
                    modifier = Modifier.padding(end = 16.dp)
                )

                Text(
                    text = "vs",
                    fontSize = 20.sp,
                    color = theme.textColor,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                ScoreCard(
                    label = if (gameState.gameMode == GameMode.PLAYER_VS_AI) "IA" else "Joueur 2",
                    score = gameState.blackScore,
                    color = theme.blackPiece,
                    isActive = gameState.board.currentPlayer == PieceColor.BLACK || gameState.isAITurn,
                    theme = theme,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Plateau de jeu
            key(gameState.refreshTrigger) {
                CheckersBoard(
                    board = gameState.board,
                    onPieceSelected = { pos -> gameState.handlePieceSelection(pos) },
                    onHover = { pos -> gameState.updateHoveredPiece(pos) },
                    canPieceMove = { pos -> gameState.canPieceMove(pos) },
                    selectedPiece = gameState.selectedPiece,
                    possibleMoves = gameState.possibleMoves,
                    hoveredPiece = gameState.hoveredPiece,
                    theme = theme
                )
            }

            // Indicateur de tour
            Text(
                text = when {
                    gameState.isAITurn -> "⏳ Tour de l'IA..."
                    gameState.gameMode == GameMode.PLAYER_VS_AI -> {
                        if (gameState.board.currentPlayer == PieceColor.WHITE)
                            "▶ Votre tour (Blancs)"
                        else
                            "▶ Tour de l'IA (Noirs)"
                    }
                    else -> {
                        if (gameState.board.currentPlayer == PieceColor.WHITE)
                            "▶ Tour du Joueur 1 (Blancs)"
                        else
                            "▶ Tour du Joueur 2 (Noirs)"
                    }
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