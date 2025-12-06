import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
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
import core.Board
import core.PieceColor
import core.Position
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ui.CheckersBoard

@Composable
@Preview
fun App() {
    val board = remember { Board() }
    var selectedPiece by remember { mutableStateOf<Position?>(null) }
    var possibleMoves by remember { mutableStateOf<List<Position>>(emptyList()) }
    var isAITurn by remember { mutableStateOf(false) }
    var mustContinueCapture by remember { mutableStateOf(false) }
    var lastMovedPosition by remember { mutableStateOf<Position?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }
    var canUndo by remember { mutableStateOf(false) }
    var canRedo by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Fonction pour gérer le tour de l'IA avec prises multiples
    fun playAITurn() {
        scope.launch {
            isAITurn = true
            delay(500)

            // Trouver le premier coup de l'IA
            val firstMove = board.getRandomMove(PieceColor.BLACK)

            if (firstMove != null) {
                val (from, to) = firstMove
                val piece = board.getPiece(from)

                if (piece != null) {
                    // Vérifier si le premier coup est une capture
                    val isCapture = board.canCapture(from, piece).any { it.first == to }

                    // Effectuer le premier mouvement
                    board.movePiece(from, to)
                    refreshTrigger++

                    // Si c'était une capture, continuer les captures multiples
                    if (isCapture) {
                        delay(500)
                        var currentPosition = to
                        var continueCapturing = true

                        while (continueCapturing && board.canCaptureFrom(currentPosition)) {
                            val currentPiece = board.getPiece(currentPosition)
                            if (currentPiece != null) {
                                val captures = board.canCapture(currentPosition, currentPiece)
                                if (captures.isNotEmpty()) {
                                    val nextCapture = captures.random().first
                                    board.movePiece(currentPosition, nextCapture)
                                    currentPosition = nextCapture
                                    refreshTrigger++
                                    delay(500)
                                } else {
                                    continueCapturing = false
                                }
                            } else {
                                continueCapturing = false
                            }
                        }
                    }
                    // Si c'était un mouvement simple, on s'arrête là (pas de capture supplémentaire)
                }
            }

            // Sauvegarder l'état après le coup de l'IA
            board.saveState()
            board.switchPlayer()
            isAITurn = false
            canUndo = board.canUndo()
            canRedo = board.canRedo()
            refreshTrigger++
        }
    }

    fun handlePieceSelection(position: Position) {
        if (isAITurn) return

        val piece = board.getPiece(position)

        // Mode capture multiple
        if (mustContinueCapture && lastMovedPosition != null) {
            if (selectedPiece != null && possibleMoves.contains(position)) {
                val fromPosition = selectedPiece!!
                val movingPiece = board.getPiece(fromPosition)

                if (movingPiece != null) {
                    val moveSuccessful = board.movePiece(fromPosition, position)

                    if (moveSuccessful) {
                        if (board.canCaptureFrom(position)) {
                            lastMovedPosition = position
                            selectedPiece = position
                            val movedPiece = board.getPiece(position)!!
                            possibleMoves = board.canCapture(position, movedPiece).map { it.first }
                            refreshTrigger++
                        } else {
                            // Fin des captures multiples
                            selectedPiece = null
                            possibleMoves = emptyList()
                            mustContinueCapture = false
                            lastMovedPosition = null

                            // Sauvegarder l'état après le tour complet du joueur
                            board.saveState()
                            board.switchPlayer()
                            canUndo = board.canUndo()
                            canRedo = board.canRedo()
                            refreshTrigger++
                            playAITurn()
                        }
                    }
                }
                return
            }

            if (position == lastMovedPosition) {
                if (selectedPiece == position) {
                    selectedPiece = null
                    possibleMoves = emptyList()
                } else {
                    selectedPiece = position
                    val p = board.getPiece(position)!!
                    possibleMoves = board.canCapture(position, p).map { it.first }
                }
                return
            }
            return
        }

        if (selectedPiece == position) {
            selectedPiece = null
            possibleMoves = emptyList()
            return
        }

        if (piece != null && piece.color == board.currentPlayer) {
            selectedPiece = position
            val captures = board.canCapture(position, piece)
            possibleMoves = if (captures.isNotEmpty()) {
                captures.map { it.first }
            } else if (!board.hasCaptures(piece.color)) {
                board.getPossibleMoves(position, piece)
            } else {
                emptyList()
            }
            return
        }

        if (selectedPiece != null && possibleMoves.contains(position)) {
            val fromPosition = selectedPiece!!
            val movingPiece = board.getPiece(fromPosition)

            if (movingPiece != null) {
                val wasCapture = board.canCapture(fromPosition, movingPiece).any { it.first == position }
                val moveSuccessful = board.movePiece(fromPosition, position)

                if (moveSuccessful) {
                    if (wasCapture && board.canCaptureFrom(position)) {
                        mustContinueCapture = true
                        lastMovedPosition = position
                        selectedPiece = position
                        val movedPiece = board.getPiece(position)!!
                        possibleMoves = board.canCapture(position, movedPiece).map { it.first }
                        refreshTrigger++
                    } else {
                        selectedPiece = null
                        possibleMoves = emptyList()
                        mustContinueCapture = false
                        lastMovedPosition = null

                        // Sauvegarder l'état après le tour complet du joueur
                        board.saveState()
                        board.switchPlayer()
                        canUndo = board.canUndo()
                        canRedo = board.canRedo()
                        refreshTrigger++
                        playAITurn()
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Boutons Undo/Redo
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (!isAITurn && board.undo()) {
                        selectedPiece = null
                        possibleMoves = emptyList()
                        mustContinueCapture = false
                        lastMovedPosition = null
                        canUndo = board.canUndo()
                        canRedo = board.canRedo()
                        refreshTrigger++
                    }
                },
                enabled = canUndo && !isAITurn,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("↶ Annuler")
            }

            Button(
                onClick = {
                    if (!isAITurn && board.redo()) {
                        selectedPiece = null
                        possibleMoves = emptyList()
                        mustContinueCapture = false
                        lastMovedPosition = null
                        canUndo = board.canUndo()
                        canRedo = board.canRedo()
                        refreshTrigger++
                    }
                },
                enabled = canRedo && !isAITurn
            ) {
                Text("↷ Refaire")
            }
        }

        // Plateau de jeu
        key(refreshTrigger) {
            CheckersBoard(
                board = board,
                onPieceSelected = { pos -> handlePieceSelection(pos) },
                selectedPiece = selectedPiece,
                possibleMoves = possibleMoves
            )
        }

        // Indicateur de tour
        Text(
            text = if (isAITurn) "Tour de l'IA..." else if (board.currentPlayer == PieceColor.WHITE) "Votre tour (Blancs)" else "Tour des Noirs",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Dames Internationales") {
        App()
    }
}