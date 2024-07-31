package it.polito.po.utility;

public interface User {

    enum Type {
        RESIDENTIAL, BUSINESS
    }
    // add the missing methods

    Type getType();

    String getId();

     String getCF();
     String getName();
     String getSurname();
     String getAddress();
     String getEmail();

}
