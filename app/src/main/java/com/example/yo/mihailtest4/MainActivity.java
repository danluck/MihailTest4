package com.example.yo.mihailtest4;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        b_on = findViewById(R.id.b_on);
        b_off = findViewById(R.id.b_off);
        b_do_discover = findViewById(R.id.b_do_discover);
        b_list = findViewById(R.id.b_list);

        list = findViewById(R.id.list_view);

        b_search = findViewById(R.id.b_search);

        bluetoothAdapterStatusValue = findViewById(R.id.textViewBluetoothStatusValue);
    }

    private void SetEventHandlers() {
        b_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "b_on: ACTION_REQUEST_ENABLE");
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLED);
            }
        });

        b_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "b_off: disable");
                mBluetoothAdapter.disable();
            }
        });

        b_do_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBluetoothAdapter.isDiscovering()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVERABLE);
                }
            }
        });

        b_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

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
                mBluetoothAdapter.startDiscovery();
            }
        });
    }

    private void CheckDeviceOpportunities() {
        if (mBluetoothAdapter == null) {
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

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (mBluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "mReceiver: STATE_OFF");
                        bluetoothAdapterStatusValue.setText(R.string.bluetoot_adapter_status_value_off);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mReceiver: STATE_TURNING_OFF");
                        bluetoothAdapterStatusValue.setText(R.string.bluetoot_adapter_status_value_turning_off);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mReceiver: STATE_ON");
                        bluetoothAdapterStatusValue.setText(R.string.bluetoot_adapter_status_value_on);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mReceiver: STATE_TURNING_ON");
                        bluetoothAdapterStatusValue.setText(R.string.bluetoot_adapter_status_value_turning_on);
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Start application");

        GetElementsIds();

        CheckDeviceOpportunities();

        SetEventHandlers();

        bluetoothAdapterStatusValue.setText(mBluetoothAdapter.isEnabled() ?
                R.string.bluetoot_adapter_status_value_on :
                R.string.bluetoot_adapter_status_value_off);

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, intentFilter);
    }

    private static final int REQUEST_ENABLED = 0;
    private static final int REQUEST_DISCOVERABLE = 0;

    private Button b_on, b_off, b_do_discover, b_list;
    private Button b_search;
    private ListView list;
    private BluetoothAdapter mBluetoothAdapter;

    private TextView bluetoothAdapterStatusValue;
}
