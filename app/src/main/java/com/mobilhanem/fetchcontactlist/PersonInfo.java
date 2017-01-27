package com.mobilhanem.fetchcontactlist;

/**
 * Created by alperbeyler on 25/10/15.
 */
public class PersonInfo {

    private String name;
    private String phoneNumber;
    public String getName(){

        return this.name;

    }
    public String getPhoneNumber(){

        return this.phoneNumber;
    }
    public PersonInfo(String name, String phoneNumber){

        this.name = name;
        this.phoneNumber = phoneNumber;

    }
}
