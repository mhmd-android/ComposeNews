@file:Suppress("PackageNaming", "PackageName")

package ir.composenews.domain.model_provider

import ir.composenews.domain.model.Market

val marketList = List(10) { index ->
    market().copy(
        name = "name $index",
    )
}

private fun market() = Market(
    id = "id",
    name = "name 1",
    symbol = "symbol",
    currentPrice = 100000.0,
    priceChangePercentage24h = 100000.0,
    imageUrl = "some_shit_url.png",
)
