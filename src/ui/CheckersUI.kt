package ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import core.Board
import core.PieceColor
import core.Position

@Composable
fun CheckersBoard(
    board: Board,
    onPieceSelected: (Position) -> Unit,
    selectedPiece: Position?,
    possibleMoves: List<Position>,
    theme: CheckersTheme = Themes.CLASSIC
) {
    Canvas(
        modifier = Modifier
            .size(500.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val cellSize = size.width / 10
                    val col = (offset.x / cellSize).toInt()
                    val row = (offset.y / cellSize).toInt()
                    if (row in 0 until 10 && col in 0 until 10) {
                        onPieceSelected(Position(row, col))
                    }
                }
            }
    ) {
        val cellSize = size.width / 10

        // Dessiner le plateau (cases alternées)
        for (row in 0 until 10) {
            for (col in 0 until 10) {
                val isDarkCell = (row + col) % 2 == 1
                drawRect(
                    color = if (isDarkCell) theme.darkSquare else theme.lightSquare,
                    topLeft = Offset(col * cellSize, row * cellSize),
                    size = Size(cellSize, cellSize)
                )
            }
        }

        // Afficher les mouvements possibles (cercles)
        possibleMoves.forEach { pos ->
            drawCircle(
                color = theme.possibleMoveHighlight,
                center = Offset(pos.col * cellSize + cellSize / 2, pos.row * cellSize + cellSize / 2),
                radius = cellSize / 5
            )
        }

        // Afficher la sélection du pion (cercle de surbrillance)
        selectedPiece?.let { pos ->
            drawCircle(
                color = theme.selectedHighlight,
                center = Offset(pos.col * cellSize + cellSize / 2, pos.row * cellSize + cellSize / 2),
                radius = cellSize / 2.5f
            )
        }

        // Dessiner les pions
        for (row in 0 until 10) {
            for (col in 0 until 10) {
                val piece = board.getPiece(Position(row, col))
                if (piece != null) {
                    val center = Offset(col * cellSize + cellSize / 2, row * cellSize + cellSize / 2)

                    // Pion principal
                    drawCircle(
                        color = if (piece.color == PieceColor.WHITE) theme.whitePiece else theme.blackPiece,
                        center = center,
                        radius = cellSize / 3
                    )

                    // Bordure pour meilleure visibilité
                    drawCircle(
                        color = if (piece.color == PieceColor.WHITE) theme.whitePieceBorder else theme.blackPieceBorder,
                        center = center,
                        radius = cellSize / 3,
                        style = Stroke(width = 2f)
                    )

                    // Marque pour les dames
                    if (piece.isQueen) {
                        drawCircle(
                            color = theme.queenMarker,
                            center = center,
                            radius = cellSize / 6
                        )
                    }
                }
            }
        }
    }
}