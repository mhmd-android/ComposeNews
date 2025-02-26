package ir.composenews.appwatch.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.items
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import ir.composenews.base.LoadableComponent
import ir.composenews.base.errorViewMapper
import ir.composenews.base.isLoading
import ir.composenews.base.use
import ir.composenews.designsystem.component.pull_refresh_indicator.pullRefresh
import ir.composenews.designsystem.component.pull_refresh_indicator.rememberPullRefreshState
import ir.composenews.designsystem.widget.ErrorView
import ir.composenews.extensions.roundToTwoDecimalPlaces
import ir.composenews.marketlist.MarketListContract
import ir.composenews.marketlist.MarketListViewModel
import ir.composenews.uimarket.model.MarketModel

@Composable
fun MarketListWearRoute(
    showFavoriteList: Boolean = false,
    onNavigateToDetailScreen: (market: MarketModel) -> Unit,
) {
    val viewModel: MarketListViewModel = hiltViewModel()
    val (state, event) = use(viewModel = viewModel)
    LaunchedEffect(key1 = Unit) {
        event.invoke(MarketListContract.Event.OnSetShowFavoriteList(showFavoriteList = showFavoriteList))
        if (!showFavoriteList) {
            event.invoke(MarketListContract.Event.OnGetMarketList)
        }
    }
    MarketListWearScreen(
        state = state,
        onNavigateToDetailScreen = onNavigateToDetailScreen,
        onRefresh = {
            event.invoke(MarketListContract.Event.OnGetMarketList)
        },
    )
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
private fun MarketListWearScreen(
    state: MarketListContract.State,
    onNavigateToDetailScreen: (market: MarketModel) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val refreshState = rememberPullRefreshState(
        refreshing = state.marketList.isLoading,
        onRefresh = onRefresh,
    )

    val listState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Card,
            last = ScalingLazyColumnDefaults.ItemType.Card,
        ),
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .pullRefresh(refreshState),
    ) {
        LoadableComponent(
            loadableData = state.marketList,
            loading = {
                ShimmerMarketListItem()
            },
            error = { error ->
                ErrorView(errorMessage = errorViewMapper(error))
            },
            loaded = { data ->
                AnimatedVisibility(
                    visible = !state.marketList.isLoading,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    ScalingLazyColumn(
                        columnState = listState,
                    ) {
                        items(
                            items = data,
                            key = { it.name },
                        ) { market ->
                            MarketItem(
                                modifier = modifier,
                                name = market.name,
                                symbol = market.symbol,
                                urlToImage = market.imageUrl,
                                price = market.currentPrice.toString(),
                                priceChangePercentage24h =
                                market.priceChangePercentage24h.roundToTwoDecimalPlaces(),
                                onItemClick = { onNavigateToDetailScreen(market) },
                            )
                        }
                    }
                }
            },
        )
    }
}
