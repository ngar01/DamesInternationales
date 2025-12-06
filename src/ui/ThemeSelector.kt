// src/main/kotlin/ui/ThemeSelector.kt
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

@Composable
fun ThemeDropdown(
    currentTheme: ThemeType,
    onThemeSelected: (ThemeType) -> Unit,
    theme: CheckersTheme
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        // Bouton principal
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(backgroundColor = theme.buttonColor),
            modifier = Modifier.height(40.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "🎨",
                    fontSize = 16.sp
                )

                // Mini aperçu du thème actuel
                Row {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(theme.lightSquare)
                    )
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(theme.darkSquare)
                    )
                }

                Text(
                    text = theme.name,
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
                .width(200.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
        ) {
            Themes.getAllThemes().forEach { (type, themeData) ->
                DropdownMenuItem(
                    onClick = {
                        onThemeSelected(type)
                        expanded = false
                    },
                    modifier = Modifier.height(48.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Aperçu du thème
                            Row {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(themeData.lightSquare)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(themeData.darkSquare)
                                )
                            }

                            Text(
                                text = themeData.name,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }

                        // Indicateur de sélection
                        if (currentTheme == type) {
                            Text(
                                text = "✓",
                                color = themeData.buttonColor,
                                fontSize = 18.sp
                            )
                        }
                    }
                }

                if (type != ThemeType.DARK) {
                    Divider(color = Color.LightGray, thickness = 0.5.dp)
                }
            }
        }
    }
}