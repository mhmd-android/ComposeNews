@file:Suppress(
    "MagicNumber",
    "TooGenericExceptionCaught",
    "MaxLineLength",
    "ktlint",
)

package ir.composenews.data.repository

import android.util.Log
import ir.composenews.data.mapper.toMarket
import ir.composenews.data.mapper.toMarketChart
import ir.composenews.data.mapper.toMarketDetail
import ir.composenews.data.mapper.toMarketEntity
import ir.composenews.domain.model.Market
import ir.composenews.domain.model.MarketChart
import ir.composenews.domain.model.MarketDetail
import ir.composenews.domain.repository.MarketRepository
import ir.composenews.localdatasource.database.MarketDao
import ir.composenews.network.Errors
import ir.composenews.network.Resource
import ir.composenews.network.mapMessageStatusCode
import ir.composenews.network.onError
import ir.composenews.network.onException
import ir.composenews.network.statusCode
import ir.composenews.network.suspendMap
import ir.composenews.network.suspendOnError
import ir.composenews.network.suspendOnException
import ir.composenews.network.suspendOnSuccess
import ir.composenews.remotedatasource.api.MarketsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MarketRepositoryImpl @Inject constructor(
    private val api: MarketsApi,
    private val dao: MarketDao,
) : MarketRepository {

    override fun getMarketList(): Flow<List<Market>> =
        dao.getMarketList().map { list -> list.map { it.toMarket() } }

    override fun getFavoriteMarketList(): Flow<List<Market>> =
        dao.getFavoriteMarketList().map { list -> list.map { it.toMarket() } }

    override suspend fun syncMarketList() {
        api.getMarkets(
            "usd",
            "market_cap_desc",
            20,
            1,
            false,
        ).suspendOnSuccess {
            val marketEntityList = data.map { marketResponse ->
                marketResponse.toMarketEntity()
            }
            marketEntityList.forEach { marketEntity ->
                dao.insertMarket(marketEntity = marketEntity)
            }
        }.onError {
            Log.d("debug", message)
        }.onException {
            Log.d("debug", message.toString())
        }
    }

    override suspend fun toggleFavoriteMarket(oldMarket: Market) {
        val marketEntity = oldMarket.copy(isFavorite = !oldMarket.isFavorite).toMarketEntity()
        dao.insertMarket(marketEntity)
    }

    override fun fetchChart(id: String): Flow<Resource<MarketChart, Errors>> = flow {
        val chart = api.getMarketChart(id, "usd", 1)
        chart.suspendOnSuccess {
            suspendMap {
                emit(Resource.Success(it.data.toMarketChart()))
            }
        }.suspendOnError {
            suspendMap {
                emit(
                    Resource.Error(
                        error = Errors.ApiError(
                            it.statusCode.mapMessageStatusCode(),
                            it.statusCode.code,
                        ),
                    ),
                )
            }
        }.suspendOnException {
            suspendMap { emit(Resource.Error(Errors.ExceptionError(it.message, throwable))) }
        }
    }

    override fun fetchDetail(id: String): Flow<Resource<MarketDetail, Errors>> = flow {
        val detail = api.getMarketDetail(id)
        detail.suspendOnSuccess {
            suspendMap {
                emit(Resource.Success(data.toMarketDetail()))
            }
        }.suspendOnError {
            suspendMap {
                emit(
                    Resource.Error(
                        Errors.ApiError(it.statusCode.mapMessageStatusCode(), it.statusCode.code),
                    ),
                )
            }
        }.suspendOnException {
            suspendMap { emit(Resource.Error(Errors.ExceptionError(it.message, it.throwable))) }
        }
    }
}
