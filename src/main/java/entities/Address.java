package entities;
/*
 * author mads
 * version 1.0
 */

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Address")
@NamedQuery(name = "Address.deleteAllRows", query = "DELETE FROM Address")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;
    private String zip;
    private String city;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_fk")
    private Person person;
}
