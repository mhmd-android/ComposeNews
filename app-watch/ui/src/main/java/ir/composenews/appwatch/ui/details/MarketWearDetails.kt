@file:Suppress("UnusedPrivateMember", "LongMethod", "ForbiddenComment")

package ir.composenews.appwatch.ui.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import coil.compose.rememberAsyncImagePainter
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import ir.composenews.base.LoadableData
import ir.composenews.base.errorViewMapper
import ir.composenews.base.isLoading
import ir.composenews.base.use
import ir.composenews.designsystem.component.QuadLineChart
import ir.composenews.designsystem.component.shimmerEffect
import ir.composenews.designsystem.theme.ComposeNewsTheme
import ir.composenews.designsystem.widget.ErrorView
import ir.composenews.marketdetail.MarketDetailContract
import ir.composenews.marketdetail.MarketDetailContract.State
import ir.composenews.marketdetail.MarketDetailViewModel
import ir.composenews.marketdetail.formatNumber
import ir.composenews.marketdetail.preview_provider.MarketDetailStateProvider
import ir.composenews.uimarket.model.MarketModel

private const val HALF_WIDTH_RATIO = 0.5f
private const val SMALL_WIDTH_RATIO = 0.2f

@Composable
fun MarketDetailWearRoute(
    market: MarketModel,
) {
    val viewModel: MarketDetailViewModel = hiltViewModel()
    val (state, event) = use(viewModel = viewModel)
    LaunchedEffect(key1 = market) {
        event.invoke(MarketDetailContract.Event.SetMarket(market = market))
        // TODO: Move into viewModel?
        event.invoke(MarketDetailContract.Event.GetMarketChart(marketId = market.id))
        event.invoke(MarketDetailContract.Event.GetMarketDetail(marketId = market.id))
    }
    MarketDetailScreen(
        marketDetailState = state,
    )
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
private fun MarketDetailScreen(
    marketDetailState: State,
) {
    val listState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Icon,
            last = ScalingLazyColumnDefaults.ItemType.Text,
        ),
    )

    when {
        marketDetailState.marketDetail.isLoading || marketDetailState.marketChart.isLoading -> {
            ShimmerMarketDetail()
        }

        marketDetailState.marketDetail is LoadableData.Error -> {
            ErrorView(errorMessage = errorViewMapper((marketDetailState.marketDetail as LoadableData.Error).error))
        }

        marketDetailState.marketChart is LoadableData.Error -> {
            ErrorView(errorMessage = errorViewMapper((marketDetailState.marketChart as LoadableData.Error).error))
        }

        listOf(
            marketDetailState.market,
            marketDetailState.marketChart,
            marketDetailState.marketDetail,
        ).none { it is LoadableData.Initial } -> {
            val market = remember { (marketDetailState.market as LoadableData.Loaded).data }

            ScalingLazyColumn(columnState = listState) {
                item {
                    Image(
                        painter = rememberAsyncImagePainter(model = market.imageUrl),
                        contentDescription = market.name,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                    )
                }
                item {
                    Text(
                        text = market.name,
                        style = MaterialTheme.typography.title1,
                    )
                }
                item {
                    Text(
                        text = "${market.currentPrice} $",
                        style = MaterialTheme.typography.body1,
                    )
                }
                item {
                    (marketDetailState.marketChart as LoadableData.Loaded).data.prices.let { prices ->
                        QuadLineChart(data = prices)
                    }
                }
                item {
                    Text(
                        text = "Market Data",
                        style = MaterialTheme.typography.title2,
                    )
                }
                item {
                    HorizontalDivider()
                }
                item {
                    MarketDetail(marketDetailState)
                }
            }
        }
    }
}

@Composable
fun MarketDetail(marketData: State) {
    val marketDetail = remember {
        (marketData.marketDetail as LoadableData.Loaded).data.marketData
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        MarketDetailDataBlock("Market Cap", formatNumber(marketDetail?.marketCapUSD))
        MarketDetailDataBlock("High 24h", marketDetail?.high24hUSD.toString())
        MarketDetailDataBlock("Low 24h", marketDetail?.low24hUSD.toString())
        MarketDetailDataBlock("Rank", "#${marketDetail?.marketCapRank}")
    }
}

@Composable
private fun MarketDetailDataBlock(title: String, value: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.title3,
        color = MaterialTheme.colors.secondary,
    )
    Text(
        text = value,
        style = MaterialTheme.typography.body2,
    )
}

@Composable
fun ShimmerMarketDetail() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        modifier = Modifier
            .fillMaxSize()
            .semantics(
                mergeDescendants = true,
                properties = { contentDescription = "Loading. PLease wait" },
            ),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .shimmerEffect(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(HALF_WIDTH_RATIO)
                .clip(RoundedCornerShape(4.dp))
                .height(20.dp)
                .shimmerEffect(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(SMALL_WIDTH_RATIO)
                .clip(RoundedCornerShape(4.dp))
                .height(12.dp)
                .shimmerEffect(),
        )
    }
}

@Preview(
    device = WearDevices.LARGE_ROUND,
    showSystemUi = true,
)
@Composable
private fun MarketDetailScreenPrev(
    @PreviewParameter(MarketDetailStateProvider::class) marketDetailState: State,
) {
    ComposeNewsTheme {
        MarketDetailScreen(marketDetailState = marketDetailState)
    }
}
