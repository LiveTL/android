package com.livetl.android.ui

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString

// Regex containing the syntax tokens
val symbolPattern by lazy {
    Regex("""(https?://[^\s\t\n]+)|(:[\w+]+:)""")
}

// Accepted annotations for the ClickableTextWrapper
enum class SymbolAnnotationType {
    LINK
}
typealias StringAnnotation = AnnotatedString.Range<String>
// Pair returning styled content and annotation for ClickableText when matching syntax token
typealias SymbolAnnotation = Pair<AnnotatedString, StringAnnotation?>

/**
 * Format a message following Markdown-lite syntax
 * | http(s)://... -> clickable link, opening it into the browser
 * | :_text: -> emote
 *
 * @param text contains message to be parsed
 * @return AnnotatedString with annotations used inside the ClickableText wrapper
 */
@Composable
fun messageFormatter(text: String): AnnotatedString {
    val tokens = symbolPattern.findAll(text)

    return buildAnnotatedString {
        var cursorPosition = 0

        for (token in tokens) {
            append(text.slice(cursorPosition until token.range.first))

            if (token.value.first() == ':') {
                // Emotes are replaced with placeholders later
                appendInlineContent(token.value, token.value)
            } else {
                val (annotatedString, stringAnnotation) = getSymbolAnnotation(
                    matchResult = token,
                    colors = MaterialTheme.colors,
                )
                append(annotatedString)

                if (stringAnnotation != null) {
                    val (item, start, end, tag) = stringAnnotation
                    addStringAnnotation(tag = tag, start = start, end = end, annotation = item)
                }
            }

            cursorPosition = token.range.last + 1
        }

        if (!tokens.none()) {
            append(text.slice(cursorPosition..text.lastIndex))
        } else {
            append(text)
        }
    }
}

/**
 * Map regex matches found in a message with supported syntax symbols
 *
 * @param matchResult is a regex result matching our syntax symbols
 * @return pair of AnnotatedString with annotation (optional) used inside the ClickableText wrapper
 */
private fun getSymbolAnnotation(
    matchResult: MatchResult,
    colors: Colors,
): SymbolAnnotation {
    return when (matchResult.value.first()) {
        'h' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value,
                spanStyle = SpanStyle(
                    color = colors.primary
                )
            ),
            StringAnnotation(
                item = matchResult.value,
                start = matchResult.range.first,
                end = matchResult.range.last,
                tag = SymbolAnnotationType.LINK.name
            )
        )
        else -> SymbolAnnotation(AnnotatedString(matchResult.value), null)
    }
}
