package ir.composenews.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarketDetail(
    val id: String,
    val marketCapRank: Int,
    val marketData: MarketData?,
    val name: String,
) : Parcelable {
    @Parcelize
    data class MarketData(
        val high24hUSD: Double,
        val low24hUSD: Double,
        val marketCapUSD: Long,
        val marketCapRank: Int,
    ) : Parcelable
}
