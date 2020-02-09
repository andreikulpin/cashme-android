package co.cashme.cashme.domain.model

import java.lang.Exception

sealed class Result<out R> {
    class Success<out T>(val value: T) : Result<T>()
    class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}