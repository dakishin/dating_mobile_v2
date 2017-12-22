package module.christian.ru.dating.api;

import java.util.List;

/**
 * Created by dakishin@gmail.com
 * 06.09.2016.
 */
public class Address {
    public List<AddressComponent> address_components;

    public String getCountry() {
        if (address_components == null || address_components.isEmpty()) {
            return null;
        }
        AddressComponent addressComponent = getByType("country", address_components);
        return addressComponent == null ? null : addressComponent.long_name;
    }

    public String getCountryCode() {
        if (address_components == null || address_components.isEmpty()) {
            return null;
        }
        AddressComponent addressComponent = getByType("country", address_components);
        return addressComponent == null ? null : addressComponent.short_name;
    }

    public String getCity() {
        if (address_components == null || address_components.isEmpty()) {
            return null;
        }

        AddressComponent addressComponent = getByType("locality", address_components);

        if (addressComponent != null) {
            return addressComponent.long_name;
        }

        addressComponent = getByType("administrative_area_level_1", address_components);
        return addressComponent == null ? null : addressComponent.short_name;
    }


    private AddressComponent getByType(String searchType, List<AddressComponent> addressComponents) {
        for (AddressComponent addressComponent : addressComponents) {
            for (String type : addressComponent.types) {
                if (searchType.equalsIgnoreCase(type)) {
                    return addressComponent;
                }
            }
        }
        return null;
    }
}

class AddressComponent {
    public String long_name;
    public String short_name;
    public List<String> types;
}

