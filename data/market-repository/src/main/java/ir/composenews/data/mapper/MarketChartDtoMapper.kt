package ir.composenews.data.mapper

import ir.composenews.domain.model.Chart
import ir.composenews.remotedatasource.dto.MarketChartResponse
import kotlinx.collections.immutable.toPersistentList

fun MarketChartResponse.toChart(): Chart = Chart(
    prices = prices.map { element -> Pair(element[0].toLong(), element[1]) }.toPersistentList(),
)
