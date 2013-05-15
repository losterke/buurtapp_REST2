package rest;

import domein.User;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/*
 * De "users" resource.
 * Deze ondersteunt CRUD operaties voor objecten van de klasse User.
 */
@Stateless
@Path("user")
public class Userservice {

    @Resource(name = "jdbc/buurtapp")
    private DataSource source;

    /*
     * Alle gebruikers opvragen.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM USER")) {
                try (ResultSet rs = stat.executeQuery()) {
                    List<User> results = new ArrayList<>();
                    while (rs.next()) {
                        User u = new User();
                        u.setId(rs.getInt("ID"));
                        u.setNaam(rs.getString("Naam"));
                        u.setVoornaam(rs.getString("Voornaam"));
                        u.setEmail(rs.getString("email"));
                        results.add(u);
                    }
                    return results;
                }
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
    }
    
    /*
     * Een nieuwe gebruiker toevoegen.
     * Het ingediende User object hoeft geen ID te hebben, dit wordt vanzelf
     * ingevuld.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(User u) {
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("INSERT INTO USER(ID,Naam, Voornaam,email) VALUES(?, ?,?,?)")) {
                stat.setInt(1, u.getId());
                stat.setString(2, u.getNaam());
                stat.setString(3, u.getVoornaam());
                stat.setString(4, u.getEmail());
                stat.executeUpdate();
            }
            
            return Response.created(URI.create("/" + u.getId())).build();
            
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
    }
    
    /*
     * Een bestaande gebruiker opvragen op basis van zijn ID.
     */
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("id") int id) {
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM USER WHERE ID = ?")) {
                stat.setInt(1, id);
                try (ResultSet rs = stat.executeQuery()) {
                    if (rs.next()) {
                        User u = new User();
                        u.setId(rs.getInt("ID"));
                        u.setNaam(rs.getString("NAam"));
                        u.setVoornaam(rs.getString("Voornaam"));
                        u.setEmail(rs.getString("email"));
                        return u;
                    } else {
                        throw new WebApplicationException(Status.NOT_FOUND);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
    }
    
    /*
     * Een bestaande gebruiker met het opgegeven ID wijzigen.
     * Het ingediende User object hoeft geen ID te hebben, aangezien deze ID
     * reeds in de URL te vinden is.
     */
    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateUser(@PathParam("id") int id, User u) {
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM USER WHERE ID = ?")) {
                stat.setInt(1, id);
                try (ResultSet rs = stat.executeQuery()) {
                    if (!rs.next()) {
                        throw new WebApplicationException(Status.NOT_FOUND);
                    }
                }
            }
            try (PreparedStatement stat = conn.prepareStatement("UPDATE USER SET Naam = ?, Voornaam = ?, Email = ? WHERE ID = ?")) {
                stat.setString(1, u.getNaam());
                stat.setString(2, u.getVoornaam());
                stat.setString(3, u.getEmail());
                stat.setInt(4, id);
                stat.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
    }
    
    /*
     * Een bestaande gebruiker met het opgegeven ID verwijderen.
     */
    @Path("{id}")
    @DELETE
    public void removeUser(@PathParam("id") int id) {
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM USER WHERE ID = ?")) {
                stat.setInt(1, id);
                try (ResultSet rs = stat.executeQuery()) {
                    if (!rs.next()) {
                        throw new WebApplicationException(Status.NOT_FOUND);
                    }
                }
            }
            try (PreparedStatement stat = conn.prepareStatement("DELETE FROM USER WHERE ID = ?")) {
                stat.setInt(1, id);
                stat.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
    }
}
