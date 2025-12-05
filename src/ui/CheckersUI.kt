package ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CheckerPiece(color: Color, isQueen: Boolean) {
    Canvas(modifier = Modifier.size(50.dp)) {
        drawCircle(color = color, radius = size.minDimension / 2)
        if (isQueen) {
            drawCircle(color = Color.White, radius = size.minDimension / 4)
        }
    }
}
