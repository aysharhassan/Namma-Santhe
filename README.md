# Namma Santhe Ledger

**Digital Udari Management for Rural Vendors**

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.8-green.svg)](https://developer.android.com/jetpack/compose)
[![Room](https://img.shields.io/badge/Room-2.6.0-orange.svg)](https://developer.android.com/training/data-storage/room)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📱 Overview

**Namma Santhe Ledger** is an offline‑first Android application designed specifically for small‑scale rural vendors (vegetable sellers, flower vendors, snack stalls, kirana shops) to manage customer credit (*Udari*). It replaces paper notebooks with a fast, simple, and reliable digital ledger that works without internet.

The app enables vendors to:
- Add customer credits in under **5 seconds**
- Track pending dues and record payments (full/partial)
- Automatically calculate **trust scores** (Green = Trusted, Yellow = Moderate, Red = Risky)
- Send **one‑tap WhatsApp reminders** with personalised messages
- Generate **UPI QR codes** for quick payment confirmation
- Work completely **offline** – no network required for core features

---

## ✨ Key Features

| Feature | Description |
|---------|-------------|
| **Secure Login** | Email or phone + PIN authentication (local DataStore) |
| **Home Dashboard** | Live totals: pending dues, collected amount, yet to receive, today’s collection, customer counts |
| **Quick Udari Entry** | Large numeric keypad, recent customer chips, quick amount buttons |
| **Customer Ledger** | Full transaction history, trust badge, call/WhatsApp actions |
| **Payment Collection** | Cash / UPI / QR code, full or partial payment, auto balance update |
| **Smart Trust Score** | Calculated from payment delay, frequency, pending amount, repayment consistency |
| **WhatsApp Reminder** | One‑click pre‑filled message (AI‑personalised optional) |
| **QR Payment** | Display UPI QR code, scan to pay, store transaction reference |
| **Daily Summary** | Auto‑generated with WorkManager (sales, collections, active customers) |
| **Settings** | Dark mode, PIN change, backup/restore, export to Excel/PDF |
| **Offline First** | Room database stores all data locally; no internet required for daily use |

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|------------|
| **Language** | Kotlin 100% |
| **UI Toolkit** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM + Repository Pattern |
| **Local Database** | Room (SQLite) with Flow & suspend functions |
| **Preferences** | DataStore |
| **Background Tasks** | WorkManager (daily summary generation) |
| **QR Code** | ZXing Android Embedded + CameraX |
| **WhatsApp Integration** | Intent API with auto‑generated messages |
| **Generative AI (optional)** | Gemini Nano (on‑device for reminder personalisation) |
| **Dependency Injection** | Manual (Hilt ready for future) |
| **Minimum SDK** | 24 (Android 7.0) |
| **Target SDK** | 34 (Android 14) |

---

## 📂 Project Structure
app/src/main/java/com/example/nammasantheledgerapp/
├── MainActivity.kt
├── data/
│ ├── database/
│ │ ├── AppDatabase.kt
│ │ ├── Converters.kt
│ │ ├── Customer.kt
│ │ ├── Transaction.kt
│ │ ├── User.kt
│ │ └── Daos.kt
│ └── repository/
│ └── SantheRepository.kt
├── ui/
│ ├── theme/
│ │ └── Theme.kt
│ └── screens/
│ ├── LoginScreen.kt
│ ├── RegisterScreen.kt
│ ├── HomeScreen.kt
│ ├── QuickUdariScreen.kt
│ ├── CustomersScreen.kt
│ ├── CustomerLedgerScreen.kt
│ ├── PaymentCollectionScreen.kt
│ ├── SettingsScreen.kt
│ └── WhatsAppReminderDialog.kt
├── viewmodel/
│ └── MainViewModel.kt
└── utils/
└── (helper classes)

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17** or higher
- Android SDK with API level 34
- A physical device or emulator (API 24+)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/namma-santhe-ledger.git
   cd namma-santhe-ledger
   Open in Android Studio

File → Open → select the project folder

Wait for Gradle sync to complete

Build and run

Connect a device or start an emulator

Click Run ▶️ (green triangle)
🧪 Testing
Unit tests (planned): Trust score calculation logic

Instrumentation tests: UI navigation and database operations

Manual testing: Performed on emulator (API 33) and real device (Android 13)

To run tests:
./gradlew test   # unit tests
./gradlew connectedAndroidTest  # instrumentation tests

🔧 Future Enhancements
Voice‑based transaction entry (using on‑device speech recognition)

Full multi‑language support (Kannada, Hindi, Tamil)

Cloud backup (Google Drive / Firebase) with offline‑first sync

Group lending & risk sharing among vendors

Advanced AI predictions for cash flow and collection days

Wearable OS companion app for market days

🤝 Contributing
Contributions are welcome! Please follow these steps:

Fork the repository

Create a feature branch (git checkout -b feature/amazing-feature)

Commit your changes (git commit -m 'Add some amazing feature')

Push to the branch (git push origin feature/amazing-feature)

Open a Pull Request


