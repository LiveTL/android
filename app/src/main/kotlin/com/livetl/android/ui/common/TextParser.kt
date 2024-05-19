package com.livetl.android.ui.common

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink

private val symbolPattern by lazy {
    """(https?://[^\s\t\n]+)|(:[\w+]+:)|(#[\w+]+)""".toRegex()
}

// Accepted annotations
enum class SymbolAnnotationType(val firstToken: Char) {
    LINK('h'),
    EMOJI(':'),
    HASHTAG('#'),
}

// Pair returning styled content and annotation for ClickableText when matching syntax token
typealias SymbolAnnotation = Pair<String, LinkAnnotation?>

/**
 * Parses a string so that it's renderable with its content.
 *
 * http(s)://... -> clickable link, opening in a browser
 * :text:        -> default chat emote
 * :_text:       -> custom chat emote
 * #text         -> hashtag
 *
 * @param text contains message to be parsed
 * @return AnnotatedString with annotations used inside the ClickableText wrapper
 */
@Composable
fun textParser(
    text: String,
    parsedContentTypes: Collection<String> = SymbolAnnotationType.entries.map { it.name },
): AnnotatedString {
    val tokens = symbolPattern.findAll(text)

    return buildAnnotatedString {
        var cursorPosition = 0

        for (token in tokens) {
            append(text.slice(cursorPosition until token.range.first))

            if (SymbolAnnotationType.EMOJI.name in parsedContentTypes &&
                token.value.first() == SymbolAnnotationType.EMOJI.firstToken
            ) {
                // Emotes are replaced with placeholders later
                appendInlineContent(token.value, token.value)
            } else {
                val (string, linkAnnotation) =
                    getSymbolAnnotation(
                        matchResult = token,
                        colors = MaterialTheme.colorScheme,
                        parsedContentTypes = parsedContentTypes,
                    )

                if (linkAnnotation != null) {
                    withLink(linkAnnotation) {
                        append(string)
                    }
                } else {
                    append(string)
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
    colors: ColorScheme,
    parsedContentTypes: Collection<String>,
): SymbolAnnotation = when {
    SymbolAnnotationType.LINK.name in parsedContentTypes &&
        matchResult.value.first() == SymbolAnnotationType.LINK.firstToken ->
        SymbolAnnotation(
            matchResult.value,
            LinkAnnotation.Url(
                url = matchResult.value,
                style = SpanStyle(
                    color = colors.primary,
                ),
            ),
        )

    SymbolAnnotationType.HASHTAG.name in parsedContentTypes &&
        matchResult.value.first() == SymbolAnnotationType.HASHTAG.firstToken ->
        SymbolAnnotation(
            matchResult.value,
            LinkAnnotation.Url(
                url = "https://www.youtube.com/hashtag/${matchResult.value.substring(1)}",
                style = SpanStyle(
                    color = colors.primary,
                ),
            ),
        )

    else -> SymbolAnnotation(matchResult.value, null)
}
