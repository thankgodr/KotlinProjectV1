package me.thankgodr.fintechchallegeapp.presentation.utils

fun Double.toTwoDecimalString(): String {
    val raw = this.toString()

    // handle scientific notation like 1.0E-4
    if (raw.contains('E') || raw.contains('e')) {
        val plain = raw.toBigDecimalString()
        return plain.toTwoDecimalStringFromPlain()
    }

    return raw.toTwoDecimalStringFromPlain()
}

private fun String.toTwoDecimalStringFromPlain(): String {
    val parts = split(".")

    val whole = parts[0]
    val decimals = parts.getOrNull(1) ?: ""

    // Truncate to 2 decimal places (no rounding) for display
    val resultDecimals = when {
        decimals.isEmpty() -> "00"
        decimals.length == 1 -> decimals + "0"
        else -> decimals.substring(0, 2) // truncate only
    }

    return "$whole.$resultDecimals"
}

private fun String.toBigDecimalString(): String {
    val value = this.toDouble()
    val sign = if (value < 0) "-" else ""
    val abs = kotlin.math.abs(value)

    val s = abs.toString()
    if (!s.contains("E")) return sign + s

    val parts = s.split("E")
    val base = parts[0].replace(".", "")
    val exponent = parts[1].toInt()

    return if (exponent < 0) {
        val zeros = "0".repeat(-exponent - 1)
        "$sign" + "0.$zeros$base"
    } else {
        val zeros = "0".repeat(exponent - (base.length - 1))
        "$sign$base$zeros"
    }
}