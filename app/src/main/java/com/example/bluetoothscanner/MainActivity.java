package com.example.bluetoothscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Button btnBluetoothOn,btnBluetoothOff,btnScan;
    private BluetoothAdapter bluetoothAdapter;
    ListView recyclerViewDeviceList;
    private ArrayList<String> deviceList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ProgressDialog progressDialog;

    public  ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    public  DeviceListAdapter deviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBluetoothOn = findViewById(R.id.btnOn);
        btnBluetoothOff = findViewById(R.id.btnOff);
        btnScan = findViewById(R.id.btnScan);
       /* btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDeviceScan();
               progressDialog= ProgressDialog.show(view.getContext(),"Scanning", "Scanning Devices....",
                        true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            progressDialog.dismiss();
                        }
                        catch(InterruptedException ex){
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }
        });*/
        recyclerViewDeviceList = findViewById(R.id.listdeviceFound);
     /*   recyclerViewDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String device = deviceList.get(i);
                Toast.makeText(MainActivity.this, "" + device, Toast.LENGTH_SHORT).show();
                BluetoothDevice btDevice = bluetoothAdapter.getRemoteDevice(device);
                Log.i("Log", "The device : " + device);

            }
        });*/


     IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
     registerReceiver(broadcastReceiver2,filter);

     recyclerViewDeviceList.setOnItemClickListener(MainActivity.this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       /* Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice d: pairedDevices) {
                String deviceName = d.getName();
                String macAddress = d.getAddress();
                Log.i("@ajinkya", "paired device: " + deviceName + " at " + macAddress);
                // do what you need/want this these list items
            }
        }*/

       recyclerViewDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            /*  String s = recyclerViewDeviceList.getItemAtPosition(i).toString();
              Toast.makeText(MainActivity.this, "Scan devices list ===>>> " + s, Toast.LENGTH_SHORT).show();

               Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
               if (pairedDevices.size() > 0) {
                   for (BluetoothDevice d: pairedDevices) {
                       String deviceName = d.getName();
                       String macAddress = d.getAddress();
                       Log.i("@ajinkya", "paired device: " + deviceName + " at " + macAddress);
                       // do what you need/want this these list items
                       Log.i("@ajinkya", "Try to pair with device: " + deviceName + " at " + macAddress);
                       d.createBond();
                       arrayAdapter.add(deviceName);
                   }

               }*/
               Log.d("@ajinkya","You clicked a device");
               String device_Name = bluetoothDevices.get(i).getName();
               String device_Address = bluetoothDevices.get(i).getAddress();

               Log.d("@ajinkya","On Item clicked  device name === >> " + device_Name);
               Log.d("@ajinkya","On Item clicked  device address === >> " + device_Address);

               if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                   Log.d("@ajinkya","Trying to pairing  with device " + device_Name);
                   bluetoothDevices.get(i).createBond();
               }
           }
       });

       Button btnPair = findViewById(R.id.btnPair);
       // btnPair.setEnabled(false);
       btnPair.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Toast.makeText(MainActivity.this, "Get Pair device list ===>>>", Toast.LENGTH_SHORT).show();
               if(bluetoothAdapter==null){
                   Toast.makeText(getApplicationContext(),"Bluetooth Not Supported",Toast.LENGTH_SHORT).show();
               }
               else{
                   Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
                   ArrayList list = new ArrayList();
                   if(pairedDevices.size()>0){
                       for(BluetoothDevice device: pairedDevices){
                           String devicename = device.getName();
                           String macAddress = device.getAddress();
                          // list.add("Name: "+devicename+"MAC Address: "+macAddress);
                           list.add("Name: "+devicename);
                       }
                      // lstvw = (ListView) findViewById(R.id.deviceList);
                       arrayAdapter = new ArrayAdapter(getApplicationContext(),R.layout.pair_devices_list,R.id.deviceName, list);
                       recyclerViewDeviceList.setAdapter(arrayAdapter);
                   }
               }



           }
       });

    }

    private void callDeviceScan() {
        bluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver,filter);
        //progressDialog.dismiss();
    }

    /*For BroadcastReceiver discovering bluetooth devices start here*/
    private BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("@ajinkya", "Action found");
            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDevices.add(device);
                Log.d("@ajinkya","on Receive " + device.getName() + " : " + device.getAddress());
                deviceListAdapter = new DeviceListAdapter(context,R.id.device_adapter_view,bluetoothDevices);
                recyclerViewDeviceList.setAdapter(deviceListAdapter);

            }
        }
    };
    /*For BroadcastReceiver discovering bluetooth devices end here*/

    private BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    // CONNECT

                }
                if (device.getBondState() == BluetoothDevice.BOND_BONDING){

                }if (device.getBondState() == BluetoothDevice.BOND_NONE){

                }


            }
        }
    };

    final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    // CONNECT

                }
                if (device.getBondState() == BluetoothDevice.BOND_BONDING){

                }
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(device.getName()+'\n'+device.getAddress());
                Log.i("@ajinkya", "found device list ===>> " + device.getName());
                recyclerViewDeviceList.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1,
                        deviceList) {
                });
            }
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(bluetoothReceiver);
        unregisterReceiver(broadcastReceiver1);
        unregisterReceiver(broadcastReceiver2);
        super.onDestroy();
    }

    public void on(View view){
        if (!bluetoothAdapter.isEnabled()){
            Toast.makeText(this, "Bluetooth Turn On", Toast.LENGTH_SHORT).show();
            Intent bluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothOn,0);

        }else {
            Toast.makeText(this, "Bluetooth Already On", Toast.LENGTH_SHORT).show();
        }
    }


    public void off(View v){
        bluetoothAdapter.disable();
        Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }

    /* OnClick method for scan devices start here*/
    public void scanDevice(View view) {
        Log.d("@ajinkya","Looking for unpaired devices");

        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            Log.d("@ajinkya","btnDiscover: Canceling discovery.");

            checkBTPermission();

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDeviceIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver1, discoverDeviceIntent);

        }if (!bluetoothAdapter.isDiscovering()){
            checkBTPermission();
            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDeviceIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver1, discoverDeviceIntent);
        }

    }

    /* OnClick method for scan devices end here*/

    /* Check bluetooth permission start here*/
    private void checkBTPermission() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0){
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
            }else {
                Log.d("@ajinkya","Check Permission: No need to check permission.  SDk version < LOLLIPOP");
            }
        }

    }

    /* Check bluetooth permission end here*/
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        bluetoothAdapter.cancelDiscovery();

        Log.d("@ajinkya","You clicked a device");
        String device_Name = bluetoothDevices.get(i).getName();
        String device_Address = bluetoothDevices.get(i).getAddress();

        Log.d("@ajinkya","On Item clicked  device name === >> " + device_Name);
        Log.d("@ajinkya","On Item clicked  device address === >> " + device_Address);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d("@ajinkya","Trying to pairing  with device " + device_Name);
            bluetoothDevices.get(i).createBond();
        }
    }
}