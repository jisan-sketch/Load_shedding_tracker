package com.example.loadsheddingtracker.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.loadsheddingtracker.R;

import java.util.ArrayList;
import java.util.List;

// LECTURE TOPIC: System Content Provider (ContactsContract) & Inter-app Sharing
// 
// EXPLANATION FOR VIVA:
// 1. Android provides system Content Providers to safely share data between apps.
// 2. Here, getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, ...)
//    queries the Android Contacts database using the READ_CONTACTS permission.
// 3. Tapping a contact formats an SMS/WhatsApp load-shedding warning via an ACTION_SENDTO or ACTION_SEND Intent.
public class ContactPickerActivity extends AppCompatActivity {

    private static final String TAG = "ContactPicker";

    private ListView lvContacts;
    private ProgressBar pbContacts;
    private TextView tvEmpty;

    private final List<String> mContactDisplayList = new ArrayList<>();
    private final List<String> mPhoneNumbers = new ArrayList<>();
    private String mArea = "Khulna";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker);

        if (getIntent().hasExtra("EXTRA_AREA")) {
            mArea = getIntent().getStringExtra("EXTRA_AREA");
        }

        lvContacts = findViewById(R.id.lv_contacts);
        pbContacts = findViewById(R.id.pb_contacts);
        tvEmpty = findViewById(R.id.tv_empty_contacts);

        android.widget.ImageButton btnBack = findViewById(R.id.btn_contacts_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Check if READ_CONTACTS permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            querySystemContacts();
        } else {
            pbContacts.setVisibility(View.GONE);
            // Show fallback dummy contacts with a helpful notice
            loadFallbackContacts();
            Toast.makeText(this, "READ_CONTACTS not granted. Displaying sample family contacts.", Toast.LENGTH_LONG).show();
        }

        // On tapping a contact, trigger an SMS intent to alert them
        lvContacts.setOnItemClickListener((parent, view, position, id) -> {
            String contactInfo = mContactDisplayList.get(position);
            String phone = (position < mPhoneNumbers.size()) ? mPhoneNumbers.get(position) : "01700000000";

            sendAlertToContact(phone, contactInfo);
        });
    }

    /**
     * LECTURE TOPIC: Querying Android System Content Provider ContactsContract
     */
    private void querySystemContacts() {
        mContactDisplayList.clear();
        mPhoneNumbers.clear();

        Uri contactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        try {
            Cursor cursor = getContentResolver().query(
                    contactsUri,
                    projection,
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            );

            if (cursor != null) {
                int nameCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int numCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                while (cursor.moveToNext() && mContactDisplayList.size() < 50) {
                    String name = cursor.getString(nameCol);
                    String number = cursor.getString(numCol);
                    if (name != null && number != null) {
                        mContactDisplayList.add(name + "\n" + number);
                        mPhoneNumbers.add(number);
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Contacts query exception: " + e.getMessage());
        }

        pbContacts.setVisibility(View.GONE);
        if (mContactDisplayList.isEmpty()) {
            loadFallbackContacts();
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    mContactDisplayList
            );
            lvContacts.setAdapter(adapter);
        }
    }

    private void loadFallbackContacts() {
        mContactDisplayList.clear();
        mPhoneNumbers.clear();

        mContactDisplayList.add("Father (Family Member)\n+880 1711-000001");
        mPhoneNumbers.add("+8801711000001");

        mContactDisplayList.add("Mother (Family Member)\n+880 1811-000002");
        mPhoneNumbers.add("+8801811000002");

        mContactDisplayList.add("Hostel Roommate (Khulna)\n+880 1911-000003");
        mPhoneNumbers.add("+8801911000003");

        mContactDisplayList.add("Lab Partner CSE 2200\n+880 1611-000004");
        mPhoneNumbers.add("+8801611000004");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                mContactDisplayList
        );
        lvContacts.setAdapter(adapter);
        tvEmpty.setVisibility(View.GONE);
    }

    private void sendAlertToContact(String phoneNumber, String contactInfo) {
        // Save selected emergency contact to SharedPreferences
        String name = contactInfo != null && contactInfo.contains("\n") ? contactInfo.split("\n")[0] : contactInfo;
        getSharedPreferences("AppSettings", MODE_PRIVATE).edit()
                .putString("emergency_contact_name", name)
                .putString("emergency_contact_number", phoneNumber)
                .apply();

        String message = "⚡ Load-shedding Alert: Scheduled power cut in " + mArea + " starting soon. Charge your devices!";
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:" + Uri.encode(phoneNumber)));
        smsIntent.putExtra("sms_body", message);

        try {
            startActivity(smsIntent);
        } catch (Exception e) {
            // Fallback to generic ACTION_SEND
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(shareIntent, "Alert contact via:"));
        }
    }
}

