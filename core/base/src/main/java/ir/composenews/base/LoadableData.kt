package ir.composenews.base

sealed class LoadableData<out T> {
    data object Initial : LoadableData<Nothing>()

    data object Loading : LoadableData<Nothing>()

    data class Loaded<T>(val data: T) : LoadableData<T>()

    data class Error<E>(val error: E) : LoadableData<Nothing>()
}

val LoadableData<*>.isLoading: Boolean
    get() = this is LoadableData.Loading
