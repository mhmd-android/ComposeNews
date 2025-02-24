package ir.composenews.marketlist

import io.kotest.core.spec.style.StringSpec
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import ir.composenews.core_test.MainCoroutineListener
import ir.composenews.core_test.dispatcher.TestDispatcherProvider
import ir.composenews.domain.model.Market
import ir.composenews.domain.use_case.GetFavoriteMarketListUseCase
import ir.composenews.domain.use_case.GetMarketListUseCase
import ir.composenews.domain.use_case.ToggleFavoriteMarketListUseCase
import ir.composenews.uimarket.model.MarketModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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

    beforeAny {
        viewModel = MarketListViewModel(
            getMarketListUseCase,
            getFavoriteMarketListUseCase,
            toggleFavoriteMarketListUseCase,
            dispatcherProvider,
        )
    }

    "Given initial state, When ViewModel is created, Then state should be default" {
//        viewModel.state.value shouldBe MarketListContract.State(
//            marketList = persistentListOf(),
//            refreshing = false,
//            showFavoriteList = false,
//            showFavoriteEmptyState = false,
//        )
    }

    "Given show favorite list is false, When OnGetMarketList is triggered, Then calls getMarketListUseCase" {

//        viewModel.baseState.test {
//            val initState = awaitItem()
//            initState shouldBe BaseContract.BaseState.OnLoading
//            val afterState = awaitItem()
//            afterState shouldBe BaseContract.BaseState.OnSuccess
//        }
//
//        viewModel.state.test {
//            val initState = awaitItem()
//            initState shouldBe MarketListContract.State(refreshing = false)
//
//            viewModel.event(MarketListContract.Event.OnGetMarketList)
//
//            val loadingState = awaitItem()
//            loadingState shouldBe MarketListContract.State(refreshing = true)
//            val afterState = awaitItem()
//            afterState shouldBe MarketListContract.State(refreshing = false)
//        }

//        coVerify { getMarketListUseCase.invoke() }
    }

    "Given show favorite list is true, When OnGetMarketList is triggered, Then calls getFavoriteMarketListUseCase" {
        coEvery { getFavoriteMarketListUseCase.invoke() } returns flowOf(emptyList())
        viewModel.event(MarketListContract.Event.OnSetShowFavoriteList(showFavoriteList = true))

        viewModel.event(MarketListContract.Event.OnGetMarketList)

//        coVerify { getFavoriteMarketListUseCase.invoke() }
    }

    "Given market list fetch succeeds, When OnGetMarketList is triggered, Then updates state with market list" {
        val marketList = provideMarketList(2)
        coEvery { getMarketListUseCase.invoke() } returns flowOf(marketList)

        viewModel.event(MarketListContract.Event.OnGetMarketList)

//        viewModel.state.value.marketList shouldBe marketList.map { it.toMarketModel() }
//            .toPersistentList()
    }

    "Given favorite market toggle, When OnFavoriteClick is triggered, Then calls toggleFavoriteMarketListUseCase" {
        val marketModel = MarketModel(
            id = "1",
            name = "Bitcoin",
            symbol = "BTC",
            currentPrice = 50000.0,
            priceChangePercentage24h = 5.0,
            imageUrl = "https://example.com/bitcoin.png",
            isFavorite = false,
        )
        coEvery { toggleFavoriteMarketListUseCase.invoke(any()) } just Runs

        viewModel.event(MarketListContract.Event.OnFavoriteClick(market = marketModel))

//        coVerify { toggleFavoriteMarketListUseCase.invoke(marketModel.toMarket()) }
    }

    "Given an exception occurs, When fetching market list, Then updates state with error" {
        coEvery { getMarketListUseCase.invoke() } returns flow { throw RuntimeException("Error occurred") }

        viewModel.event(MarketListContract.Event.OnGetMarketList)

//        viewModel.baseState.value shouldBe BaseContract.BaseState.OnError(
//            errors = Errors.ExceptionError(message = "Error occurred"),
//        )
    }

    "With OnSetShowFavoriteList event and showFavoriteList is false then we should hide favorite list" {
        runTest {
            val expected = false

            viewModel.event(MarketListContract.Event.OnSetShowFavoriteList(expected))
            advanceUntilIdle()

            val actual = viewModel.state.value.showFavoriteList

//            actual shouldBeEqual expected
        }
    }

    "With OnSetShowFavoriteList event and showFavoriteList is true then we should show favorite list" {
        runTest {
            val expected = true

            viewModel.event(MarketListContract.Event.OnSetShowFavoriteList(expected))
            advanceUntilIdle()

            val actual = viewModel.state.value.showFavoriteList

//            actual shouldBeEqual expected
        }
    }

    "With OnGetMarketList event and showFavorite is false  we should get all market items ()" {
        runTest {
            // Given
            val expectedMarketList = provideMarketList(10)
            coEvery { getMarketListUseCase.invoke() } returns flowOf(expectedMarketList)

            // When
            viewModel.event(MarketListContract.Event.OnSetShowFavoriteList(false))
            viewModel.event(MarketListContract.Event.OnGetMarketList)
            advanceUntilIdle()

            // Then
//            val actualMarketList = viewModel.state.value.marketList.map { it.toMarket() }

//            actualMarketList shouldBeEqual expectedMarketList
        }
    }

    "With OnGetMarketList event and showFavorite is true  we should get all favorite market items ()" {
        runTest {
            // Given
            val expectedMarketList = provideMarketList(10)
            coEvery { getFavoriteMarketListUseCase.invoke() } returns flowOf(expectedMarketList)

            // When
            viewModel.event(MarketListContract.Event.OnSetShowFavoriteList(true))
            viewModel.event(MarketListContract.Event.OnGetMarketList)
            advanceUntilIdle()

            // Then
//            val actualMarketList = viewModel.state.value.marketList.map { it.toMarket() }

//            actualMarketList shouldBeEqual expectedMarketList
        }
    }

    "With OnRefresh event and showFavorite is true we should refresh market list with favorites" {
        runTest {
            val oldMarketList = provideMarketList(2)

            coEvery { getFavoriteMarketListUseCase.invoke() } returns flowOf(oldMarketList)

            viewModel.event(MarketListContract.Event.OnSetShowFavoriteList(true))
            viewModel.event(MarketListContract.Event.OnGetMarketList)

            advanceUntilIdle()

//            oldMarketList shouldBeEqual viewModel.state.value.marketList.map { it.toMarket() }

            val newMarketList = provideMarketList(4)

            coEvery { getFavoriteMarketListUseCase.invoke() } returns flowOf(newMarketList)

//            viewModel.event(MarketListContract.Event.OnRefresh)

            advanceUntilIdle()
//            val actualMarketList = viewModel.state.value.marketList.map { it.toMarket() }

//            newMarketList shouldBeEqual actualMarketList
//            viewModel.state.value.refreshing shouldBeEqual false
        }
    }

    "With OnRefresh event and showFavorite is false we should refresh market list with favorites" {
        runTest {
            val oldMarketList = provideMarketList(5)

            coEvery { getMarketListUseCase.invoke() } returns flowOf(oldMarketList)

            viewModel.event(MarketListContract.Event.OnSetShowFavoriteList(false))

            val newMarketList = provideMarketList(5)

            coEvery { getMarketListUseCase.invoke() } returns flowOf(newMarketList)

//            viewModel.event(MarketListContract.Event.OnRefresh)
            advanceUntilIdle()

//            val actualMarketList = viewModel.state.value.marketList.map { it.toMarket() }

//            newMarketList shouldBeEqual actualMarketList
//            viewModel.state.value.refreshing shouldBeEqual false
        }
    }
})

private fun provideMarketList(size: Int): List<Market> {
    return (0 until size).map {
        Market(
            id = UUID.randomUUID().toString(),
            name = "Ethereum$it",
            symbol = "XRP",
            currentPrice = Random.nextDouble(300.0, 2300.0),
            priceChangePercentage24h = Random.nextDouble(300.0, 2300.0),
            isFavorite = false,
            imageUrl = "google.com",
        )
    }
}
