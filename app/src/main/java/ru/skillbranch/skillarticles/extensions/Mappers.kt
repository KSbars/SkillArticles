package ru.skillbranch.skillarticles.extensions

import ru.skillbranch.skillarticles.data.AppSettings
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.viewmodels.ArticleState
import ru.skillbranch.skillarticles.viewmodels.BottombarData
import ru.skillbranch.skillarticles.viewmodels.SubmenuData

fun ArticleState.toAppSettings(): AppSettings {
    return AppSettings(isDarkMode, isBigText)
}

fun ArticleState.toArticlePersonalInfo(): ArticlePersonalInfo {
    return ArticlePersonalInfo(isLike, isBookmark)
}

fun ArticleState.asMap(): Map<String, Any?> = mapOf(
    "isAuth" to isAuth,
    "isLoadingContent" to isLoadingContent,
    "isLoadingReviews" to isLoadingReviews,
    "isLike" to isLike,
    "isBookmark" to isBookmark,
    "isShowMenu" to isShowMenu,
    "isBigText" to isBigText,
    "isDarkMode" to isDarkMode,
    "isSearch" to isSearch,
    "searchQuery" to searchQuery,
    "searchResults" to searchResults,
    "searchPosition" to searchPosition,
    "shareLink" to shareLink,
    "title" to title,
    "category" to category,
    "categoryIcon" to categoryIcon,
    "date" to date,
    "author" to author,
    "poster" to poster,
    "content" to content,
    "reviews" to reviews,
)

fun ArticleState.fromMap(map: Map<String, Any?>): ArticleState = copy(
    isAuth = map["isAuth"] as Boolean,
    isLoadingContent = map["isLoadingContent"] as Boolean,
    isLoadingReviews = map["isLoadingReviews"] as Boolean,
    isLike = map["isLike"] as Boolean,
    isBookmark = map["isBookmark"] as Boolean,
    isShowMenu = map["isShowMenu"] as Boolean,
    isBigText = map["isBigText"] as Boolean,
    isDarkMode = map["isDarkMode"] as Boolean,
    isSearch = map["isSearch"] as Boolean,
    searchQuery = map["searchQuery"] as String,
    searchResults = map["searchResults"] as List<Pair<Int, Int>>,
    searchPosition = map["searchPosition"] as Int,
    shareLink = map["shareLink"] as String,
    title = map["title"] as String,
    category = map["category"] as String,
    categoryIcon = map["categoryIcon"] as Any,
    date = map["date"] as String,
    author = map["author"] as Any,
    poster = map["poster"] as String,
    content = map["content"] as String,
    reviews = map["reviews"] as List<Any>,
)