package com.example.kavyakanaja.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.kavyakanaja.R

private val baseline = Typography()

val PoppinsFamily = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_semibold, FontWeight.Bold)
)

val RobotoFamily = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_medium, FontWeight.SemiBold),
    Font(R.font.roboto_medium, FontWeight.Bold)
)

val NotoSansKannadaFamily = FontFamily(
    Font(R.font.notosanskannada_regular, FontWeight.Normal)
)

fun kannadaContentStyle(base: TextStyle): TextStyle =
    base.copy(
        fontFamily = NotoSansKannadaFamily,
        fontSize = 20.sp,
        lineHeight = 32.sp
    )

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = PoppinsFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = PoppinsFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = PoppinsFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = PoppinsFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = PoppinsFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = PoppinsFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = PoppinsFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = PoppinsFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = PoppinsFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = RobotoFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = RobotoFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = RobotoFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = RobotoFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = RobotoFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = RobotoFamily)
)
