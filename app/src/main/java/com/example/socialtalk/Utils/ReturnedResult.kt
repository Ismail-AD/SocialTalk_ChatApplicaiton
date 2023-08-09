package com.example.socialtalk.Utils


sealed class ReturnedResult<out T> {
    class Success<out R>(val data: R) : ReturnedResult<R>()
    class Failure(val message: String) : ReturnedResult<Nothing>()
    object Loading : ReturnedResult<Nothing>()
}

