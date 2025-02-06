package ir.composenews.localdatasource.test

import ir.composenews.db.MarketEntity
import ir.composenews.localdatasource.database.TRUE

val favoriteMarketEntity = MarketEntity(
    id = "id",
    name = "name",
    symbol = "symbol",
    currentPrice = 100000.0,
    priceChangePercentage24h = 100000.0,
    imageUrl = "some_shit_url.png",
    isFavorite = TRUE,
)
