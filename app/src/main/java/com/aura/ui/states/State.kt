package com.aura.ui.states

sealed class State {
    object Idle : State()            // Pas encore tenté
    object Success : State()         // Connexion réussie
    object Loading : State()         // En cours
    sealed class Error : State() {
        object LoginError : Error()                 // Erreur identification
        object UnknownId : Error()                  // Erreur d'id de destinataire
        object SameUserId : Error()                 // Ne peut pas envoyer de l'argent à soi-même
        object InsufficientBalance : Error()        // Solde insuffisant
        object Server : Error()                     // Erreur serveur
        object NoInternet : Error()                 // Pas de connexion Internet
        object UnknownError : Error()               // Erreur inconnue
    }
}