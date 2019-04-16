package com.example.agnosticblescanner.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.agnosticblescanner.R;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import java.util.Collection;
import java.util.HashSet;
import datamodel.BeaconModel;
import helpers.GenericHelper;
import listadapters.CustomBeaconListAdapter;

public class GenericBleScannerActivity extends AppCompatActivity implements BeaconConsumer {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private String bleScanningUUID;
    private Region region;
    private BeaconManager beaconManager;
    private ListView beaconListView;
    private HashSet<Beacon> foundBeacons;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_generic_ble_scanner);
        this.checkPermissions();
        this.assignLocalVariables();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.checkPermissions();
        this.assignLocalVariables();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (this.region != null) {
            try {
                this.beaconManager.stopRangingBeaconsInRegion(this.region);
            }
            catch (RemoteException e) {
                GenericHelper.alertUser(this, e.toString(), "BLE Scan Result");
            }
        }

        this.beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        this.region = new Region("generic-beacon", null, null, null);

        this.beaconManager.removeAllRangeNotifiers();

        this.beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
            HashSet<Beacon> updatedBeacons = new HashSet<>();
            for (Beacon beacon : collection) {
                if (beacon.getId1().toString().equalsIgnoreCase(GenericBleScannerActivity.this.bleScanningUUID)) {
                    boolean beaconAlreadyExists = false;
                    for (Beacon existingBeacon : GenericBleScannerActivity.this.foundBeacons) {
                        if (existingBeacon.getId1().toString().equalsIgnoreCase(beacon.getId1().toString())
                                && existingBeacon.getId2().toString().equalsIgnoreCase(beacon.getId2().toString())
                                && existingBeacon.getId3().toString().equalsIgnoreCase(beacon.getId3().toString())) {
                            beaconAlreadyExists = true;
                            break;
                        }
                    }

                    if (!beaconAlreadyExists) {
                        updatedBeacons.add(beacon);
                    }
                }
            }

            GenericBleScannerActivity.this.updateBeaconList(updatedBeacons);
            }
        });

        try {
            this.beaconManager.startRangingBeaconsInRegion(this.region);
        }
        catch (RemoteException e) {
            GenericHelper.alertUser(this, e.toString(), "BLE Scan Result");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });

                    builder.show();
                }

                return;
            }
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This application needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });

                builder.show();
            }
        }
    }

    private void assignLocalVariables() {
        this.bleScanningUUID = this.getIntent().getExtras().getString("bleUUID");
        String beaconType = this.getIntent().getExtras().getString("beaconType");
        String scanMessage = String.format("Scanning for %s beacons", beaconType);

        ((TextView)this.findViewById(R.id.bleScanningDetailsTextBox)).setText(scanMessage);

        this.foundBeacons = new HashSet<Beacon>();

        this.handler = new Handler();

        this.beaconListView = this.findViewById(R.id.beaconsListView);

        this.beaconManager = BeaconManager.getInstanceForApplication(this);
        this.beaconManager.setForegroundScanPeriod(5100);
        this.beaconManager.setForegroundBetweenScanPeriod(2000);
        this.beaconManager.setBackgroundScanPeriod(5100);
        this.beaconManager.setBackgroundBetweenScanPeriod(2000);
        this.beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        this.beaconManager.bind(this);
    }

    private void updateBeaconList(HashSet<Beacon> updatedBeacons) {
        this.foundBeacons.addAll(updatedBeacons);
        HashSet<BeaconModel> beaconModels = new HashSet<>();
        for (Beacon beacon : this.foundBeacons) {
            beaconModels.add(new BeaconModel(beacon));
        }

        BeaconModel[] beaconModelArray = beaconModels.toArray(new BeaconModel[beaconModels.size()]);
        CustomBeaconListAdapter adapter = new CustomBeaconListAdapter(this, beaconModelArray);
        this.beaconListView.setAdapter(adapter);
        this.beaconListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BeaconModel clicked = (BeaconModel)parent.getItemAtPosition(position);
                GenericHelper.alertUser(GenericBleScannerActivity.this, clicked.getDisplayMessage(), "Beacon Details");
            }
        });

        this.beaconListView.invalidate();
    }
}