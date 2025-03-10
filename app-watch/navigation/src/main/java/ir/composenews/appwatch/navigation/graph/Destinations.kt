@file:Suppress(
    "ktlint:standard:class-signature",
    "ktlint:standard:blank-line-before-declaration",
    "ktlint:standard:trailing-comma-on-call-site"
)

package ir.composenews.appwatch.navigation.graph

sealed class Destinations(val route: String) {
    data object MarketListScreen : Destinations("market_list_screen")
    data class MarketDetailScreen(val market: String = "market") :
        Destinations("market_detail_screen")
}
