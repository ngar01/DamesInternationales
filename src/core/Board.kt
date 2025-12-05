package core

data class Position(val row: Int, val col: Int)

enum class PieceColor { WHITE, BLACK }

data class Piece(val color: PieceColor, val isQueen: Boolean = false)

class Board {
    private val grid: Array<Array<Piece?>> = Array(10) { arrayOfNulls(10) }

    fun placePiece(piece: Piece, position: Position) {
        grid[position.row][position.col] = piece
    }

    fun movePiece(from: Position, to: Position): Boolean {
        // Logique de déplacement à implémenter
        return true
    }
}
