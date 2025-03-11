@file:Suppress(
    "MaxLineLength",
    "ktlint:standard:class-signature",
)

package ir.composenews.localdatasource.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import ir.composenews.core_test.SuspendSpec
import ir.composenews.db.MarketDatabase
import ir.composenews.db.MarketEntity
import kotlinx.coroutines.flow.first

class MarketDaoTest : SuspendSpec({
    lateinit var marketDao: MarketDao

    beforeSpec {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        MarketDatabase.Schema.create(driver)
        val db = MarketDatabase(driver)
        marketDao = MarketDaoImpl(db)
    }

    "Given database that insert market data, When GetMarketList is called, Then returns list of market entities" {
        val marketEntities = MarketEntity("1", "Bitcoin", "BTC", 50000.0, 5.0, "url", FALSE)

        marketDao.insertMarket(marketEntities)

        marketDao.getMarketList().first().shouldContainExactly(marketEntities)
    }

    "Given database that insert favorite market data, When GetFavoriteMarketList is called, Then returns list of favorite market entities" {
        val favoriteMarkets = MarketEntity("1", "Bitcoin", "BTC", 50000.0, 5.0, "url", TRUE)

        marketDao.insertMarket(favoriteMarkets)

        marketDao.getFavoriteMarketList().first().shouldContainExactly(favoriteMarkets)
    }

    "Given a list of market entities, When updating the value of Market, Then inserts or updates entities in the database" {
        val marketEntity = MarketEntity("1", "Bitcoin", "BTC", 50000.0, 5.0, "url", FALSE)
        marketDao.insertMarket(marketEntity = marketEntity)

        marketDao.getMarketList().first().shouldContainExactly(marketEntity)

        val updatedMarket =
            marketEntity.copy(currentPrice = 50001.0, priceChangePercentage24h = 5.000001)
        marketDao.insertMarket(marketEntity = updatedMarket)

        marketDao.getMarketList().first().size shouldBe 1
        marketDao.getMarketList().first().shouldContainExactly(updatedMarket)
    }
})
