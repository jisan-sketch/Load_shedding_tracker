# Firebase Setup Guide for Load-shedding Tracker

This step-by-step guide walks you through connecting your Firebase project to the **Load-shedding Tracker** Android app (`com.example.loadsheddingtracker`).

> **Note on App Safety:** If you run the app before configuring Firebase, the app **will NOT crash**. The app detects missing Firebase configuration gracefully (`FirebaseHelper.isFirebaseConfigured`), displays friendly student notifications, and provides offline fallback demo data so you can present all Android architecture features immediately in a viva.

---

## Step 1: Create a Firebase Project

1. Open your web browser and navigate to the [Firebase Console](https://console.firebase.google.com/).
2. Log in with your Google account.
3. Click **"Add project"** (or **"Create a project"**).
4. Enter a project name (e.g., `LoadSheddingTracker-Lab`).
5. (Optional) Disable Google Analytics for this university lab project to simplify setup.
6. Click **"Create project"** and wait for it to complete.

---

## Step 2: Register the Android App

1. On the Firebase Project Overview page, click the **Android icon** (`</>`) to add an Android application.
2. Enter the exact Android Package Name:
   ```text
   com.example.loadsheddingtracker
   ```
3. Enter an optional App nickname (e.g., `Load-shedding Tracker`).
4. (Optional) Debug signing certificate SHA-1:
   - For basic Email/Password Auth, RTDB, and Firestore, SHA-1 is **not required**.
   - If needed, run `./gradlew signingReport` in the terminal to view your debug SHA-1.
5. Click **"Register app"**.

---

## Step 3: Download and Place `google-services.json`

1. Click **"Download google-services.json"**.
2. Copy or move the downloaded `google-services.json` file directly into the **`app/`** folder of this project:
   ```text
   E:\2_2\my_android_project\app\google-services.json
   ```
3. Once placed in the `app/` directory, Gradle automatically detects the file and applies the `com.google.gms.google-services` plugin on your next build!

---

## Step 4: Enable Email / Password Authentication

1. In the left navigation menu of the Firebase Console, click **Build** ➔ **Authentication**.
2. Click **"Get started"**.
3. Under the **"Sign-in method"** tab, click **"Email/Password"**.
4. Enable the first toggle: **"Email/Password"** (leave "Email link" disabled).
5. Click **"Save"**.
6. (Optional) Under the **"Users"** tab, click **"Add user"** to create a test user (e.g., `student@example.com` / `123456`), or use the in-app **Register** screen.

---

## Step 5: Enable Firebase Realtime Database

1. In the left navigation menu, click **Build** ➔ **Realtime Database**.
2. Click **"Create Database"**.
3. Choose a database location (e.g., `United States` or `Singapore`).
4. In the Security rules dialog, select **"Start in test mode"**:
   ```json
   {
     "rules": {
       ".read": true,
       ".write": true
     }
   }
   ```
5. Click **"Enable"**.
6. Realtime live community reports will now automatically populate under the `/reports` path.

---

## Step 6: Enable Cloud Firestore

1. In the left navigation menu, click **Build** ➔ **Firestore Database**.
2. Click **"Create database"**.
3. Choose a database location close to Bangladesh (e.g., `asia-south1 (Mumbai)` or `default`).
4. In the Security rules dialog, select **"Start in test mode"**:
   ```text
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /{document=**} {
         allow read, write: if true;
       }
     }
   }
   ```
5. Click **"Create"**.
6. Outage history records will now automatically populate under the `outage_history` collection.

---

## Step 7: Build and Run

In Android Studio:
1. Click **File** ➔ **Sync Project with Gradle Files** (or "Sync Now").
2. Connect your Android device or start an emulator.
3. Click the green **Run** button (`Shift + F10`).
4. Register a new user, view live schedules, submit community outage reports, and observe real-time data sync!

