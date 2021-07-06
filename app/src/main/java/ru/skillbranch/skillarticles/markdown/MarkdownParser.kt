package ru.skillbranch.skillarticles.markdown

import java.util.regex.Pattern

object MarkdownParser {

    private val LINE_SEPARATOR = System.getProperty("line.separator") ?: "\n"

    private const val UNORDERED_LIST_ITEM_GROUP = "(^[*+-] .+$)"
    private const val HEADER_GROUP = "(^#{1,6} .+?$)"
    private const val QUOTE_GROUP = "(^> .+?$)"
    private const val ITALIC_GROUP = "((?<!\\*)\\*[^*].*?[^*]?\\*(?!\\*)|(?<!_)_[^_].*?[^_]?_(?!_))"
    private const val BOLD_GROUP =
        "((?<!\\*)\\*{2}[^*].*?[^*]?\\*{2}(?!\\*)|(?<!_)_{2}[^_].*?[^_]?_{2}(?!_))"
    private const val STRIKE_GROUP = "((?<!~)~{2}[^~].*?~{2}(?!~))"
    private const val RULE_GROUP = "(^[-_*]{3}$)"
    private const val INLINE_CODE_GROUP = "((?<!`)`[^`\\s].*?[^`\\s]?`(?!`))"
    private const val LINK_GROUP = "((?<!!)\\[[^\\[\\]]*?]\\(.+?\\)|^\\[*?]\\(.*?\\))"
    private const val ORDERED_LIST_ITEM_GROUP = "(^\\d+?\\. .+$)"
    private const val MULTILINE_CODE_GROUP = "(^(?<!`)`{3}[^`\\s][\\w\\s\\n\\.]*?[^`\\s]?`{3}(?!`)$)"
    private const val IMAGE_GROUP = "(\\!\\[.*\\]\\(.*\\))"

    private const val MARKDOWN_GROUPS = "$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP|$QUOTE_GROUP" +
            "|$ITALIC_GROUP|$BOLD_GROUP|$STRIKE_GROUP|$RULE_GROUP|$INLINE_CODE_GROUP|$LINK_GROUP" +
            "|$ORDERED_LIST_ITEM_GROUP|$MULTILINE_CODE_GROUP|$IMAGE_GROUP"

    private val elementsPattern by lazy { Pattern.compile(MARKDOWN_GROUPS, Pattern.MULTILINE) }

    fun parse(string: String): MarkdownText {
        val elements = mutableListOf<Element>()
        elements.addAll(findElements(string))
        return MarkdownText(elements)
    }

    fun clear(string: String): String? {
        val text = parse(string)
        return text.elements.spreadText()
    }

    private fun Element.spreadText(): String {
        val builder = StringBuilder()
        if (elements.isNotEmpty()) {
            builder.append(elements.spreadText())
        } else {
            builder.append(text)
        }
        return builder.toString()
    }

    private fun List<Element>.spreadText(): String {
        val builder = StringBuilder()

        if (this.isNotEmpty()) {
            this.fold(builder) { acc, el -> acc.also { it.append(el.spreadText()) } }
        }
        return builder.toString()
    }

    private fun findElements(string: CharSequence): List<Element> {
        val parents = mutableListOf<Element>()
        val matcher = elementsPattern.matcher(string)
        var lastStartIndex = 0

        loop@ while (matcher.find(lastStartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()

            // if something was found then everything before is TEXT
            if (lastStartIndex < startIndex) {
                parents.add(Element.Text(string.subSequence(lastStartIndex, startIndex)))
            }

            // found text
            var text: CharSequence

            // groups range to iterate by groups
            val groups = 1..12
            var group = -1
            for (gr in groups) {
                if (matcher.group(gr) != null) {
                    group = gr
                    break
                }
            }

            when (group) {
                // NOT FOUND -> BREAK
                -1 -> break@loop

                // UNORDERED LIST
                1 -> {
                    // text without "* "
                    text = string.subSequence(startIndex.plus(2), endIndex)

                    // find inner elements
                    val subs = findElements(text)
                    val element = Element.UnorderedListItem(text, subs)
                    parents.add(element)
                }

                // HEADER
                2 -> {
                    val reg = "^#{1,6}".toRegex().find(string.subSequence(startIndex, endIndex))
                    val level = reg!!.value.length

                    // text without "{#} "
                    text = string.subSequence(startIndex.plus(level.inc()), endIndex)
                    val element = Element.Header(level, text)
                    parents.add(element)
                }

                // QUOTE
                3 -> {
                    // text without "> "
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    val subElements = findElements(text)
                    val element = Element.Quote(text, subElements)
                    parents.add(element)
                }

                // ITALIC
                4 -> {
                    // text without "*{}*"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val subElements = findElements(text)
                    val element = Element.Italic(text, subElements)
                    parents.add(element)
                }

                // BOLD
                5 -> {
                    // text without "**{}**"
                    text = string.subSequence(startIndex.plus(2), endIndex.plus(-2))
                    val subElements = findElements(text)
                    val element = Element.Bold(text, subElements)
                    parents.add(element)
                }

                // STRIKE
                6 -> {
                    // text without "~~{}~~"
                    text = string.subSequence(startIndex.plus(2), endIndex.plus(-2))
                    val subElements = findElements(text)
                    val element = Element.Strike(text, subElements)
                    parents.add(element)
                }

                // RULE
                7 -> {
                    // text without "***", insert empty character
                    val element = Element.Rule()
                    parents.add(element)
                }

                // INLINE CODE
                8 -> {
                    // text without "`{}`"
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val element = Element.InlineCode(text)
                    parents.add(element)
                }

                // LINK
                9 -> {
                    // full text for regex
                    text = string.subSequence(startIndex, endIndex)
                    val (title, link) = "\\[(.*)]\\((.*)\\)".toRegex().find(text)!!.destructured
                    val element = Element.Link(link, title)
                    parents.add(element)
                }

                // ORDERED LIST
                10 -> {
                    // full text for regex
                    text = string.subSequence(startIndex, endIndex)
                    val (order, content) = "(\\d+?\\.) (.+)".toRegex().find(text)!!.destructured
                    val element = Element.OrderedListItem(order, content)
                    parents.add(element)
                }

                // MULTILINE CODE
                11 -> {
                    // text without "```{}```"
                    text = string.subSequence(startIndex.plus(3), endIndex.plus(-3))
                    val element = Element.BlockCode(text = text)
                    parents.add(element)
                }

                // IMAGE
                12 -> {
                    // full text for regex
                    text = string.subSequence(startIndex, endIndex)
                    println("hello")
                    val (alt, url) = "!\\[(.*)\\]\\((\\S*).*\\)".toRegex().find(text)!!.destructured
                    val title = if (text.matches("!\\[.*\\]\\(\\S* \".*\"\\)".toRegex()))
                        "!\\[.*\\]\\(\\S* \"(.*)\"\\)".toRegex().find(text)!!.destructured.component1()
                    else ""
                    val element = Element.Image(url, if (alt.isNotEmpty()) alt else null, title)
                    parents.add(element)
                }
            }
            lastStartIndex = endIndex
        }
        if (lastStartIndex < string.length) {
            val text = string.subSequence(lastStartIndex, string.length)
            parents.add(Element.Text(text))
        }

        return parents
    }
}

data class MarkdownText(val elements: List<Element>)

sealed class Element {
    abstract val text: CharSequence
    abstract val elements: List<Element>

    data class Text(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class UnorderedListItem(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Header(
        val level: Int = 1,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Quote(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Italic(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Bold(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Strike(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Rule(
        override val text: CharSequence = " ",
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class InlineCode(
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Link(
        val link: String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class OrderedListItem(
        val order: String,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class BlockCode(
        val type: Type = Type.MIDDLE,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element() {
        enum class Type { START, END, MIDDLE, SINGLE }
    }

    data class Image(
        val url: String,
        val alt: String?,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()
}