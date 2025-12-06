// src/main/kotlin/core/GameState.kt
package core

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class GameStatus {
    PLAYING,
    WHITE_WINS,
    BLACK_WINS,
    DRAW
}

class GameState(private val scope: CoroutineScope) {
    var board by mutableStateOf(Board())
        private set

    var selectedPiece by mutableStateOf<Position?>(null)
        private set

    var possibleMoves by mutableStateOf<List<Position>>(emptyList())
        private set

    var isAITurn by mutableStateOf(false)
        private set

    var mustContinueCapture by mutableStateOf(false)
        private set

    var lastMovedPosition by mutableStateOf<Position?>(null)
        private set

    var refreshTrigger by mutableStateOf(0)
        private set

    var canUndo by mutableStateOf(false)
        private set

    var canRedo by mutableStateOf(false)
        private set

    var gameStatus by mutableStateOf(GameStatus.PLAYING)
        private set

    // Vérifier la fin de partie
    private fun checkGameOver() {
        val whiteHasPieces = board.hasAnyPieces(PieceColor.WHITE)
        val blackHasPieces = board.hasAnyPieces(PieceColor.BLACK)
        val currentPlayerHasMoves = board.hasAnyMoves(board.currentPlayer)

        gameStatus = when {
            !blackHasPieces || (board.currentPlayer == PieceColor.BLACK && !currentPlayerHasMoves) -> GameStatus.WHITE_WINS
            !whiteHasPieces || (board.currentPlayer == PieceColor.WHITE && !currentPlayerHasMoves) -> GameStatus.BLACK_WINS
            else -> GameStatus.PLAYING
        }
    }

    // Réinitialiser le jeu
    fun resetGame() {
        board = Board()
        selectedPiece = null
        possibleMoves = emptyList()
        isAITurn = false
        mustContinueCapture = false
        lastMovedPosition = null
        refreshTrigger = 0
        canUndo = false
        canRedo = false
        gameStatus = GameStatus.PLAYING
    }

    // Annuler
    fun undo() {
        if (!isAITurn && board.undo()) {
            selectedPiece = null
            possibleMoves = emptyList()
            mustContinueCapture = false
            lastMovedPosition = null
            canUndo = board.canUndo()
            canRedo = board.canRedo()
            refreshTrigger++
            checkGameOver()
        }
    }

    // Refaire
    fun redo() {
        if (!isAITurn && board.redo()) {
            selectedPiece = null
            possibleMoves = emptyList()
            mustContinueCapture = false
            lastMovedPosition = null
            canUndo = board.canUndo()
            canRedo = board.canRedo()
            refreshTrigger++
            checkGameOver()
        }
    }

    // Tour de l'IA
    fun playAITurn() {
        scope.launch {
            isAITurn = true
            delay(500)

            val firstMove = board.getRandomMove(PieceColor.BLACK)

            if (firstMove != null) {
                val (from, to) = firstMove
                val piece = board.getPiece(from)

                if (piece != null) {
                    val isCapture = board.canCapture(from, piece).any { it.first == to }
                    board.movePiece(from, to)
                    refreshTrigger++

                    if (isCapture) {
                        delay(500)
                        var currentPosition = to

                        while (board.canCaptureFrom(currentPosition)) {
                            val currentPiece = board.getPiece(currentPosition)
                            if (currentPiece != null) {
                                val captures = board.canCapture(currentPosition, currentPiece)
                                if (captures.isNotEmpty()) {
                                    val nextCapture = captures.random().first
                                    board.movePiece(currentPosition, nextCapture)
                                    currentPosition = nextCapture
                                    refreshTrigger++
                                    delay(500)
                                } else break
                            } else break
                        }
                    }
                }
            }

            board.saveState()
            board.switchPlayer()
            isAITurn = false
            canUndo = board.canUndo()
            canRedo = board.canRedo()
            refreshTrigger++
            checkGameOver()
        }
    }

    // Gérer la sélection de pièce
    fun handlePieceSelection(position: Position) {
        if (isAITurn || gameStatus != GameStatus.PLAYING) return

        val piece = board.getPiece(position)

        // Mode capture multiple
        if (mustContinueCapture && lastMovedPosition != null) {
            handleContinueCapture(position, piece)
            return
        }

        // Désélection
        if (selectedPiece == position) {
            selectedPiece = null
            possibleMoves = emptyList()
            return
        }

        // Sélection d'une pièce
        if (piece != null && piece.color == board.currentPlayer) {
            selectPiece(position, piece)
            return
        }

        // Déplacement vers une destination valide
        if (selectedPiece != null && possibleMoves.contains(position)) {
            movePieceToPosition(position)
        }
    }

    private fun handleContinueCapture(position: Position, piece: Piece?) {
        if (selectedPiece != null && possibleMoves.contains(position)) {
            val fromPosition = selectedPiece!!
            val movingPiece = board.getPiece(fromPosition)

            if (movingPiece != null && board.movePiece(fromPosition, position)) {
                if (board.canCaptureFrom(position)) {
                    lastMovedPosition = position
                    selectedPiece = position
                    val movedPiece = board.getPiece(position)!!
                    possibleMoves = board.canCapture(position, movedPiece).map { it.first }
                    refreshTrigger++
                } else {
                    endPlayerTurn()
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
        }
    }

    private fun selectPiece(position: Position, piece: Piece) {
        selectedPiece = position
        val captures = board.canCapture(position, piece)
        possibleMoves = if (captures.isNotEmpty()) {
            captures.map { it.first }
        } else if (!board.hasCaptures(piece.color)) {
            board.getPossibleMoves(position, piece)
        } else {
            emptyList()
        }
    }

    private fun movePieceToPosition(position: Position) {
        val fromPosition = selectedPiece!!
        val movingPiece = board.getPiece(fromPosition)

        if (movingPiece != null) {
            val wasCapture = board.canCapture(fromPosition, movingPiece).any { it.first == position }

            if (board.movePiece(fromPosition, position)) {
                if (wasCapture && board.canCaptureFrom(position)) {
                    mustContinueCapture = true
                    lastMovedPosition = position
                    selectedPiece = position
                    val movedPiece = board.getPiece(position)!!
                    possibleMoves = board.canCapture(position, movedPiece).map { it.first }
                    refreshTrigger++
                } else {
                    endPlayerTurn()
                }
            }
        }
    }

    private fun endPlayerTurn() {
        selectedPiece = null
        possibleMoves = emptyList()
        mustContinueCapture = false
        lastMovedPosition = null
        board.saveState()
        board.switchPlayer()
        canUndo = board.canUndo()
        canRedo = board.canRedo()
        refreshTrigger++
        checkGameOver()

        if (gameStatus == GameStatus.PLAYING) {
            playAITurn()
        }
    }
}