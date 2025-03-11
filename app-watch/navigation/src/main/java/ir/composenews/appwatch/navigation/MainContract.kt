@file:Suppress(
    "ktlint:standard:class-signature",
    "ktlint:standard:no-empty-first-line-in-class-body",
    "ktlint:standard:trailing-comma-on-call-site"
)

package ir.composenews.appwatch.navigation

import ir.composenews.base.UnidirectionalViewModel
import ir.composenews.uimarket.model.MarketModel

interface MainContract :
    UnidirectionalViewModel<MainContract.Event, MainContract.State> {

    data class State(
        val market: MarketModel? = null,
    )

    sealed class Event {
        data class SetMarket(val market: MarketModel?) : Event()
    }
}
