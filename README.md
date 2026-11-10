# Load-shedding Tracker (Bangladesh)
### Course: CSE 2200 – Advanced Programming (Android Lab Project)
### Language: Java (No Kotlin, Plain findViewById, Pure Native AndroidX & Material Components)

---

## 1. Project Overview

**Load-shedding Tracker** is a comprehensive, native Android application built specifically for university viva and laboratory demonstration. It allows residents of Bangladesh (default area: **Khulna**) to view electricity outage schedules, receive advance notifications prior to power cuts, log and view community-reported outages in real time, inspect historical outages, and fetch live weather conditions.

Every single lecture and laboratory topic from **Lecture 8 (Android Basics)**, **Lab Manual (ListView & RecyclerView)**, **Lecture 10 (Android Components)**, and the **Firebase & Networking Document** is fully implemented with clear, self-explanatory code and comments.

---

## 2. Project Folder Structure Explained

```text
E:\2_2\my_android_project\
├── app\
│   ├── src\
│   │   └── main\
│   │       ├── AndroidManifest.xml          # Declares all 14 Activities, Service, Receiver, Provider, and permissions
│   │       ├── java\com\example\loadsheddingtracker\
│   │       │   ├── activities\              # UI Activities (Splash, Login, Register, Dashboard, etc.)
│   │       │   ├── adapters\                # ScheduleAdapter (ListView), OutageRecyclerAdapter (RecyclerView)
│   │       │   ├── models\                  # POJOs for SQLite, RTDB (/reports), and Firestore (outage_history)
│   │       │   ├── providers\               # ScheduleContentProvider, Contract, and SQLiteOpenHelper
│   │       │   ├── receivers\               # PowerConnectionReceiver & dynamic NetworkChangeReceiver
│   │       │   ├── services\                # OutageCountdownService (Started & Foreground Service)
│   │       │   └── utils\                   # WeatherHttpUtil, NotificationHelper, FirebaseHelper
│   │       └── res\
│   │           ├── drawable\                # Vector icons (lightning, power cut, notification) and card shapes
│   │           ├── layout\                  # RelativeLayout, ConstraintLayout, LinearLayout, TableLayout, FrameLayout
│   │           ├── values\                  # colors.xml (amber/charcoal), strings.xml, themes.xml
│   │           └── xml\                     # backup_rules.xml, data_extraction_rules.xml
│   ├── build.gradle.kts                     # App-level dependencies (RecyclerView, Material, Firebase BoM)
│   └── google-services-PLACEHOLDER.txt      # Safety placeholder note for google-services.json
├── gradle\
│   └── libs.versions.toml                   # Version catalog
├── build.gradle.kts                         # Root project build script with google-services plugin declaration
├── gradle.properties                        # Gradle JVM args and redirection to high-capacity storage
├── DATABASE_COMPARISON.md                   # Realtime Database vs Firestore viva comparison matrix
├── FIREBASE_SETUP.md                        # Step-by-step instructions to enable Firebase Auth, RTDB & Firestore
└── README.md                                # Complete project documentation & viva topic map
```

### Folder Roles for Viva Explanation:
* **`activities/`**: Houses the Activity classes controlling user interaction, Activity lifecycle callbacks, explicit/implicit intents, and runtime permission flows.
* **`adapters/`**: Implements custom data binding logic for both `ListView` (using the `convertView` reuse pattern) and `RecyclerView` (using `RecyclerView.ViewHolder`).
* **`models/`**: Simple Data Models with public no-argument constructors and getters/setters required for SQLite, Firebase RTDB deserialization, and Cloud Firestore document mapping.
* **`providers/`**: Encapsulates data persistence behind Android's standard `ContentProvider` API, backed by `SQLiteOpenHelper`.
* **`receivers/`**: Handles asynchronous system and application events (power connected/disconnected, battery low, network state change, service ticks).
* **`services/`**: Runs background tasks independent of the UI lifecycle; demonstrates a Foreground Service with an ongoing notification.
* **`utils/`**: Helper utilities for background HTTP requests (`HttpURLConnection`), Android 8.0+ `NotificationChannel` management, and safe Firebase checking.
* **`res/layout/`**: Contains XML layouts demonstrating 5 distinct layout ViewGroup types.
* **`res/values/`**: Centralizes design tokens (colors, string resources, themes) adhering to Android best practices.

---

## 3. Comprehensive Viva Topic Coverage Table (All 25 Items)

| # | Lecture / Lab Topic | Feature Description in App | Screen / Component | Source Code Files |
| :-: | :--- | :--- | :--- | :--- |
| **1** | **Project Folder Structure** | Clean package layout: activities, adapters, models, services, receivers, providers, utils | All modules | `java/com/example/loadsheddingtracker/*` |
| **2** | **Button Click Handling (3 Ways)** | (a) `android:onClick` in XML (`onRegisterClick`), (b) Java lambda (`setOnClickListener`), (c) Anonymous inner class (`new View.OnClickListener()`) | Login & Dashboard | `activity_login.xml`, `LoginActivity.java`, `DashboardActivity.java` |
| **3** | **RelativeLayout** | Dashboard layout constructed using `layout_below`, `alignParentEnd`, `centerHorizontal`, `layout_toEndOf` | Power Dashboard | `activity_dashboard.xml`, `DashboardActivity.java` |
| **4** | **ConstraintLayout** | Form layouts with vertical guidelines, percentage guidelines, and chain constraints | Login & Register | `activity_login.xml`, `activity_register.xml` |
| **5** | **UI Layout Diversity** | Demonstrated in screens: FrameLayout (Splash), LinearLayout with weight (Select Area), TableLayout (Weekly Schedule Matrix), GridLayout (Duration presets) | Splash, Schedule, Log Outage | `activity_splash.xml`, `activity_schedule.xml`, `activity_log_outage.xml` |
| **6** | **Activity Lifecycle** | Overrides `onCreate`, `onStart`, `onResume`, `onPause`, `onStop`, `onRestart`, `onDestroy` with Log.d, on-screen text tracker card, toasts, and `onSaveInstanceState` | Dashboard | `DashboardActivity.java` |
| **7** | **Explicit & Implicit Intents** | Explicit intents pass data with `putExtra()`; Implicit intents: `ACTION_DIAL` (16999 helpline), `ACTION_VIEW` (WZPDCL website), `ACTION_SEND` (share outage) | Dashboard & Outage Detail | `DashboardActivity.java`, `OutageDetailActivity.java` |
| **8** | **Simple ListView + ArrayAdapter** | Displays String[] of Bangladesh districts using `android.R.layout.simple_list_item_1` with `setOnItemClickListener` and `ActivityResultLauncher` | Select Area | `SelectAreaActivity.java`, `activity_select_area.xml` |
| **9** | **Custom ListView + Adapter** | `ScheduleAdapter extends ArrayAdapter` with `convertView` recycling pattern and static `ViewHolder` helper class | Today's Schedule | `ScheduleActivity.java`, `ScheduleAdapter.java`, `item_schedule.xml` |
| **10** | **RecyclerView with ViewHolder** | `OutageRecyclerAdapter extends RecyclerView.Adapter<OutageViewHolder>` with `onCreateViewHolder`, `onBindViewHolder`, and explicit Intent click handler | Outage History | `OutageHistoryActivity.java`, `OutageRecyclerAdapter.java`, `item_outage.xml` |
| **11** | **LayoutManager Variety** | Runtime button toggle switching between `LinearLayoutManager` (list) and `GridLayoutManager(2)` (grid) | Outage History | `OutageHistoryActivity.java` |
| **12** | **ListView vs RecyclerView** | Complete theoretical and practical comparison table documented below | README & Viva Map | `README.md`, `AboutActivity.java` |
| **13** | **Started & Foreground Service** | `OutageCountdownService` counts down to next outage; runs as Foreground Service with persistent notification; Start/Stop buttons | Dashboard & Service | `OutageCountdownService.java`, `DashboardActivity.java` |
| **14** | **Notifications & Channels** | NotificationChannel (`loadshedding_channel_id`), foreground persistent notification, 15-min warning notification with `PendingIntent.FLAG_IMMUTABLE`, power restored alert | Notifications | `NotificationHelper.java` |
| **15** | **BroadcastReceivers** | (a) `PowerConnectionReceiver` (manifest-declared for `POWER_CONNECTED`, `POWER_DISCONNECTED`, `BATTERY_LOW`), (b) Dynamic `NetworkChangeReceiver`, (c) Custom countdown broadcast | Receivers | `PowerConnectionReceiver.java`, `NetworkChangeReceiver.java`, `OutageCountdownService.java` |
| **16** | **Runtime Permissions** | Modern `ActivityResultContracts.RequestMultiplePermissions` for `POST_NOTIFICATIONS`, `READ_CONTACTS`, `ACCESS_FINE_LOCATION`, `CALL_PHONE` with rationale dialog | Permissions Screen | `PermissionsActivity.java`, `activity_permissions.xml` |
| **17** | **Content Providers (Custom & System)** | (a) Custom `ScheduleContentProvider` backed by `SQLiteOpenHelper` with `UriMatcher` & `ContentResolver`; (b) System provider `ContactsContract` | Schedule & Contact Picker | `ScheduleContentProvider.java`, `ScheduleDbHelper.java`, `ContactPickerActivity.java` |
| **18** | **HTTP Networking & Threading** | Fetches live weather for Bangladesh areas from Open-Meteo REST API using `HttpURLConnection` on background thread (`ExecutorService`) + `JSONObject` parsing | Dashboard Weather | `WeatherHttpUtil.java`, `DashboardActivity.java` |
| **19** | **Firebase Integration & Safety** | Gradle setup with Google Services; `FIREBASE_SETUP.md` documentation; `FirebaseHelper` prevents crashes when `google-services.json` is missing | Firebase Setup | `FIREBASE_SETUP.md`, `FirebaseHelper.java`, `app/build.gradle.kts` |
| **20** | **Firebase Authentication** | Email/Password Registration, Login, Logout, "Forgot password" (`sendPasswordResetEmail`), auto-login check, progress bar, input validation | Auth Screens | `LoginActivity.java`, `RegisterActivity.java` |
| **21** | **Firebase RTDB – Save Data** | Report outage form saves live citizen incident under `/reports/{pushId}` via `setValue()` and `push()` | Report Outage | `ReportOutageActivity.java`, `CommunityReportModel.java` |
| **22** | **Firebase RTDB – Retrieve Data** | `LiveReportsActivity` reads `/reports` via `addValueEventListener`, displays in RecyclerView, and supports deleting reports | Live Reports | `LiveReportsActivity.java`, `CommunityReportsAdapter.java` |
| **23** | **Cloud Firestore – Add Data** | Form logs structured outage history records into Firestore collection `outage_history` using `collection().add()` | Log Outage | `LogOutageActivity.java`, `OutageHistoryModel.java` |
| **24** | **Cloud Firestore – Read Data** | Queries `outage_history` with `whereEqualTo("area", area)` and `orderBy("timestamp")`; implements both `.get()` and `.addSnapshotListener()`, plus document update/delete | Outage History & Detail | `OutageHistoryActivity.java`, `OutageDetailActivity.java` |
| **25** | **RTDB vs Firestore Comparison** | Dedicated architectural guide and comparison matrix comparing JSON trees vs Collections, query power, offline support, and pricing | Documentation | `DATABASE_COMPARISON.md` |

---

## 4. Lab Manual Topic: ListView vs RecyclerView Comparison

| Feature | ListView | RecyclerView |
| :--- | :--- | :--- |
| **ViewHolder Pattern** | Optional. Developers can choose to write `findViewById()` inside `getView()`, which harms scrolling performance unless manually implemented using `convertView.setTag()`. | **Mandatory**. The `RecyclerView.Adapter` enforces extending `RecyclerView.ViewHolder`, guaranteeing optimized view recycling. |
| **Layout Orientation & Flexibility** | Only supports standard vertical scrolling lists. Horizontal or grid arrangements require third-party libraries or completely different widgets (`GridView`). | Highly modular. Uses a pluggable `LayoutManager` (`LinearLayoutManager`, `GridLayoutManager`, `StaggeredGridLayoutManager`) that can be switched dynamically at runtime. |
| **Item Animations** | No built-in item animation support. Animating additions or deletions requires complex custom code. | Built-in support for default animations via `ItemAnimator` (fade, slide, reorder) on `notifyItemInserted()`, `notifyItemRemoved()`, etc. |
| **Item Decoration** | Basic divider support via `android:divider` and `android:dividerHeight`. | Rich `ItemDecoration` architecture allowing custom decorators, spacing, and dividers. |
| **Performance** | High memory consumption and stuttering if `convertView` is not recycled properly. | Highly optimized memory footprint; separates view recycling from layout positioning. |
| **Implementation in App** | Demonstrated in `SelectAreaActivity.java` (Simple) & `ScheduleActivity.java` (Custom `ScheduleAdapter`). | Demonstrated in `OutageHistoryActivity.java` (with runtime Linear/Grid toggle) & `LiveReportsActivity.java`. |

---

## 5. How to Build and Run the App

1. **Prerequisites:**
   - Android Studio (Hedgehog, Iguana, Jellyfish, Koala, Ladybug, Meerkat or newer).
   - Android SDK Platform 34 or higher installed.
   - Java 17 or Java 21 JDK.

2. **Open in Android Studio:**
   - Open Android Studio ➔ **Open** ➔ Select `E:\2_2\my_android_project`.
   - Android Studio will automatically sync the Gradle configuration.

3. **Running the App:**
   - Connect a physical Android phone with USB Debugging enabled, or launch an Android Virtual Device (AVD).
   - Click the green **Run** button (`Shift + F10`).
   - The app launches into `SplashActivity`, waits 2 seconds, and opens `LoginActivity`.
   - You can immediately test all 25 features!

4. **Connecting Your Firebase Project:**
   - Follow the detailed steps in [`FIREBASE_SETUP.md`](FIREBASE_SETUP.md) to add your own `google-services.json`.

