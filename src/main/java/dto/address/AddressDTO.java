package dto.address;

import entities.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private Long id;
    private String street;
    private String zip;
    private String city;

    public AddressDTO(Address address) {
        this.id = address.getId();
        this.street = address.getStreet();
        this.zip = address.getZip();
        this.city = address.getCity();
    }
}
