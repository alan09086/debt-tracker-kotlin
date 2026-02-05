package com.debttracker.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.debttracker.app.ui.theme.*

@Composable
fun MatrixCard(
    modifier: Modifier = Modifier,
    borderColor: Color = MatrixGreenBorder,
    accentColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .background(MatrixBlack)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        // Accent bar on the left
        if (accentColor != null) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(accentColor)
                    .align(Alignment.CenterStart)
            )
        }
        Column(
            modifier = Modifier.padding(
                start = if (accentColor != null) 16.dp else 12.dp,
                end = 12.dp,
                top = 12.dp,
                bottom = 12.dp
            ),
            content = content
        )
    }
}

@Composable
fun MatrixButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isDanger: Boolean = false,
    compact: Boolean = false
) {
    val borderColor = if (isDanger) MatrixRed else MatrixGreen
    val textColor = if (isDanger) MatrixRed else MatrixGreen

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, if (enabled) borderColor else borderColor.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = textColor,
            disabledContentColor = textColor.copy(alpha = 0.5f)
        ),
        contentPadding = if (compact) PaddingValues(horizontal = 8.dp, vertical = 4.dp) else ButtonDefaults.ContentPadding
    ) {
        Text(
            text = if (compact) text else "[ $text ]",
            style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelLarge,
            maxLines = 1
        )
    }
}

@Composable
fun MatrixIconButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDanger: Boolean = false
) {
    val borderColor = if (isDanger) MatrixRed else MatrixGreen

    OutlinedIconButton(
        onClick = onClick,
        modifier = modifier.size(40.dp),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = IconButtonDefaults.outlinedIconButtonColors(
            contentColor = borderColor
        )
    ) {
        icon()
    }
}

@Composable
fun MatrixTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    Column(modifier = modifier) {
        Text(
            text = "> $label",
            style = MaterialTheme.typography.labelMedium,
            color = MatrixGreen
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MatrixGreen,
                unfocusedBorderColor = MatrixGreenBorder,
                focusedTextColor = MatrixGreen,
                unfocusedTextColor = MatrixGreen,
                cursorColor = MatrixGreen,
                focusedContainerColor = MatrixBlack,
                unfocusedContainerColor = MatrixBlack
            )
        )
    }
}

@Composable
fun BalanceBadge(
    balance: Double,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, borderColor, textColor) = when {
        balance > 0 -> Triple(MatrixGreen.copy(alpha = 0.1f), MatrixGreen, MatrixGreen)
        balance < 0 -> Triple(MatrixRed.copy(alpha = 0.1f), MatrixRed, MatrixRed)
        else -> Triple(MatrixGreen.copy(alpha = 0.05f), MatrixGreenBorder, MatrixGreenDim)
    }

    val text = when {
        balance > 0 -> "+$${String.format("%.2f", balance)}"
        balance < 0 -> "-$${String.format("%.2f", kotlin.math.abs(balance))}"
        else -> "$0.00"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
    }
}

@Composable
fun MatrixHeader(
    title: String,
    modifier: Modifier = Modifier,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MatrixDark,
        border = BorderStroke(1.dp, MatrixGreen),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingContent?.invoke()
            Text(
                text = "[ $title ]",
                style = MaterialTheme.typography.headlineMedium,
                color = MatrixGreen,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            trailingContent?.invoke()
        }
    }
}

@Composable
fun MatrixDivider(
    modifier: Modifier = Modifier
) {
    Divider(
        modifier = modifier,
        thickness = 1.dp,
        color = MatrixGreenBorderDark
    )
}
