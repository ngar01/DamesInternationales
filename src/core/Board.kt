// src/main/kotlin/core/Board.kt
package core

data class Position(val row: Int, val col: Int)

enum class PieceColor { WHITE, BLACK }

data class Piece(val color: PieceColor, val isQueen: Boolean = false)

// Classe pour sauvegarder l'état du plateau (pour undo/redo)
data class BoardState(
    val grid: Array<Array<Piece?>>,
    val currentPlayer: PieceColor
) {
    fun copy(): BoardState {
        val newGrid = Array(10) { row ->
            Array(10) { col ->
                grid[row][col]?.copy()
            }
        }
        return BoardState(newGrid, currentPlayer)
    }
}

class Board {
    private val grid: Array<Array<Piece?>> = Array(10) { arrayOfNulls(10) }
    var currentPlayer: PieceColor = PieceColor.WHITE

    // Historique pour undo/redo
    private val history = mutableListOf<BoardState>()
    private var historyIndex = -1

    init {
        // Initialiser les pions noirs (rangées 0-3)
        for (row in 0 until 4) {
            for (col in 0 until 10) {
                if ((row + col) % 2 == 1) grid[row][col] = Piece(PieceColor.BLACK)
            }
        }
        // Initialiser les pions blancs (rangées 6-9)
        for (row in 6 until 10) {
            for (col in 0 until 10) {
                if ((row + col) % 2 == 1) grid[row][col] = Piece(PieceColor.WHITE)
            }
        }

        // Sauvegarder l'état initial
        saveState()
    }

    fun getPiece(position: Position): Piece? = grid[position.row][position.col]

    private fun isValidPosition(row: Int, col: Int): Boolean = row in 0 until 10 && col in 0 until 10

    // Sauvegarder l'état actuel du plateau
    fun saveState() {
        // Supprimer tous les états après l'index actuel (pour le redo)
        while (history.size > historyIndex + 1) {
            history.removeAt(history.size - 1)
        }

        // Copier le plateau actuel
        val newGrid = Array(10) { row ->
            Array(10) { col ->
                grid[row][col]?.copy()
            }
        }
        history.add(BoardState(newGrid, currentPlayer))
        historyIndex++
    }

    // Annuler le dernier coup
    fun undo(): Boolean {
        if (historyIndex > 0) {
            historyIndex--
            restoreState(history[historyIndex])
            return true
        }
        return false
    }

    // Refaire le coup annulé
    fun redo(): Boolean {
        if (historyIndex < history.size - 1) {
            historyIndex++
            restoreState(history[historyIndex])
            return true
        }
        return false
    }

    fun canUndo(): Boolean = historyIndex > 0
    fun canRedo(): Boolean = historyIndex < history.size - 1

    private fun restoreState(state: BoardState) {
        for (row in 0 until 10) {
            for (col in 0 until 10) {
                grid[row][col] = state.grid[row][col]?.copy()
            }
        }
        currentPlayer = state.currentPlayer
    }

    // Obtenir tous les mouvements simples possibles (sans capture)
    fun getPossibleMoves(from: Position, piece: Piece): List<Position> {
        val moves = mutableListOf<Position>()
        val directions = if (piece.isQueen) listOf(-1, 1) else listOf(if (piece.color == PieceColor.WHITE) -1 else 1)

        for (rowDir in directions) {
            for (colDir in listOf(-1, 1)) {
                val toRow = from.row + rowDir
                val toCol = from.col + colDir
                if (isValidPosition(toRow, toCol) && grid[toRow][toCol] == null) {
                    moves.add(Position(toRow, toCol))
                }
            }
        }
        return moves
    }

    // Vérifier les captures possibles depuis une position
    // Retourne: Liste de Pair(destination, position du pion capturé)
    fun canCapture(from: Position, piece: Piece): List<Pair<Position, Position>> {
        val captures = mutableListOf<Pair<Position, Position>>()
        val directions = if (piece.isQueen) listOf(-1, 1) else listOf(if (piece.color == PieceColor.WHITE) -1 else 1)

        for (rowDir in directions) {
            for (colDir in listOf(-1, 1)) {
                val midRow = from.row + rowDir
                val midCol = from.col + colDir
                val toRow = from.row + 2 * rowDir
                val toCol = from.col + 2 * colDir

                if (isValidPosition(midRow, midCol) && isValidPosition(toRow, toCol)) {
                    val midPiece = grid[midRow][midCol]
                    val toPiece = grid[toRow][toCol]
                    // Vérifier qu'il y a un pion adverse à capturer et que la destination est vide
                    if (midPiece != null && midPiece.color != piece.color && toPiece == null) {
                        captures.add(Position(toRow, toCol) to Position(midRow, midCol))
                    }
                }
            }
        }
        return captures
    }

    // Vérifier si le joueur actuel a des captures obligatoires
    fun hasCaptures(color: PieceColor): Boolean {
        for (row in 0 until 10) {
            for (col in 0 until 10) {
                val piece = grid[row][col]
                if (piece?.color == color) {
                    if (canCapture(Position(row, col), piece).isNotEmpty()) {
                        return true
                    }
                }
            }
        }
        return false
    }

    // Déplacer un pion avec gestion des captures
    // Retourne true si le mouvement a réussi
    fun movePiece(from: Position, to: Position): Boolean {
        val piece = grid[from.row][from.col] ?: return false

        // Vérifier si le joueur a des captures obligatoires
        val playerHasCaptures = hasCaptures(piece.color)
        val thisCaptures = canCapture(from, piece)

        // Si des captures sont possibles globalement, ce mouvement doit être une capture
        if (playerHasCaptures) {
            if (thisCaptures.isEmpty()) {
                // Ce pion ne peut pas capturer alors que d'autres le peuvent
                return false
            }
            // Vérifier que la destination 'to' fait partie des captures possibles
            val captureInfo = thisCaptures.find { it.first == to }
            if (captureInfo == null) {
                return false
            }
        }

        // Effectuer le déplacement
        var movedPiece = piece
        grid[to.row][to.col] = movedPiece
        grid[from.row][from.col] = null

        // Si c'est une capture, supprimer le pion capturé
        if (thisCaptures.isNotEmpty()) {
            val captureInfo = thisCaptures.find { it.first == to }
            if (captureInfo != null) {
                val capturedPos = captureInfo.second
                grid[capturedPos.row][capturedPos.col] = null
            }
        }

        // Promotion en dame (avant de vérifier les captures multiples)
        if ((movedPiece.color == PieceColor.WHITE && to.row == 0) ||
            (movedPiece.color == PieceColor.BLACK && to.row == 9)) {
            movedPiece = Piece(movedPiece.color, isQueen = true)
            grid[to.row][to.col] = movedPiece
        }

        return true
    }

    // Vérifier si une pièce à une position donnée peut encore capturer
    fun canCaptureFrom(position: Position): Boolean {
        val piece = grid[position.row][position.col] ?: return false
        return canCapture(position, piece).isNotEmpty()
    }

    // IA : Trouver un coup aléatoire pour l'ordinateur (avec support des prises multiples)
    fun getRandomMove(color: PieceColor): Pair<Position, Position>? {
        val pieces = mutableListOf<Position>()
        for (row in 0 until 10) {
            for (col in 0 until 10) {
                val piece = grid[row][col]
                if (piece?.color == color) pieces.add(Position(row, col))
            }
        }

        // Priorité aux captures (obligatoires)
        for (from in pieces.shuffled()) {
            val piece = grid[from.row][from.col] ?: continue
            val captures = canCapture(from, piece)
            if (captures.isNotEmpty()) {
                return from to captures.random().first
            }
        }

        // Si aucune capture, faire un mouvement simple
        for (from in pieces.shuffled()) {
            val piece = grid[from.row][from.col] ?: continue
            val moves = getPossibleMoves(from, piece)
            if (moves.isNotEmpty()) return from to moves.random()
        }

        return null
    }

    fun switchPlayer() {
        currentPlayer = if (currentPlayer == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
    }
}