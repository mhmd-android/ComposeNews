@file:Suppress(
    "PackageNaming",
    "PackageName",
    "ktlint:standard:annotation",
    "ktlint:standard:trailing-comma-on-call-site"
)

package ir.composenews.core_test.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class PlatformDispatcherProvider @Inject constructor() : DispatcherProvider {
    override val ui: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val bg: CoroutineDispatcher = Dispatchers.Default
}
