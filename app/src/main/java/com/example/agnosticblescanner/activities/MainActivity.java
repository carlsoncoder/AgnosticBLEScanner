package com.example.agnosticblescanner.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.agnosticblescanner.R;

import helpers.GenericHelper;

public class MainActivity extends AppCompatActivity {

    private Button scanBleButton;
    private Spinner beaconDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        this.assignLocalVariables();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.assignLocalVariables();
    }

    private void assignLocalVariables() {
        this.scanBleButton = (Button)this.findViewById(R.id.scanBleButton);
        this.scanBleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.launchBleScanningActivity();
            }
        });

        this.beaconDropdown = (Spinner)this.findViewById(R.id.beaconSpinner);
        String[] beaconConfigurations = new String[] { "GAM - API Healthcare", "Kontakt.io - Regular", "Kontakt.io - Secure Shuffling", "BlueCats", "Gimbal"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, beaconConfigurations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.beaconDropdown.setAdapter(adapter);


        Boolean hasBluetoothLE = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        if (!hasBluetoothLE) {
            GenericHelper.alertUser(this, "Bluetooth LE is NOT available on this device!", "BLE Scanner");
            this.scanBleButton.setVisibility(View.GONE);
        }
    }

    private void launchBleScanningActivity() {
        String selectedBeaconConfiguration = this.beaconDropdown.getSelectedItem().toString();
        String uuid = this.determineUUIDForBeaconConfiguration(selectedBeaconConfiguration);

        Bundle bundle = new Bundle();
        bundle.putString("bleUUID", uuid);
        bundle.putString("beaconType", selectedBeaconConfiguration);
        Intent intent = new Intent(this, GenericBleScannerActivity.class);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }

    private String determineUUIDForBeaconConfiguration(String beaconConfiguration) {
        switch (beaconConfiguration) {
            case "GAM - API Healthcare": {
                return "FE913213-B311-4A42-8C16-47FAEAC938EF";
            }
            case "Kontakt.io - Regular": {
                return "F7826DA6-4FA2-4E98-8024-BC5B71E0893E";
            }
            case "Kontakt.io - Secure Shuffling": {
                return "EF9D7835-B7B9-4ABF-9A02-C10AB55E777B";
            }
            case "BlueCats": {
                return "61687109-905F-4436-91F8-E602F514C96D";
            }
            case "Gimbal": {
                return "6E7222B3-ED60-499E-B429-445C35B06CA0";
            }
            default: {
                return null;
            }
        }
    }
}