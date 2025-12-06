import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.GameState
import core.GameStatus
import core.PieceColor
import ui.CheckersBoard
import ui.GameOverDialog

@Composable
@Preview
fun App() {
    val scope = rememberCoroutineScope()
    val gameState = remember { GameState(scope) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titre
            Text(
                text = "Dames Internationales",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Boutons Undo/Redo
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { gameState.undo() },
                    enabled = gameState.canUndo && !gameState.isAITurn,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("↶ Annuler")
                }

                Button(
                    onClick = { gameState.redo() },
                    enabled = gameState.canRedo && !gameState.isAITurn
                ) {
                    Text("↷ Refaire")
                }
            }

            // Plateau de jeu
            key(gameState.refreshTrigger) {
                CheckersBoard(
                    board = gameState.board,
                    onPieceSelected = { pos -> gameState.handlePieceSelection(pos) },
                    selectedPiece = gameState.selectedPiece,
                    possibleMoves = gameState.possibleMoves
                )
            }

            // Indicateur de tour
            Text(
                text = when {
                    gameState.isAITurn -> "Tour de l'IA..."
                    gameState.board.currentPlayer == PieceColor.WHITE -> "Votre tour (Blancs)"
                    else -> "Tour des Noirs"
                },
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 18.sp
            )
        }

        // Dialog de fin de partie
        if (gameState.gameStatus != GameStatus.PLAYING) {
            GameOverDialog(
                gameStatus = gameState.gameStatus,
                onPlayAgain = { gameState.resetGame() },
                onQuit = { /* Vous pouvez gérer la fermeture ici */ }
            )
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Dames Internationales") {
        App()
    }
}