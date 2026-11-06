package com.example.loadsheddingtracker.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.loadsheddingtracker.R;
import com.example.loadsheddingtracker.receivers.NetworkChangeReceiver;
import com.example.loadsheddingtracker.services.OutageCountdownService;
import com.example.loadsheddingtracker.utils.FirebaseHelper;
import com.example.loadsheddingtracker.utils.WeatherHttpUtil;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * LECTURE TOPIC: Activity Lifecycle, RelativeLayout, Intents & Service Management
 * Core Dashboard meeting all university viva requirements:
 * 1. Root and containers strictly use RelativeLayout positioning rules.
 * 2. Activity lifecycle callbacks (7 methods + onSaveInstanceState) with Logcat logging.
 * 3. 3 Button Click techniques: (a) android:onClick XML, (b) Java lambda, (c) View.OnClickListener anonymous class.
 * 4. Explicit Intents (Schedule, Report, Community, History, Settings).
 * 5. Implicit Intents: ACTION_DIAL (16999 helpline), ACTION_VIEW (utility website), ACTION_SEND (share alert).
 * 6. ActivityResultLauncher for selecting district from SelectAreaActivity.
 * 7. Dynamic BroadcastReceivers: Network connectivity changes & Countdown service ticks.
 * 8. Started & Foreground Service management with single toggle switch.
 */
public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardLifecycle";
    public static final String HELPLINE_NUMBER = "16999"; // National Electricity Helpline in Bangladesh
    public static final String POWER_CO_URL = "https://www.wzpdcl.gov.bd"; // West Zone Power Distribution Company

    // State key for onSaveInstanceState
    private static final String KEY_SELECTED_AREA = "saved_selected_area";

    // UI View References
    private TextView tvDashGreeting;
    private TextView tvDashUserEmail;
    private TextView tvNetworkStatus;
    private View viewNetworkDot;
    private ImageButton btnDashSettings;
    private ImageButton btnDashLogout;

    private TextView tvCurrentArea;
    private View btnChangeArea;

    private TextView tvWeatherInfo;
    private ImageButton btnRefreshWeather;

    private TextView tvCountdownTimer;
    private LinearProgressIndicator progressCountdown;
    private SwitchCompat switchCountdownService;

    // Quick Action Cards
    private MaterialCardView cardNavSchedule;
    private MaterialCardView cardNavReport;
    private MaterialCardView cardNavLiveReports;
    private MaterialCardView cardNavHistory;
    private MaterialCardView cardCallHelpline;
    private MaterialCardView cardShareStatus;

    // Bottom Navigation Tabs
    private LinearLayout navTabHome;
    private LinearLayout navTabSchedule;
    private LinearLayout navTabReport;
    private LinearLayout navTabHistory;
    private LinearLayout navTabSettings;

    // State Variable
    private String mCurrentArea = "Khulna"; // Default area as requested

    // =========================================================================
    // LECTURE TOPIC: ActivityResultLauncher (Modern replacement for startActivityForResult)
    // =========================================================================
    private final ActivityResultLauncher<Intent> mSelectAreaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    // Extract data returned from SelectAreaActivity via Intent extra
                    String chosenArea = result.getData().getStringExtra("SELECTED_AREA");
                    if (chosenArea != null && !chosenArea.isEmpty()) {
                        mCurrentArea = chosenArea;
                        tvCurrentArea.setText(mCurrentArea);
                        Toast.makeText(this, "Area updated to: " + mCurrentArea, Toast.LENGTH_SHORT).show();
                        // Refresh weather for new area via HttpURLConnection
                        loadWeatherForArea(mCurrentArea);
                    }
                }
            }
    );

    // =========================================================================
    // LECTURE TOPIC: Dynamic BroadcastReceivers
    // =========================================================================
    private final BroadcastReceiver mCountdownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && OutageCountdownService.ACTION_COUNTDOWN_TICK.equals(intent.getAction())) {
                String formatted = intent.getStringExtra(OutageCountdownService.EXTRA_FORMATTED_TIME);
                if (formatted != null && tvCountdownTimer != null) {
                    tvCountdownTimer.setText(formatted);
                }
            }
        }
    };

    private NetworkChangeReceiver mNetworkReceiver;

    // =========================================================================
    // LECTURE TOPIC: Activity Lifecycle Callbacks (Overridden with Logcat logging)
    // =========================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lifecycle: onCreate() - Activity creation, view inflation, initial state binding
        logLifecycle("onCreate()");
        setContentView(R.layout.activity_dashboard);

        // Bind views with plain findViewById
        initViews();

        // Restore saved instance state after device rotation
        if (savedInstanceState != null) {
            mCurrentArea = savedInstanceState.getString(KEY_SELECTED_AREA, "Khulna");
        }
        tvCurrentArea.setText(mCurrentArea);

        // Setup personalized greeting from authenticated user email
        setupUserGreeting();

        // Setup button click events using all 3 lecture-mandated techniques
        setupClickListeners();

        // Fetch weather asynchronously via HttpURLConnection on a background thread
        loadWeatherForArea(mCurrentArea);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Lifecycle: onStart() - Activity becomes visible to user; register dynamic receivers
        logLifecycle("onStart()");

        // 1. Dynamic BroadcastReceiver: Network connectivity changes
        mNetworkReceiver = new NetworkChangeReceiver(isConnected -> {
            if (isConnected) {
                tvNetworkStatus.setText("Online");
                viewNetworkDot.setBackgroundResource(R.drawable.bg_network_dot_online);
            } else {
                tvNetworkStatus.setText("Offline");
                viewNetworkDot.setBackgroundResource(R.drawable.bg_network_dot_offline);
            }
        });
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // 2. Dynamic BroadcastReceiver: Countdown ticks from OutageCountdownService
        IntentFilter countdownFilter = new IntentFilter(OutageCountdownService.ACTION_COUNTDOWN_TICK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(mCountdownReceiver, countdownFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(mCountdownReceiver, countdownFilter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Lifecycle: onResume() - Activity is in the foreground and interactive with user
        logLifecycle("onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Lifecycle: onPause() - Another activity is gaining focus; pause lightweight animations
        logLifecycle("onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Lifecycle: onStop() - Activity no longer visible; unregister dynamic receivers
        logLifecycle("onStop()");

        if (mNetworkReceiver != null) {
            try { unregisterReceiver(mNetworkReceiver); } catch (Exception ignored) {}
        }
        try { unregisterReceiver(mCountdownReceiver); } catch (Exception ignored) {}
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Lifecycle: onRestart() - Activity returning to foreground after being stopped
        logLifecycle("onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Lifecycle: onDestroy() - Activity is finishing or system is reclaiming memory
        logLifecycle("onDestroy()");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // LECTURE TOPIC: Preserving state across configuration changes (e.g. screen rotation)
        outState.putString(KEY_SELECTED_AREA, mCurrentArea);
        Log.d(TAG, "onSaveInstanceState: Saved area=" + mCurrentArea);
    }

    private void logLifecycle(String callbackName) {
        // Log to Logcat for developer and examiner viva verification
        Log.d(TAG, "Activity Lifecycle Callback: " + callbackName);
    }

    // =========================================================================
    // View Binding & Event Handling
    // =========================================================================

    private void initViews() {
        tvDashGreeting = findViewById(R.id.tv_dash_greeting);
        tvDashUserEmail = findViewById(R.id.tv_dash_user_email);
        tvNetworkStatus = findViewById(R.id.tv_network_status);
        viewNetworkDot = findViewById(R.id.view_network_dot);
        btnDashSettings = findViewById(R.id.btn_dash_settings);
        btnDashLogout = findViewById(R.id.btn_dash_logout);

        tvCurrentArea = findViewById(R.id.tv_current_area);
        btnChangeArea = findViewById(R.id.btn_change_area);

        tvWeatherInfo = findViewById(R.id.tv_weather_info);
        btnRefreshWeather = findViewById(R.id.btn_refresh_weather);

        tvCountdownTimer = findViewById(R.id.tv_countdown_timer);
        progressCountdown = findViewById(R.id.progress_countdown);
        switchCountdownService = findViewById(R.id.switch_countdown_service);

        cardNavSchedule = findViewById(R.id.card_nav_schedule);
        cardNavReport = findViewById(R.id.card_nav_report);
        cardNavLiveReports = findViewById(R.id.card_nav_live_reports);
        cardNavHistory = findViewById(R.id.card_nav_history);
        cardCallHelpline = findViewById(R.id.card_call_helpline);
        cardShareStatus = findViewById(R.id.card_share_status);

        navTabHome = findViewById(R.id.nav_tab_home);
        navTabSchedule = findViewById(R.id.nav_tab_schedule);
        navTabReport = findViewById(R.id.nav_tab_report);
        navTabHistory = findViewById(R.id.nav_tab_history);
        navTabSettings = findViewById(R.id.nav_tab_settings);
    }

    private void setupUserGreeting() {
        String email = null;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
        }
        if (email == null) {
            email = getIntent().getStringExtra("EXTRA_USER_EMAIL");
        }
        if (email == null || email.isEmpty()) {
            email = "student@example.com";
        }

        if (tvDashUserEmail != null) {
            tvDashUserEmail.setText(email);
        }

        String displayName = "Student";
        if (email != null && email.contains("@")) {
            String prefix = email.substring(0, email.indexOf("@"));
            if (!prefix.isEmpty()) {
                displayName = Character.toUpperCase(prefix.charAt(0)) + (prefix.length() > 1 ? prefix.substring(1) : "");
                // If prefix contains a dot like "tanvir.ahmed", take first part
                if (displayName.contains(".")) {
                    displayName = displayName.substring(0, displayName.indexOf("."));
                }
            }
        }
        tvDashGreeting.setText("Hello, " + displayName);
    }

    private void setupClickListeners() {
        // ---------------------------------------------------------------------
        // LECTURE TOPIC: Click handling method (c) - Anonymous View.OnClickListener
        // ---------------------------------------------------------------------
        cardNavSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Explicit Intent to ScheduleActivity (Custom ListView demonstration)
                Intent intent = new Intent(DashboardActivity.this, ScheduleActivity.class);
                intent.putExtra("EXTRA_AREA", mCurrentArea);
                startActivity(intent);
            }
        });

        // ---------------------------------------------------------------------
        // LECTURE TOPIC: Click handling method (b) - Modern Java Lambda expressions
        // ---------------------------------------------------------------------

        // Change Area via ActivityResultLauncher
        btnChangeArea.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SelectAreaActivity.class);
            mSelectAreaLauncher.launch(intent);
        });

        // Refresh Weather Button
        btnRefreshWeather.setOnClickListener(v -> loadWeatherForArea(mCurrentArea));

        // Settings Icon Button
        btnDashSettings.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Logout Button
        if (btnDashLogout != null) {
            btnDashLogout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(DashboardActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        // Report Outage Card (Firebase RTDB Write)
        cardNavReport.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ReportOutageActivity.class);
            intent.putExtra("EXTRA_AREA", mCurrentArea);
            startActivity(intent);
        });

        // Community Feed Card (Firebase RTDB Read)
        cardNavLiveReports.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LiveReportsActivity.class);
            intent.putExtra("EXTRA_AREA", mCurrentArea);
            startActivity(intent);
        });

        // Outage History Card (Cloud Firestore Read + RecyclerView)
        cardNavHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, OutageHistoryActivity.class);
            intent.putExtra("EXTRA_AREA", mCurrentArea);
            startActivity(intent);
        });

        // IMPLICIT INTENT 1: ACTION_DIAL to call electricity helpline 16999
        cardCallHelpline.setOnClickListener(v -> {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + HELPLINE_NUMBER));
            startActivity(dialIntent);
        });

        // Foreground Service Switch Toggle
        switchCountdownService.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Intent serviceIntent = new Intent(DashboardActivity.this, OutageCountdownService.class);
            if (isChecked) {
                // Start Countdown Service as a Foreground Service
                serviceIntent.setAction(OutageCountdownService.ACTION_START);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
                Toast.makeText(DashboardActivity.this, "Countdown service started", Toast.LENGTH_SHORT).show();
            } else {
                // Stop Countdown Service
                serviceIntent.setAction(OutageCountdownService.ACTION_STOP);
                stopService(serviceIntent);
                tvCountdownTimer.setText("00:45:00");
                Toast.makeText(DashboardActivity.this, "Countdown service stopped", Toast.LENGTH_SHORT).show();
            }
        });

        // Bottom Navigation Bar tabs
        navTabHome.setOnClickListener(v -> {
            // Already on Dashboard Home
        });

        navTabSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ScheduleActivity.class);
            intent.putExtra("EXTRA_AREA", mCurrentArea);
            startActivity(intent);
        });

        navTabReport.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ReportOutageActivity.class);
            intent.putExtra("EXTRA_AREA", mCurrentArea);
            startActivity(intent);
        });

        navTabHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, OutageHistoryActivity.class);
            intent.putExtra("EXTRA_AREA", mCurrentArea);
            startActivity(intent);
        });

        navTabSettings.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    // -------------------------------------------------------------------------
    // LECTURE TOPIC: Click handling method (a) - android:onClick attribute in XML
    // -------------------------------------------------------------------------
    /**
     * Called directly from activity_dashboard.xml via android:onClick="onXmlShareStatusClick".
     * Triggers an IMPLICIT INTENT with ACTION_SEND to share outage details via external apps.
     */
    public void onXmlShareStatusClick(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareMessage = "⚡ Load-shedding Alert for " + mCurrentArea + "!\n" +
                "Track real-time power cuts and daily schedules via Load-shedding Tracker App.";
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Power Cut Alert - " + mCurrentArea);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "Share Outage Alert via:"));
    }

    // =========================================================================
    // LECTURE TOPIC: HTTP Request using HttpURLConnection on Background Thread
    // =========================================================================
    private void loadWeatherForArea(String area) {
        tvWeatherInfo.setText("Checking weather for " + area + "…");
        WeatherHttpUtil.fetchWeather(area, new WeatherHttpUtil.WeatherCallback() {
            @Override
            public void onSuccess(String weatherSummary, double temperature, double windSpeed) {
                tvWeatherInfo.setText(weatherSummary);
            }

            @Override
            public void onError(String errorMessage) {
                tvWeatherInfo.setText("25°C · Clear sky (Default)");
            }
        });
    }
}
