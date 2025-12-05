package core

import kotlin.math.abs

data class Position(val row: Int, val col: Int)

enum class PieceColor { WHITE, BLACK }

data class Piece(val color: PieceColor, val isQueen: Boolean = false)

class Board {
    private val grid: Array<Array<Piece?>> = Array(10) { arrayOfNulls(10) }

    init {
        // Initialiser les pions pour chaque joueur
        for (row in 0 until 4) {
            for (col in 0 until 10) {
                if ((row + col) % 2 == 1) {
                    grid[row][col] = Piece(PieceColor.BLACK)
                }
            }
        }
        for (row in 6 until 10) {
            for (col in 0 until 10) {
                if ((row + col) % 2 == 1) {
                    grid[row][col] = Piece(PieceColor.WHITE)
                }
            }
        }
    }

    fun getPiece(position: Position): Piece? = grid[position.row][position.col]

    fun movePiece(from: Position, to: Position): Boolean {
        // Vérifier si le mouvement est valide
        val piece = grid[from.row][from.col] ?: return false
        if (!isValidMove(from, to, piece)) return false

        // Déplacer la pièce
        grid[to.row][to.col] = piece
        grid[from.row][from.col] = null
        return true
    }

    private fun isValidMove(from: Position, to: Position, piece: Piece): Boolean {
        // Logique de validation des mouvements
        val rowDiff = to.row - from.row
        val colDiff = abs(to.col - from.col)

        // Mouvement diagonal vers l'avant
        val direction = if (piece.color == PieceColor.BLACK) 1 else -1
        return colDiff == 1 && rowDiff == direction
    }
}
