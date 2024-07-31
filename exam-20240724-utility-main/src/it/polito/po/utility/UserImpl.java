package it.polito.po.utility;

public class UserImpl implements User {
    private String id;
    private String cf;
    private String name;
    private String surname;
    private String address;
    private String email;
    private Type type;

    public UserImpl(String id, String cf, String name, String surname, String address, String email, Type type) {
        this.id = id;
        this.cf = cf;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.email = email;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getCF() {
        return cf;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public Type getType() {
        return type;
    }
}
