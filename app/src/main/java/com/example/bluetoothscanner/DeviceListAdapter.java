package com.example.bluetoothscanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ajinkya on 3/22/2021.
 */
public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater layoutInflater;
    private ArrayList<BluetoothDevice> devices;
    private int mViewResourceId;

    public DeviceListAdapter(Context context,int resourceId,ArrayList<BluetoothDevice> devices){
        super(context,resourceId,devices);
        this.devices = devices;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = resourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.device_adapter_view, null);

        BluetoothDevice device = devices.get(position);

        if (device != null) {
            TextView deviceName = convertView.findViewById(R.id.deviceName);
            TextView deviceAddress = convertView.findViewById(R.id.deviceAddress);

            deviceName.setText(device.getName());
            deviceAddress.setText(device.getAddress());
        }
            return convertView;
        }
}
