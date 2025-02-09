package ir.composenews.domain.model

import kotlinx.collections.immutable.PersistentList

data class MarketChart(
    val prices: PersistentList<Pair<Long, Double>>,
)
