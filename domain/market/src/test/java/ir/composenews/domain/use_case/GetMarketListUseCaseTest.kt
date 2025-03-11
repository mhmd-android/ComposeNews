@file:Suppress(
    "PackageNaming",
    "PackageName",
    "ktlint:standard:class-signature",
)

package ir.composenews.domain.use_case

import io.kotest.core.spec.style.StringSpec
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import ir.composenews.domain.repository.MarketRepository

class GetMarketListUseCaseTest : StringSpec({

    lateinit var repository: MarketRepository
    lateinit var useCase: GetMarketListUseCase

    beforeTest {
        repository = mockk(relaxed = true)
        useCase = GetMarketListUseCase(repository = repository)
    }

    "Given repository syncs market data, When invoked, Then syncMarketList is called" {
        coEvery { repository.syncMarketList() } just Runs

        useCase.invoke()

        coVerify { repository.syncMarketList() }
    }
})
