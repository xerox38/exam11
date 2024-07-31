package it.polito.po.utility;

public class ContractImpl implements Contract {
    private String id;
    private User user;
    private ServicePoint servicePoint;

    public ContractImpl(String id, User user, ServicePoint servicePoint) {
        this.id = id;
        this.user = user;
        this.servicePoint = servicePoint;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public ServicePoint getServicePoint() {
        return servicePoint;
    }
}
