@file:Suppress("TooGenericExceptionThrown", "MaxLineLength", "ktlint")

package ir.composenews.marketlist

import app.cash.turbine.test
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import ir.composenews.base.LoadableData
import ir.composenews.core_test.MainCoroutineListener
import ir.composenews.core_test.dispatcher.TestDispatcherProvider
import ir.composenews.domain.model.Market
import ir.composenews.domain.use_case.GetFavoriteMarketListUseCase
import ir.composenews.domain.use_case.GetMarketListUseCase
import ir.composenews.domain.use_case.ToggleFavoriteMarketListUseCase
import ir.composenews.network.Errors
import ir.composenews.uimarket.mapper.toMarket
import ir.composenews.uimarket.mapper.toMarketModel
import ir.composenews.uimarket.model.MarketModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineScheduler
import java.util.UUID
import kotlin.random.Random

class MarketListViewModelTest : StringSpec({

    val getMarketListUseCase: GetMarketListUseCase = mockk(relaxed = true)
    val getFavoriteMarketListUseCase: GetFavoriteMarketListUseCase = mockk(relaxed = true)
    val toggleFavoriteMarketListUseCase: ToggleFavoriteMarketListUseCase = mockk(relaxed = true)
    val testScheduler = TestCoroutineScheduler()
    val dispatcherProvider = TestDispatcherProvider(testScheduler)
    lateinit var viewModel: MarketListViewModel

    listeners(MainCoroutineListener())

    beforeEach {
        viewModel = MarketListViewModel(
            getMarketListUseCase,
            getFavoriteMarketListUseCase,
            toggleFavoriteMarketListUseCase,
            dispatcherProvider,
        )
    }

    "Given initial state, When ViewModel is created, Then state should be default" {
        val initialState = viewModel.state.value

        initialState.marketList.shouldBeInstanceOf<LoadableData.Initial>()
        initialState.showFavoriteList shouldBe false
    }

    "Given ViewModel state, When show favorite list is toggled, Then state updates correctly" {
        viewModel.event(MarketListContract.Event.OnSetShowFavoriteList(showFavoriteList = true))

        viewModel.state.value.showFavoriteList shouldBe true
    }

    "Given market list use case, When market list is successfully fetched, Then loaded state contains market data" {
        val marketList = provideMarketList(2)
        coEvery { getMarketListUseCase() } returns flowOf(marketList)

        viewModel.state.test {
            val initState = awaitItem().marketList
            initState.shouldBeInstanceOf<LoadableData.Initial>()

            viewModel.event(MarketListContract.Event.OnGetMarketList)

            val loadingState = awaitItem().marketList
            loadingState.shouldBeInstanceOf<LoadableData.Loading>()

            val loadedState = awaitItem().marketList
            loadedState shouldBe LoadableData.Loaded(data = marketList.map { it.toMarketModel() })
        }
    }

    "Given favorite list mode is enabled, When favorite market list is fetched, Then loaded state contains favorite markets" {
        val favoriteMarketList = provideMarketList(1, isFavorite = true)
        coEvery { getFavoriteMarketListUseCase() } returns flowOf(favoriteMarketList)

        viewModel.event(MarketListContract.Event.OnSetShowFavoriteList(showFavoriteList = true))
        viewModel.event(MarketListContract.Event.OnGetMarketList)

        val loadedState = viewModel.state.value.marketList
        loadedState.shouldBeInstanceOf<LoadableData.Loaded<PersistentList<MarketModel>>>()
        loadedState.data.size shouldBe 1
        loadedState.data.first().isFavorite shouldBe true
    }

    "Given a market, When favorite click event is triggered, Then toggle favorite use case is called" {
        val marketModel = provideMarketList(size = 1).first().toMarketModel()
        coEvery { toggleFavoriteMarketListUseCase(any()) } returns Unit

        viewModel.event(MarketListContract.Event.OnFavoriteClick(market = marketModel))

        coVerify { toggleFavoriteMarketListUseCase(marketModel.toMarket()) }
    }

    "Given market list use case, When market list fetch fails, Then error state is set" {
        val exception = RuntimeException("Network Error")
        coEvery { getMarketListUseCase() } returns flow { throw exception }

        viewModel.event(MarketListContract.Event.OnGetMarketList)

        val errorState = viewModel.state.value.marketList
        errorState.shouldBeInstanceOf<LoadableData.Error>()
        (errorState.error as Errors.ExceptionError).message shouldBe exception.message
    }
})

private fun provideMarketList(size: Int, isFavorite: Boolean = false): List<Market> =
    (0 until size).map {
        Market(
            id = UUID.randomUUID().toString(),
            name = "Ethereum$it",
            symbol = "XRP",
            currentPrice = Random.nextDouble(300.0, 2300.0),
            priceChangePercentage24h = Random.nextDouble(300.0, 2300.0),
            isFavorite = isFavorite,
            imageUrl = "google.com",
        )
    }
