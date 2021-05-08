package com.livetl.android.ui.common

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString

private val symbolPattern by lazy {
    """(https?://[^\s\t\n]+)|(:[\w+]+:)|(#[\w+]+)""".toRegex()
}

// Accepted annotations
enum class SymbolAnnotationType(val firstToken: Char) {
    LINK('h'),
    EMOJI(':'),
    HASHTAG('#'),
}
typealias StringAnnotation = AnnotatedString.Range<String>

// Pair returning styled content and annotation for ClickableText when matching syntax token
typealias SymbolAnnotation = Pair<AnnotatedString, StringAnnotation?>

/**
 * Parses a string so that it's renderable with its content.
 *
 * http(s)://... -> clickable link, opening in a browser
 * :_text:       -> custom chat emote
 * #text         -> hashtag
 *
 * @param text contains message to be parsed
 * @return AnnotatedString with annotations used inside the ClickableText wrapper
 */
@Composable
fun textParser(
    text: String,
    parsedContentTypes: Collection<String> = SymbolAnnotationType.values().map { it.name },
): AnnotatedString {
    val tokens = symbolPattern.findAll(text)

    return buildAnnotatedString {
        var cursorPosition = 0

        for (token in tokens) {
            append(text.slice(cursorPosition until token.range.first))

            if (SymbolAnnotationType.EMOJI.name in parsedContentTypes && token.value.first() == SymbolAnnotationType.EMOJI.firstToken) {
                // Emotes are replaced with placeholders later
                appendInlineContent(token.value, token.value)
            } else {
                val (annotatedString, stringAnnotation) = getSymbolAnnotation(
                    matchResult = token,
                    colors = MaterialTheme.colors,
                    parsedContentTypes = parsedContentTypes,
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
    parsedContentTypes: Collection<String>,
): SymbolAnnotation {
    return when {
        SymbolAnnotationType.LINK.name in parsedContentTypes && matchResult.value.first() == SymbolAnnotationType.LINK.firstToken -> SymbolAnnotation(
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
        SymbolAnnotationType.HASHTAG.name in parsedContentTypes && matchResult.value.first() == SymbolAnnotationType.HASHTAG.firstToken -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value,
                spanStyle = SpanStyle(
                    color = colors.primary
                )
            ),
            StringAnnotation(
                item = matchResult.value.substring(1),
                start = matchResult.range.first,
                end = matchResult.range.last,
                tag = SymbolAnnotationType.HASHTAG.name
            )
        )
        else -> SymbolAnnotation(AnnotatedString(matchResult.value), null)
    }
}
