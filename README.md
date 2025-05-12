<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a name="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->


<!-- PROJECT LOGO -->
<br />
<div align="center">
    <img src="aura.png" alt="Logo" width="200" height="200">

<h3 align="center">AURA ANDROID</h3>

  <p align="center">
    The Android app AURA
  </p>
</div>

### Built With

* ![android]
* ![kotlin]
* ![androidstudio]
* ![gradle]

<!-- GETTING STARTED -->
## Getting Started

Aura is a simple banking application built with **Kotlin**, following the **MVVM architecture pattern**, and designed with clean architecture principles.

The app allows users to:
- Log in using an ID and password
- View their account balance(s)
- Transfer money to other users

---

## 🏗️ Project Structure

├── ui/
│   ├── login/             # LoginActivity, LoginViewModel
│   ├── home/              # HomeActivity, HomeViewModel
│   └── transfer/          # TransferActivity, TransferViewModel
│
├── data/
│   └── network/
│       ├── AuraClient     # Retrofit API interface
│       └── repository/    # AuraRepository
│
├── domain/
│   └── model/             # LoginModel, UserModel, TransferModel
│
├── states/                # State.kt, Error classes (e.g., NoConnectionException)
│
└── tests/                 # Unit tests using Turbine and fake repositories

---

## 🔧 Tech Stack

- Kotlin
- MVVM + Clean Architecture
- Jetpack ViewModel
- StateFlow + Channel
- Retrofit + Moshi
- Hilt (DI)
- OkHttp Logging Interceptor
- Coroutines / Flow
- Turbine for Flow testing
- JUnit for unit testing

---

## 🚀 Features

### ✅ Login
- Validates non-empty fields
- Shows success or failure with a toast
- Handles no internet and server errors

### 💰 Home
- Displays account list
- Shows main account balance
- Allows retry on failure
- Navigates to transfer screen

### 🔁 Transfer
- Sends amount to another user
- Handles insufficient funds
- Validates non-zero amount
- Handles unknown user and server errors

---

## 🧪 Testing

Aura includes unit tests for all ViewModels and the repository.

### Tools:
- JUnit
- Kotlin Coroutines Test
- Turbine for Flow testing

### Test files:
- AuraRepositoryTest.kt
- HomeViewModelTest.kt
- LoginViewModelTest.kt
- TransferViewModelTest.kt

Run tests using:

./gradlew testDebugUnitTest

HTML test report:
app/build/reports/tests/testDebugUnitTest/index.html

---

## 🛠️ How to Run

1. Clone the repository:
   git clone https://github.com/your-username/aura.git

2. Launch the backend server (from OpenClassrooms or local)
3. Update the base URL in NetworkModule.kt (e.g., use 10.0.2.2 for emulator)

4. Run the app in Android Studio or via command line:
   ./gradlew installDebug

---

## 📦 Future Improvements

- UI tests (Espresso or Compose Testing)
- Better error messaging
- Authentication token handling
- Persistence with Room

---

## 👤 Author

**Arno**  
Training in Android Development  
Passionate about cycling, minimalism & clean code.

---

## 📄 License

MIT License – free to use, modify, and distribute.