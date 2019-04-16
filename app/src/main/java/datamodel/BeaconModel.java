package datamodel;

import org.altbeacon.beacon.Beacon;

public class BeaconModel {

    //region Fields

    private String uuid;
    private int major;
    private int minor;
    private double proximity;
    private int rssi;
    private int tx;
    private long accuracy;

    // endregion

    //region Constructors

    public BeaconModel(Beacon beacon) {
        this.uuid = beacon.getId1().toString();
        this.major = beacon.getId2().toInt();
        this.minor = beacon.getId3().toInt();
        this.proximity = beacon.getDistance();
        this.rssi = beacon.getRssi();
        this.tx = beacon.getTxPower();
        this.accuracy = Math.round(beacon.getDistance() * 100.0) / 100;
    }

    // endregion

    //region Methods

    public String getUUID() {
        return this.uuid;
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public double getProximity() {
        return this.proximity;
    }

    public int getRssi() {
        return this.rssi;
    }

    public int getTx() {
        return this.tx;
    }

    public long getAccuracy() {
        return this.accuracy;
    }

    public String getDisplayMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("UUID: %s\n", this.uuid));
        builder.append(String.format("Major: %d\n", this.major));
        builder.append(String.format("Minor: %d\n", this.minor));
        builder.append(String.format("Proximity: %s\n", String.valueOf(this.proximity)));
        builder.append(String.format("RSSI: %d\n", this.rssi));
        builder.append(String.format("TX: %d\n", this.tx));
        builder.append(String.format("Accuracy: %s\n", String.valueOf(this.accuracy)));

        return builder.toString();
    }

    // endregion
}