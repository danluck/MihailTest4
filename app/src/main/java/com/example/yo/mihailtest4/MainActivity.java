package com.example.yo.mihailtest4;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private void GetElementsIds() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        b_on = findViewById(R.id.b_on);
        b_off = findViewById(R.id.b_off);
        b_do_discover = findViewById(R.id.b_do_discover);
        b_list = findViewById(R.id.b_list);

        list = findViewById(R.id.list_view);

        b_search = findViewById(R.id.b_search);
    }

    private void SetEventHandlers() {
        b_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLED);
            }
        });

        b_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothAdapter.disable();
            }
        });

        b_do_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isDiscovering()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVERABLE);
                }
            }
        });

        b_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                ArrayList<String> devices = new ArrayList<>();

                for (BluetoothDevice bt : pairedDevices) {
                    devices.add(bt.getName());
                }

                ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, devices);

                list.setAdapter(arrayAdapter);

            }
        });

        b_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothAdapter.startDiscovery();
            }
        });
    }

    private void CheckDeviceOpportunities() {
        if (bluetoothAdapter == null) {
            Log.d(TAG, "Bluetooth is not supported on this device");
            Toast.makeText(this,"", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Start application");

        GetElementsIds();

        CheckDeviceOpportunities();

        bluetoothAdapterStatusValue = findViewById(R.id.textViewBluetoothStatusValue);
        bluetoothAdapterStatusValue.setText(bluetoothAdapter.isEnabled() ? R.string.bluetoot_adapter_status_value_enabled :
                R.string.bluetoot_adapter_status_value_disabled);

        SetEventHandlers();
    }

    private static final int REQUEST_ENABLED = 0;
    private static final int REQUEST_DISCOVERABLE = 0;

    private Button b_on, b_off, b_do_discover, b_list;
    private Button b_search;
    private ListView list;
    private BluetoothAdapter bluetoothAdapter;

    private TextView bluetoothAdapterStatusValue;
}
