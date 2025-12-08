package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.GameMode
import core.GameModes

@Composable
fun GameModeDropdown(
    currentMode: GameMode,
    onModeSelected: (GameMode) -> Unit,
    theme: CheckersTheme,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val currentModeInfo = GameModes.getInfo(currentMode)

    Box {
        // Bouton principal
        Button(
            onClick = { expanded = true },
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(backgroundColor = theme.buttonColor),
            modifier = Modifier.height(40.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = currentModeInfo.icon,
                    fontSize = 16.sp
                )

                Text(
                    text = currentModeInfo.name,
                    color = Color.White,
                    fontSize = 14.sp
                )

                Text(
                    text = "▼",
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }

        // Menu déroulant
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(250.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
        ) {
            GameModes.MODES.forEach { modeInfo ->
                DropdownMenuItem(
                    onClick = {
                        onModeSelected(modeInfo.mode)
                        expanded = false
                    },
                    modifier = Modifier.height(60.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = modeInfo.icon,
                                fontSize = 24.sp
                            )

                            Column {
                                Text(
                                    text = modeInfo.name,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = modeInfo.description,
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        // Indicateur de sélection
                        if (currentMode == modeInfo.mode) {
                            Text(
                                text = "✓",
                                color = theme.buttonColor,
                                fontSize = 18.sp
                            )
                        }
                    }
                }

                if (modeInfo.mode != GameMode.PLAYER_VS_PLAYER) {
                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                }
            }
        }
    }
}