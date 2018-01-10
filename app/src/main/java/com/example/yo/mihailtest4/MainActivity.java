package com.example.yo.mihailtest4;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
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
        b_scan_le = findViewById(R.id.b_scan_le);

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
                Log.d(TAG, "Discover: start discovering");
                if (mBluetoothAdapter.isDiscovering()) {
                    Log.d(TAG, "Discover: cancelDiscovery");
                    mBluetoothAdapter.cancelDiscovery();
                }

                mBluetoothAdapter.startDiscovery();
                IntentFilter discoveryDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadcastReceiver3, discoveryDevicesIntent);
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
                //mBluetoothAdapter.startDiscovery();
            }
        });

        b_scan_le.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Permission for scan granted, start scanLeDevice");
                    scanLeDevice(true);
                    // Permission granted, yay! Start the Bluetooth device scan.
                } else {
                    // Alert the user that this application requires the location permission to perform the scan.
                }
            }
        }
    }

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;

    private DeviceListAdapter mLeDeviceListAdapter;

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: mLeScanCallback");
                            Log.d(TAG, "run: Find device name:" + device.getName() + "address:" + device.getAddress());
                            //mLeDeviceListAdapter.add(device);
                            //mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    private boolean mScanning;
    private Handler mHandler;

    // Stops scanning after 30 seconds.
    private static final long SCAN_PERIOD = 30000;

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            /*
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);*/

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
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

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "mBroadcastReceiver3: onReceive ACTION_FOUND");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBtDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ":" + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBtDevices);
                listViewDeviceFound.setAdapter(mDeviceListAdapter);
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

    public ArrayList<BluetoothDevice> mBtDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView listViewDeviceFound;


    private static final int REQUEST_ENABLED = 0;
    private static final int REQUEST_DISCOVERABLE = 0;

    private Button b_on, b_off, b_do_discover, b_list;
    private Button b_search;
    private Button b_scan_le;
    private ListView list;
    private BluetoothAdapter mBluetoothAdapter;

    private TextView bluetoothAdapterStatusValue;

    public void Discover(View view) {

    }
}
