package com.aura.ui.login

sealed class LoginState {
    object Idle : LoginState()            // Pas encore tenté
    object Success : LoginState()         // Connexion réussie
    object Error : LoginState()           // Connexion échouée
    object NoInternet : LoginState()      // Pas de connexion Internet
    object Loading : LoginState()         // En cours
}