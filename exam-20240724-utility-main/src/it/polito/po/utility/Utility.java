package it.polito.po.utility;

import java.time.LocalDate;
import java.util.ArrayList;   
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utility {

    private Map<String, ServicePointImpl> servicePoints = new HashMap<>();

    private Map<String, MeterImpl> meters = new HashMap<>();   

    private Map<String, UserImpl> users = new HashMap<>();

    private Map<String, ContractImpl> contracts = new HashMap<>();
    
    private Map<String, Map<LocalDate, Double>> readings = new HashMap<>();    

    public String defineServicePoint(String municipality, String address, double lat, double lon) {
        String id = "SP" + (servicePoints.size() + 1);
        ServicePointImpl sp = new ServicePointImpl(id, municipality, address, lat, lon);
        servicePoints.put(id, sp);    
        return id;   
    }

    public Collection<String> getServicePoints() {
        return servicePoints.keySet();    
    }

    public ServicePoint getServicePoint(String id) {
        return servicePoints.get(id);   
    }

    public String addMeter(String sn, String brand, String model, String unit) {
        String id = "MT" + (meters.size() + 1);
        MeterImpl m = new MeterImpl(id, sn, brand, model, unit);
        meters.put(id, m);      
        return id;    
    }

    public void installMeter(String spId, String mId) throws UtilityException {
        ServicePointImpl sp = servicePoints.get(spId);    
        MeterImpl m = meters.get(mId);  

        if (sp == null) throw new UtilityException("Service point ID not found: " + spId);

        if (m == null) throw new UtilityException("Meter ID not found: " + mId);
        sp.setMeter(m);
        m.setServicePoint(sp);
    }

    public Meter getMeter(String id) {
        return meters.get(id);
    }

    public String addUser(String cf, String name, String surname, String address, String email) {
        String id = "U" + (users.size() + 1);
        UserImpl u = new UserImpl(id, cf, name, surname, address, email, User.Type.RESIDENTIAL);
        users.put(id, u);
        return id;
    }

    public String addUser(String vat, String businessName, String address, String email) {
        String id = "U" + (users.size() + 1);
        UserImpl u = new UserImpl(id, vat, businessName, "", address, email, User.Type.BUSINESS);
        users.put(id, u);
        return id;
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public Collection<String> getUsers() {
        return users.keySet();
    }

    public String signContract(String uId, String spId) throws UtilityException {
        UserImpl u = users.get(uId);
        ServicePointImpl sp = servicePoints.get(spId);
        if (u == null) throw new UtilityException("User ID not found: " + uId);
        if (sp == null) throw new UtilityException("Service point ID not found: " + spId);
        if (sp.getMeter().isEmpty()) throw new UtilityException("No meter attached to service point: " + spId);
        String id = "C" + (contracts.size() + 1);
        ContractImpl c = new ContractImpl(id, u, sp);
        contracts.put(id, c);
        return id;
    }

    public Contract getContract(String contractId) {
        return contracts.get(contractId);
    }

    public void addReading(String contractId, String meterId, String date, double value) throws UtilityException {
        ContractImpl c = contracts.get(contractId);
        MeterImpl m = meters.get(meterId);
        if (c == null) throw new UtilityException("Contract ID not found: " + contractId);
        if (m == null) throw new UtilityException("Meter ID not found: " + meterId);
        if (!c.getServicePoint().getMeter().get().equals(m)) throw new UtilityException("Meter not attached to service point for contract: " + contractId);
        LocalDate readingDate = LocalDate.parse(date);
        readings.computeIfAbsent(contractId, k -> new HashMap<>()).put(readingDate, value);
    }

    public Map<String, Double> getReadings(String contractId) {
        Map<LocalDate, Double> readingMap = readings.get(contractId);
        if (readingMap == null) return Collections.emptyMap();
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<LocalDate, Double> entry : readingMap.entrySet()) {
            result.put(entry.getKey().toString(), entry.getValue());
        }
        return result;
    }

    public double getLatestReading(String contractId) {
        Map<LocalDate, Double> readingMap = readings.get(contractId);
        if (readingMap == null) return 0;
        return readingMap.entrySet().stream().max(Map.Entry.comparingByKey()).map(Map.Entry::getValue).orElse(0.0);
    }

    public double getEstimatedReading(String contractId, String date) throws UtilityException {
        LocalDate queryDate = LocalDate.parse(date);
        Map<LocalDate, Double> readingMap = readings.get(contractId);
        if (readingMap == null || readingMap.size() < 2) throw new UtilityException("Not enough readings to estimate.");

        List<Map.Entry<LocalDate, Double>> entries = new ArrayList<>(readingMap.entrySet());
        entries.sort(Map.Entry.comparingByKey());

        LocalDate t1 = null, t2 = null;
        Double y1 = null, y2 = null;

        for (int i = 0; i < entries.size(); i++) {
            LocalDate currentDate = entries.get(i).getKey();
            if (currentDate.isAfter(queryDate)) {
                t2 = currentDate;
                y2 = entries.get(i).getValue();
                if (i > 0) {
                    t1 = entries.get(i - 1).getKey();
                    y1 = entries.get(i - 1).getValue();
                }
                break;
            }
        }

        if (t1 == null && t2 == null) {
            // if the date is after the last reading
            t1 = entries.get(entries.size() - 2).getKey();
            y1 = entries.get(entries.size() - 2).getValue();
            t2 = entries.get(entries.size() - 1).getKey();
            y2 = entries.get(entries.size() - 1).getValue();
            return y1 + (queryDate.toEpochDay() - t1.toEpochDay()) * (y2 - y1) / (t2.toEpochDay() - t1.toEpochDay());
        } else if (t1 == null) {
            // if the date is before the first reading
            throw new UtilityException("Invalid dates for estimation.");
        }

        if (queryDate.isEqual(t1)) return y1;
        if (queryDate.isEqual(t2)) return y2;

        return y1 + (queryDate.toEpochDay() - t1.toEpochDay()) * (y2 - y1) / (t2.toEpochDay() - t1.toEpochDay());
    }

    public double getConsumption(String contractId, String startDate, String endDate) throws UtilityException {
        double startReading = getEstimatedReading(contractId, startDate);
        double endReading = getEstimatedReading(contractId, endDate);
        return endReading - startReading;
    }

    public List<String> getBillBreakdown(String contractId, int startMonth, int endMonth, int year) throws UtilityException {
        LocalDate start = LocalDate.of(year, startMonth, 1);
        LocalDate end = start.plusMonths(endMonth - startMonth + 1).withDayOfMonth(1);
        List<String> result = new ArrayList<>();
        for (LocalDate date = start; date.isBefore(end); date = date.plusMonths(1)) {
            LocalDate nextDate = date.plusMonths(1).withDayOfMonth(1);
            double startReading = getEstimatedReading(contractId, date.toString());
            double endReading = getEstimatedReading(contractId, nextDate.toString());
            double consumption = endReading - startReading;
            result.add(String.format("%s..%s: %.1f -> %.1f = %.1f", date, nextDate.minusDays(1), startReading, endReading, consumption));
        }
        return result;
    }
}
