package ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import core.Board
import core.Position

@Composable
fun CheckersBoard(board: Board) {
    Canvas(modifier = Modifier.size(500.dp)) {
        val cellSize = size.width / 10
        for (row in 0 until 10) {
            for (col in 0 until 10) {
                val isDarkCell = (row + col) % 2 == 1
                drawRect(
                    color = if (isDarkCell) Color.DarkGray else Color.LightGray,
                    topLeft = Offset(col * cellSize, row * cellSize),
                    //size = android.graphics.Size(cellSize, cellSize)
                )
                val piece = board.getPiece(Position(row, col))
                if (piece != null) {
                    drawCircle(
                        color = if (piece.color == core.PieceColor.BLACK) Color.Black else Color.White,
                        center = Offset(col * cellSize + cellSize / 2, row * cellSize + cellSize / 2),
                        radius = cellSize / 3
                    )
                }
            }
        }
    }
}
