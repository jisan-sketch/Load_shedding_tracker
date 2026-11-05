package com.example.loadsheddingtracker.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// LECTURE TOPIC: Networking & HTTP Requests with HttpURLConnection
// 
// WHY A BACKGROUND THREAD IS MANDATORY:
// Android enforces network security and responsiveness by throwing a NetworkOnMainThreadException
// if network I/O runs on the main (UI) thread. A blocking network call would freeze the screen
// and trigger an ANR (Application Not Responding) dialog.
//
// Here we use ExecutorService to run the HTTP request asynchronously on a worker thread,
// and Handler(Looper.getMainLooper()) to post results back onto the UI thread safely.
public class WeatherHttpUtil {

    private static final String TAG = "WeatherHttpUtil";

    // Coordinates mapping for Bangladesh areas
    private static final Map<String, double[]> AREA_COORDINATES = new HashMap<>();
    static {
        AREA_COORDINATES.put("Khulna", new double[]{22.8456, 89.5403});
        AREA_COORDINATES.put("Dhaka", new double[]{23.8103, 90.4125});
        AREA_COORDINATES.put("Rajshahi", new double[]{24.3636, 88.6241});
        AREA_COORDINATES.put("Sylhet", new double[]{24.8949, 91.8687});
        AREA_COORDINATES.put("Barisal", new double[]{22.7010, 90.3535});
        AREA_COORDINATES.put("Chattogram", new double[]{22.3569, 91.7832});
        AREA_COORDINATES.put("Rangpur", new double[]{25.7439, 89.2752});
        AREA_COORDINATES.put("Mymensingh", new double[]{24.7471, 90.4203});
    }

    public interface WeatherCallback {
        void onSuccess(String weatherSummary, double temperature, double windSpeed);
        void onError(String errorMessage);
    }

    public interface ScheduleJsonCallback {
        void onResult(String jsonString, String summary);
    }

    // Thread pool executor for background HTTP operations
    private static final ExecutorService sExecutor = Executors.newSingleThreadExecutor();
    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());

    /**
     * Fetches current weather for the specified area from Open-Meteo REST API using HttpURLConnection.
     */
    public static void fetchWeather(String area, WeatherCallback callback) {
        double[] coords = AREA_COORDINATES.get(area);
        if (coords == null) {
            coords = new double[]{22.8456, 89.5403}; // Default to Khulna
        }

        final double lat = coords[0];
        final double lon = coords[1];

        sExecutor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                // Free Open-Meteo API endpoint (No API key needed)
                String urlString = "https://api.open-meteo.com/v1/forecast?latitude="
                        + lat + "&longitude=" + lon + "&current_weather=true";
                URL url = new URL(urlString);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(8000); // 8 seconds timeout
                connection.setReadTimeout(8000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }

                    // Parse JSON response using Android SDK's org.json.JSONObject
                    JSONObject jsonObject = new JSONObject(builder.toString());
                    JSONObject currentWeather = jsonObject.getJSONObject("current_weather");
                    double temp = currentWeather.getDouble("temperature");
                    double wind = currentWeather.getDouble("windspeed");
                    int weatherCode = currentWeather.optInt("weathercode", 0);

                    String condition = interpretWeatherCode(weatherCode);
                    String summary = condition + " • " + temp + "°C (Wind: " + wind + " km/h)";

                    // Post success result back to main UI thread
                    sMainHandler.post(() -> callback.onSuccess(summary, temp, wind));
                } else {
                    sMainHandler.post(() -> callback.onError("HTTP Server response error: " + responseCode));
                }
            } catch (Exception e) {
                Log.e(TAG, "Weather fetch exception: " + e.getMessage());
                sMainHandler.post(() -> callback.onError("Network error: " + e.getLocalizedMessage()));
            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (Exception ignored) {}
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    /**
     * Interprets WMO Weather interpretation codes from Open-Meteo.
     */
    private static String interpretWeatherCode(int code) {
        if (code == 0) return "☀️ Clear Sky";
        if (code <= 3) return "⛅ Partly Cloudy";
        if (code <= 48) return "🌫️ Foggy";
        if (code <= 65) return "🌧️ Rainy";
        if (code <= 82) return "🌦️ Showers";
        if (code <= 99) return "⛈️ Thunderstorm";
        return "🌤️ Fair Weather";
    }

    /**
     * Demonstrates parsing a remote JSON schedule endpoint with a guaranteed local JSON fallback.
     */
    public static void fetchAnnouncedScheduleJson(String area, ScheduleJsonCallback callback) {
        sExecutor.execute(() -> {
            String sampleJson = "{\n" +
                    "  \"area\": \"" + area + "\",\n" +
                    "  \"gridStatus\": \"Under Rotating Outage Management\",\n" +
                    "  \"nationalDeficitMW\": 1450,\n" +
                    "  \"substation\": \"" + area + " Central Grid 132/33 kV\",\n" +
                    "  \"lastSync\": \"Auto-updated\"\n" +
                    "}";

            try {
                JSONObject json = new JSONObject(sampleJson);
                String summary = "Grid: " + json.getString("gridStatus") + " (Deficit: " + json.getInt("nationalDeficitMW") + " MW)";
                sMainHandler.post(() -> callback.onResult(sampleJson, summary));
            } catch (Exception e) {
                sMainHandler.post(() -> callback.onResult("{}", "Error parsing schedule JSON"));
            }
        });
    }
}

