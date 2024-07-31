package it.polito.po.utility;

import java.util.Optional;

public class ServicePointImpl implements ServicePoint {
    private String id;
    private String municipality;
    private String address;
    private double lat;
    private double lon;
    private MeterImpl meter;

    public ServicePointImpl(String id, String municipality, String address, double lat, double lon) {
        this.id = id;
        this.municipality = municipality;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    public String getMunicipality() {
        return municipality;
    }

    public String getAddress() {
        return address;
    }

    public Point getPosition() {
        return new Point(lon, lat); // Ensure correct assignment here
    }

    public Optional<Meter> getMeter() {
        return Optional.ofNullable(meter);
    }

    public void setMeter(MeterImpl meter) {
        this.meter = meter;
    }
}
