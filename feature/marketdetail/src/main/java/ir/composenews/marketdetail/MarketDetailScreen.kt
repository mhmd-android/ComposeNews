@file:Suppress("UnusedPrivateMember", "LongMethod", "ForbiddenComment")

package ir.composenews.marketdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import ir.composenews.base.LoadableData
import ir.composenews.base.errorViewMapper
import ir.composenews.base.isLoading
import ir.composenews.base.use
import ir.composenews.designsystem.component.FavoriteIcon
import ir.composenews.designsystem.component.QuadLineChart
import ir.composenews.designsystem.preview.ThemePreviews
import ir.composenews.designsystem.theme.ComposeNewsTheme
import ir.composenews.designsystem.widget.ErrorView
import ir.composenews.marketdetail.MarketDetailContract.State
import ir.composenews.marketdetail.preview_provider.MarketDetailStateProvider
import ir.composenews.uimarket.model.MarketModel

@Composable
fun MarketDetailRoute(
    market: MarketModel,
    viewModel: MarketDetailViewModel = hiltViewModel(),
) {
    val (state, event) = use(viewModel = viewModel)

    LaunchedEffect(key1 = market) {
        event.invoke(MarketDetailContract.Event.SetMarket(market = market))
        // TODO: Move into viewModel?
        event.invoke(MarketDetailContract.Event.GetMarketChart(marketId = market.id))
        event.invoke(MarketDetailContract.Event.GetMarketDetail(marketId = market.id))
    }

    MarketDetailScreen(
        marketDetailState = state,
        onFavoriteClick = {
            event.invoke(MarketDetailContract.Event.OnFavoriteClick(market = it))
        },
    )
}

@Composable
private fun MarketDetailScreen(
    marketDetailState: State,
    onFavoriteClick: (market: MarketModel) -> Unit,
) {
    when {
        marketDetailState.marketDetail.isLoading || marketDetailState.marketChart.isLoading -> {
            MarketDetailLoadingView()
        }

        marketDetailState.marketDetail is LoadableData.Error -> {
            ErrorView(errorMessage = errorViewMapper(marketDetailState.marketDetail.error))
        }

        marketDetailState.marketChart is LoadableData.Error -> {
            ErrorView(errorMessage = errorViewMapper(marketDetailState.marketChart.error))
        }

        listOf(
            marketDetailState.market,
            marketDetailState.marketChart,
            marketDetailState.marketDetail,
        ).none { it is LoadableData.Initial } -> {
            val market = remember { (marketDetailState.market as LoadableData.Loaded).data }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = market.imageUrl),
                                contentDescription = market.name,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape),
                            )
                            Column(
                                modifier = Modifier.weight(1F),
                            ) {
                                Text(
                                    text = market.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                )
                                Text(
                                    text = "${market.currentPrice} $",
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                    }

                    (marketDetailState.marketChart as LoadableData.Loaded).data.prices.let { prices ->
                        QuadLineChart(
                            data = prices,
                        )
                    }
                    MarketData()
                    MarketDetail(marketDetailState)
                    Spacer(modifier = Modifier.height(80.dp))
                }

                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    onClick = {},
                ) {
                    FavoriteIcon(isFavorite = market.isFavorite) {
                        onFavoriteClick(market)
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketDetail(marketDetailState: State) {
    val marketDetail = remember {
        (marketDetailState.marketDetail as LoadableData.Loaded).data
    }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        MarketCap(marketCapUSD = marketDetail.marketData?.marketCapUSD ?: 0L)
        High24(high24hUSD = marketDetail.marketData?.high24hUSD ?: 0.0)
        Low24(low24hUSD = marketDetail.marketData?.low24hUSD ?: 0.0)
        Rank(marketCapRank = marketDetail.marketCapRank)
    }
}

@Composable
private fun Rank(marketCapRank: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = "Rank",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "#$marketCapRank",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun Low24(low24hUSD: Double) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = "Low 24h",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = low24hUSD.toString(),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun High24(high24hUSD: Double) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = "High 24h",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = high24hUSD.toString(),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun MarketCap(marketCapUSD: Long) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = "Market Cap",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = formatNumber(marketCapUSD),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun MarketData() {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Market Data",
            style = MaterialTheme.typography.titleLarge,
        )
    }
    HorizontalDivider(color = Color.Gray)
}

// TODO: Move from UI Layer to domain

fun formatNumber(number: Long?): String {
    val format = number?.div(BILLION)
    if (format != null) {
        return if (format >= 1) {
            "$${format}B"
        } else {
            "$${format}M"
        }
    }
    return ""
}

@ThemePreviews
@Composable
private fun MarketDetailScreenPrev(
    @PreviewParameter(MarketDetailStateProvider::class) marketDetailState: State,
) {
    ComposeNewsTheme {
        Surface {
            MarketDetailScreen(marketDetailState = marketDetailState, onFavoriteClick = {})
        }
    }
}

private const val BILLION: Long = 1000000000L
