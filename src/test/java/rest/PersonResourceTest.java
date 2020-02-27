package rest;
/*
 * author mads
 * version 1.0
 */

import com.google.gson.JsonObject;
import entities.Person;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;
import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.parsing.Parser;

import java.net.URI;
import java.sql.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OneToMany;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;


public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Person p1, p2;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.DROP_AND_CREATE);

        httpServer = startServer();
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
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

    @Test
    public void testServerIsUp() {
        given().when().get("/person").then().statusCode(HttpStatus.OK_200.getStatusCode());
    }

    @Test
    public void testGetAllPersons() {
        given().when()
                .get("/person")
                .then().assertThat().body("all", hasSize(2));
    }

    @Test
    public void testAddPerson() {
        Map<String, String> content = new HashMap<>();
        content.put("firstName", "Test");
        content.put("lastName", "TestLast");
        content.put("phone", "11223344");

        given()
                .contentType(ContentType.JSON)
                .with()
                .body(Arrays.asList(content))
                .when()
                .post("/person/add")
                .then()
                .body("all.firstName", contains("Test"));
    }

    @Disabled // TODO
    @Test
    public void testGetPersonByid() {
        Long id = p1.getId();
        given().when()
                .get("/person/{id}", id)
                .then()
                .body("firstName", is("Mads"));
    }

    @Test
    public void testUpdatePerson() {
        Long id = p1.getId();
        Map<String, String> content = new HashMap<>();
        content.put("firstName", "edited");

        given()
                .contentType(ContentType.JSON)
                .with()
                .body(content)
                .when()
                .put("person/{id}", id)
                .then()
                .body("firstName", is("edited"));
    }

    @Test
    public void testDeletePerson() {
        Long id = p1.getId();
        given()
                .when()
                .delete("/person/{id}", id)
                .then()
                .statusCode(200)
                .body("firstName", is("Mads"));
    }
}
