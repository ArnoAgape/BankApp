package com.aura.ui.login

sealed class State {
    object Idle : State()            // Pas encore tenté
    object Success : State()         // Connexion réussie
    object Loading : State()         // En cours
    sealed class Error {
        object LoginError : State()      // Erreur identification
        object Server : State()          // Erreur serveur
        object NoInternet : State()      // Pas de connexion Internet
        object UnknownError : State()    // Erreur inconnue
    }
}