package rest;
/*
 * author mads
 * version 1.0
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dto.person.PersonDTO;
import entities.Person;
import exceptions.PersonNotFoundException;
import facades.PersonFacade;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


@Path("/person")
public class PersonResource {
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
            "pu",
            "jdbc:mysql://localhost:3307/f2w1_wednesday",
            "dev",
            "ax2",
            EMF_Creator.Strategy.DROP_AND_CREATE);

    private static final PersonFacade FACADE =  PersonFacade.getPersonFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAll() {
        return Response
                .ok()
                .entity(GSON.toJson(FACADE.getAll()))
                .build();
    }

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPersons (String json) {
        Type listType = new TypeToken<ArrayList<Person>>(){}.getType();
        List<Person> personList = GSON.fromJson(json, listType);
        personList.forEach(person -> {
            person.getAddress().setPerson(person);
        });
        return Response
                .ok()
                .entity(GSON.toJson(FACADE.addPerson(personList)))
                .build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPersonById (@PathParam("id") Long id) throws PersonNotFoundException {
        return Response
                .ok(GSON.toJson(FACADE.getById(id)))
                .build();
    }

    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editPerson (String json, @PathParam("id") Long id) throws PersonNotFoundException {
        PersonDTO personDTO = GSON.fromJson(json, PersonDTO.class);
        personDTO.setId(id);
        return Response
                .ok(GSON.toJson(FACADE.editPerson(personDTO)))
                .build();
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePerson (@PathParam("id") Long id) throws PersonNotFoundException {
        return Response
                .ok(GSON.toJson(FACADE.deletePerson(id)))
                .build();
    }


}
