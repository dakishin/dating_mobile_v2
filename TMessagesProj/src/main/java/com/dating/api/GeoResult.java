package com.dating.api;

import java.util.List;

/**
 * Created by dakishin@gmail.com
 */
public class GeoResult {
    public List<Address> results;

    public String getCountryCode() {
        if (results == null || results.isEmpty()) {
            return null;
        }
        Address address = results.get(0);
        return address.getCountryCode();
    }

    public String getCountry() {
        if (results == null || results.isEmpty()) {
            return null;
        }
        Address address = results.get(0);
        return address.getCountry();
    }

    public String getCity() {
        if (results == null || results.isEmpty()) {
            return null;
        }
        Address address = results.get(0);
        return address.getCity();
    }

}
