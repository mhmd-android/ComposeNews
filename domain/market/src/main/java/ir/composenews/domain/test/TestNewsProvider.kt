@file:Suppress(
    "ktlint:standard:multiline-expression-wrapping",
)

package ir.composenews.domain.test

import ir.composenews.domain.model.Market

val notFavoriteMarket = Market(
    id = "id",
    name = "name",
    symbol = "symbol",
    currentPrice = 100000.0,
    priceChangePercentage24h = 100000.0,
    imageUrl = "some_shit_url.png",
    isFavorite = false,
)

val favoriteMarket = Market(
    id = "id",
    name = "name",
    symbol = "symbol",
    currentPrice = 100000.0,
    priceChangePercentage24h = 100000.0,
    imageUrl = "some_shit_url.png",
    isFavorite = true,
)
