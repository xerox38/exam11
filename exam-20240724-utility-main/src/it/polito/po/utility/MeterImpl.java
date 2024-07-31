package it.polito.po.utility;

import java.util.Optional;

public class MeterImpl implements Meter {
    private String id;
    private String sn;
    private String brand;
    private String model;
    private String unit;
    private ServicePointImpl servicePoint;

    public MeterImpl(String id, String sn, String brand, String model, String unit) {
        this.id = id;
        this.sn = sn;
        this.brand = brand;
        this.model = model;
        this.unit = unit;
    }

    public String getId() {
        return id;
    }

    public String getSN() {
        return sn;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getUnit() {
        return unit;
    }

    public Optional<ServicePoint> getServicePoint() {
        return Optional.ofNullable(servicePoint);
    }

    public void setServicePoint(ServicePointImpl servicePoint) {
        this.servicePoint = servicePoint;
    }
}

