package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(substr: String, ignoreCase: Boolean = true) : List<Int> {
    if (substr.isEmpty() || this == null) return emptyList()
    val result = mutableListOf<Int>()
    var currentIndex = 0
    while (currentIndex < length) {
        currentIndex = indexOf(substr, startIndex = currentIndex, ignoreCase)
        if (currentIndex == -1) break
        result.add(currentIndex)
        currentIndex += substr.length
    }
    return result
}