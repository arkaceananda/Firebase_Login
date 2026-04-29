package com.example.firebaselogin.utils

sealed class ExceptionHelper : Exception() {
    class AuthError : ExceptionHelper()
    class NetworkError : ExceptionHelper()
    class UnknownError : ExceptionHelper()
    data class Error(val errorMessage: String) : ExceptionHelper() {
        override val message: String get() = errorMessage
    }
}

sealed class UiState {
    object IsLoading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}
