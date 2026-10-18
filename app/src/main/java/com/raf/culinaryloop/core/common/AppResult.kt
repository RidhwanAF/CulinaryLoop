package com.raf.culinaryloop.core.common

/**
 * Domain error representations for typed functional outcomes.
 */
sealed interface DomainError {
    /**
     * Connectivity or network transport failures.
     */
    data class Network(val message: String) : DomainError

    /**
     * Server-side errors (HTTP 5xx or remote ESB service errors).
     */
    data class Server(val code: Int, val message: String) : DomainError

    /**
     * Entity not found (HTTP 404 or missing local store/product).
     */
    data class NotFound(val entity: String, val id: String) : DomainError

    /**
     * Input or business validation failure.
     */
    data class Validation(val reason: String) : DomainError

    /**
     * Unclassified domain or system error.
     */
    data class Unknown(val throwable: Throwable? = null) : DomainError
}

/**
 * Typed outcome representing either [Success] or [Failure].
 *
 * @param T The successful payload type.
 * @param E The domain error hierarchy type.
 */
sealed interface AppResult<out T, out E : DomainError> {
    /**
     * Successful outcome containing [data].
     */
    data class Success<out T>(val data: T) : AppResult<T, Nothing>

    /**
     * Erroneous outcome containing [error].
     */
    data class Failure<out E : DomainError>(val error: E) : AppResult<Nothing, E>

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    fun getOrNull(): T? = (this as? Success)?.data

    fun errorOrNull(): E? = (this as? Failure)?.error
}

inline fun <T, E : DomainError, R> AppResult<T, E>.map(transform: (T) -> R): AppResult<R, E> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Failure -> this
}

inline fun <T, E : DomainError, R : DomainError> AppResult<T, E>.mapError(transform: (E) -> R): AppResult<T, R> = when (this) {
    is AppResult.Success -> this
    is AppResult.Failure -> AppResult.Failure(transform(error))
}

inline fun <T, E : DomainError> AppResult<T, E>.onSuccess(action: (T) -> Unit): AppResult<T, E> {
    if (this is AppResult.Success) action(data)
    return this
}

inline fun <T, E : DomainError> AppResult<T, E>.onFailure(action: (E) -> Unit): AppResult<T, E> {
    if (this is AppResult.Failure) action(error)
    return this
}
