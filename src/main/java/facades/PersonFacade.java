package facades;
/*
 * author mads
 * version 1.0
 */

import dto.PersonDTO;
import dto.PersonListDTO;
import entities.Person;
import exceptions.PersonNotFoundException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;


public class PersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    private PersonFacade() {
    }

    /**
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public PersonListDTO addPerson(List<Person> persons) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            persons.forEach(person -> {
                em.persist(person);
            });
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return new PersonListDTO(persons);
    }

    public PersonDTO getById(long id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        Person person = em.find(Person.class, id);
        return new PersonDTO(person.getId(), person.getFirstName(), person.getLastName(), person.getPhone());
    }

    public PersonListDTO getAll() {
        EntityManager em = getEntityManager();
        List<Person> personList = em.createQuery("SELECT P FROM Person p", Person.class).getResultList();
        return new PersonListDTO(personList);
    }

    public PersonDTO editPerson(PersonDTO personDTO) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        Person managedPerson = em.find(Person.class, personDTO.getId());
        try {
            em.getTransaction().begin();
            if (personDTO.getFirstName() != null) {
                managedPerson.setFirstName(personDTO.getFirstName());
            }
            if (personDTO.getLastName() != null) {
                managedPerson.setLastName(personDTO.getLastName());
            }
            if (personDTO.getPhone() != null) {
                managedPerson.setPhone(personDTO.getPhone());
            }
            managedPerson.setLastEdited(new Date());
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(managedPerson);
    }


    public PersonDTO deletePerson(Long id) throws PersonNotFoundException {
        EntityManager em = getEntityManager();
        Person person = em.find(Person.class, id);
        if (person == null) {
            throw new PersonNotFoundException("Person not found");
        }
        try {
            em.getTransaction().begin();
            em.remove(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(person);
    }
}
