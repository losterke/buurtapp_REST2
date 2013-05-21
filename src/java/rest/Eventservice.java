/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import domein.Comment;
import domein.Locatie;
import domein.Event;
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

/**
 *
 * @author tim
 */
@Stateless
@Path("event")
public class Eventservice {
    
    @Resource(name = "jdbc/buurtapp")
    private DataSource source;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Event> getAllEvents(){
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Event INNER JOIN USER ON Event.AUTEUR = USER.ID")) {
                try (ResultSet rs = stat.executeQuery()) {
                    List<Event> results = new ArrayList<>();
                    while (rs.next()) {
                        Event e = new Event();
                        e.setId(rs.getInt("Event.ID"));
                        e.setNaam(rs.getString("Event.Naam"));                        
                        e.setInfo(rs.getString("Info"));
                        e.setStart(rs.getDate("Start"));
                        e.setEnd(rs.getDate("Einde"));
                        
                        User u = new User();
                        u.setId(rs.getInt("User.ID"));
                        u.setNaam(rs.getString("User.Naam"));
                        u.setVoornaam(rs.getString("Voornaam"));
                        u.setEmail(rs.getString("email"));
                        e.setAuteur(u);
                        
                        Locatie l = new Locatie();
                        l.setLatitude(rs.getDouble("Latitude"));
                        l.setLongitude(rs.getDouble("Longitude"));
                        e.setLocatie(l);
                        
                        results.add(e);
                    }
                    return results;
                }
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }       
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addEvent(Event e){
        try (Connection conn = source.getConnection()) {
            
            if(e.getAuteur() == null){
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Een melding moet een auteur hebben.").build());
            }
            
            try (PreparedStatement stat = conn.prepareStatement("SELECT MAX(ID) FROM Event")) {
                try (ResultSet rs = stat.executeQuery()) {
                    if (rs.next()) {
                        e.setId(rs.getInt(1) + 1);
                    } else {
                        e.setId(1);
                    }
                }
            }
            
            try (PreparedStatement stat = conn.prepareStatement("INSERT INTO Event(ID,naam,info,start,einde,latitude,longitude,auteur) VALUES(?,?, ?, ?, ?,?,?,?)")) {
                stat.setInt(1, e.getId());
                stat.setString(2, e.getNaam());
                stat.setString(3, e.getInfo());
                stat.setDate(4, e.getStart());
                stat.setDate(5, e.getEnd());
                stat.setDouble(6, e.getLocatie().getLatitude());
                stat.setDouble(7, e.getLocatie().getLongitude());
                stat.setInt(8, e.getAuteur().getId());
                stat.executeUpdate();
            }
            
            return Response.created(URI.create("/" + e.getId())).build();
            
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }               
    }
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Event getEvent(int id){
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Event INNER JOIN USER ON event.auteur = USER.ID WHERE Event.ID = ?")) {
                stat.setInt(1, id);
                try (ResultSet rs = stat.executeQuery()) {
                    if (rs.next()) {
                        Event e = new Event();
                        e.setId(rs.getInt("Event.ID"));
                        e.setNaam(rs.getString("Event.Naam"));
                        e.setInfo(rs.getString("Info"));
                        e.setStart(rs.getDate("Start"));
                        e.setEnd(rs.getDate("Einde"));
                        
                        User u = new User();
                        u.setId(rs.getInt("User.ID"));
                        u.setNaam(rs.getString("User.Naam"));
                        u.setVoornaam(rs.getString("Voornaam"));
                        u.setEmail(rs.getString("email"));
                        e.setAuteur(u);
                        
                        Locatie l = new Locatie();
                        l.setLatitude(rs.getDouble("Latitude"));
                        l.setLongitude(rs.getDouble("Longitude"));
                        e.setLocatie(l);
                        
                        return e;
                    }else{
                        throw new WebApplicationException(Response.Status.NOT_FOUND);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
    }
    
    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateEvent(@PathParam("id") int id, Event e){
        try (Connection conn = source.getConnection()) {
            
            if(e.getAuteur() == null){
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Een melding moet een auteur hebben.").build());
            }
            
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Event WHERE ID = ?")) {
                stat.setInt(1, id);
                try (ResultSet rs = stat.executeQuery()) {
                    if (!rs.next()) {
                        throw new WebApplicationException(Response.Status.NOT_FOUND);
                    }
                }
            }
            
            try (PreparedStatement stat = conn.prepareStatement("UPDATE Event SET NAam = ?,info = ?, Start = ?, einde = ?, LATITUDE = ?, LONGITUDE =  ?, Auteur = ? WHERE ID = ?")) {
                
                stat.setString(1, e.getNaam());
                stat.setString(2, e.getInfo());
                stat.setDate(3, e.getStart());
                stat.setDate(4, e.getEnd());
                stat.setDouble(5, e.getLocatie().getLatitude());
                stat.setDouble(6, e.getLocatie().getLongitude());
                stat.setInt(7, e.getAuteur().getId());
                stat.setInt(8, id);
                stat.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
    }
    
    @Path("{id}")
    @DELETE
    public void removeEvent(@PathParam("id") int id) {
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Event WHERE ID = ?")) {
                stat.setInt(1, id);
                try (ResultSet rs = stat.executeQuery()) {
                    if (!rs.next()) {
                        throw new WebApplicationException(Response.Status.NOT_FOUND);
                    }
                }
            }
            
            try (PreparedStatement stat = conn.prepareStatement("DELETE FROM Event WHERE ID = ?")) {
                stat.setInt(1, id);
                stat.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
    }
    
    @Path("{id}/comment")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Comment> getAllCommentForEvent(@PathParam("id") int eventId) {
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Comment INNER JOIN USER ON Comment.auteur = USER.ID WHERE Event = ?")) {
                stat.setInt(1,eventId);
                try (ResultSet rs = stat.executeQuery()) {
                    List<Comment> results = new ArrayList<>();
                    while (rs.next()) {
                        Comment c = new Comment();
                        c.setId(rs.getInt("Comment.id"));
                        c.setInhoud(rs.getString("inhoud"));
                        
                        
                        User u = new User();
                        u.setId(rs.getInt("User.id"));
                        u.setNaam(rs.getString("Naam"));
                        u.setVoornaam(rs.getString("Voornaam"));
                        u.setEmail(rs.getString("email"));
                        c.setAuteur(u);
                        
                        results.add(c);                        
                    }
                    return results;
                }
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
    }
    
    @Path("{id}/comment")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCommentToEvent(@PathParam("id") int eventId, Comment c) {
        try (Connection conn = source.getConnection()) {
            
            if(c.getAuteur() == null){
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Een comment moet een auteur hebben.").build());
            }
            
            try (PreparedStatement stat = conn.prepareStatement("SELECT MAX(ID) FROM Comment")) {
                try (ResultSet rs = stat.executeQuery()) {
                    if (rs.next()) {
                        c.setId(rs.getInt(1) + 1);
                    } else {
                        c.setId(1);
                    }
                }
            }
            
            try (PreparedStatement stat = conn.prepareStatement("INSERT INTO comment (id,inhoud,auteur,event) VALUES(?, ?, ?, ?)")) {
                stat.setInt(1, c.getId());
                stat.setString(2, c.getInhoud());
                stat.setInt(3, c.getAuteur().getId());
                stat.setInt(4, eventId);
                stat.executeUpdate();
            }
            
            return Response.created(URI.create("/" + c.getId())).build();
            
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
    }
    
    @Path("{id}/comment/{cid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Comment getComment(@PathParam("id") int eventId, @PathParam("cid") int commentId) {
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Comment INNER JOIN User ON Comment.auteur = user.id WHERE comment.ID = ? AND melding.ID = ?")) {
                stat.setInt(1, commentId);
                stat.setInt(2, eventId);
                try (ResultSet rs = stat.executeQuery()) {
                    if(rs.next()){
                        Comment c = new Comment();
                        c.setId(rs.getInt("Comment.Id"));
                        c.setInhoud(rs.getString("Inhoud"));
                        
                        User u = new User();
                        u.setId(rs.getInt("User.id"));
                        u.setNaam(rs.getString("Naam"));
                        u.setVoornaam(rs.getString("Voornaam"));
                        u.setEmail(rs.getString("email"));
                        c.setAuteur(u);
                        
                        return c;
                    }
                }
                
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
        return null;
    }
    
    @Path("{id}/comment/{cid}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateComment(@PathParam("id") int eventId, @PathParam("cid") int commentId, Comment c) {
        try (Connection conn = source.getConnection()) {
            if (c.getAuteur() == null) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Een comment moet een auteur hebben.").build());
            }
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM comment WHERE ID = ? AND event = ?")) {
                stat.setInt(1, commentId);
                stat.setInt(2, eventId);
                try (ResultSet rs = stat.executeQuery()) {
                    if (!rs.next()) {
                        throw new WebApplicationException(Response.Status.NOT_FOUND);
                    }
                }
            }
            
            try (PreparedStatement stat = conn.prepareStatement("UPDATE comment SET Inhoud = ?, auteur = ? WHERE ID = ? AND event = ?")) {
                stat.setString(1, c.getInhoud());
                stat.setInt(2,c.getAuteur().getId());
                stat.setInt(3, c.getId());
                stat.setInt(4, c.getEvent().getId());
                stat.executeUpdate();
            }
        }catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
    }
            
    @Path("{id}/comment/{cid}")
    @DELETE
    public void removeComment(@PathParam("id") int eventId, @PathParam("cid") int commentId) {
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Comment WHERE ID = ? AND event= ?")) {
                stat.setInt(1, commentId);
                stat.setInt(2, eventId);
                try (ResultSet rs = stat.executeQuery()) {
                    if (!rs.next()) {
                        throw new WebApplicationException(Response.Status.NOT_FOUND);
                    }
                }
            }
            
            try (PreparedStatement stat = conn.prepareStatement("DELETE FROM Comment WHERE ID = ?")) {
                stat.setInt(1, commentId);
                stat.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
    }
}
