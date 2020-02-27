package facades;
/*
 * author mads
 * version 1.0
 */

import dto.PersonDTO;
import dto.PersonListDTO;
import entities.Person;
import exceptions.PersonNotFoundException;
import utils.EMF_Creator;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade personfacade;
    private static Person p1, p2;

    public PersonFacadeTest() {
    }

    //@BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/startcode_test",
                "dev",
                "ax2",
                Strategy.CREATE);
        personfacade = PersonFacade.getPersonFacade(emf);
    }

    @BeforeAll
    public static void setUpClassV2() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.DROP_AND_CREATE);
        personfacade = PersonFacade.getPersonFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
        // Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        p1 = new Person("Mads", "Brandt", "22334455");
        p2 = new Person("Name", "Lastname", "44556677");
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.persist(p1);
            em.persist(p2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testGetAllPersons () {
        int expectedSize = 2;
        int actualSize = personfacade.getAll().getAll().size();
        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void testAddPersons () {
        List<Person> personList = Arrays.asList(new Person("Some", "Name", "11223344"));
        personfacade.addPerson(personList);
        int expectedSize = 3;
        int actualSize = emf.createEntityManager().createQuery("SELECT P FROM Person p").getResultList().size();
        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void testGetPersonById () throws PersonNotFoundException {
        PersonDTO personDTO = personfacade.getById(p1.getId());
        assertEquals("Mads", personDTO.getFirstName());
    }

    @Test
    public void testEditPerson () throws PersonNotFoundException {
        PersonDTO personDTO = new PersonDTO(p2);
        personDTO.setFirstName("Edited");

        personfacade.editPerson(personDTO);

        String actualFirstName = personDTO.getFirstName();
        String expectedFirstName = "Edited";
        assertEquals(expectedFirstName, actualFirstName);

    }

    @Test
    public void testEditPerson_usingNewDto () throws PersonNotFoundException {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setId(p1.getId());
        personDTO.setLastName("Edited");

        PersonDTO persistedPerson = personfacade.editPerson(personDTO);

        String actualLastName = persistedPerson.getLastName();
        String expectedLastName = "Edited";
        assertEquals(expectedLastName, actualLastName);
    }

    @Test
    public void testDeletePerson () throws PersonNotFoundException {
        personfacade.deletePerson(p2.getId());
        int expectedSize = 1;
        int actualSize = emf.createEntityManager().createQuery("SELECT P FROM Person p").getResultList().size();
        assertEquals(expectedSize, actualSize);
    }
}
