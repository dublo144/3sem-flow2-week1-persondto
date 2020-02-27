package dto;

import entities.Person;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PersonListDTO {
    List<PersonDTO> all = new ArrayList<>();

    public PersonListDTO(List<Person> personList) {
        personList.forEach(person -> {
            all.add(new PersonDTO(person));
        });
    }
}
