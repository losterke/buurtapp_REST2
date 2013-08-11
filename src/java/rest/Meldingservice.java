/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import domein.Comment;
import domein.Foto;
import domein.Locatie;
import domein.Melding;
import domein.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.logging.Param;

/**
 *
 * @author tim
 */
@Stateless
@Path("melding")
public class Meldingservice {
    
    
    private static final int MAX_SIZE_IN_MB = 1;
    
    @Resource(name = "jdbc/buurtapp")
    private DataSource source;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Melding> getAllMeldingen(){
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM MELDING INNER JOIN USER ON Melding.AUTEUR = USER.ID")) {
                try (ResultSet rs = stat.executeQuery()) {
                    List<Melding> results = new ArrayList<>();
                    while (rs.next()) {
                        Melding m = new Melding();
                        m.setId(rs.getInt("Melding.ID"));
                        m.setType(rs.getString("Type"));
                        m.setBeschrijving(rs.getString("Beschrijving"));
                        
                        User u = new User();
                        u.setId(rs.getInt("User.ID"));
                        u.setNaam(rs.getString("Naam"));
                        u.setVoornaam(rs.getString("Voornaam"));
                        u.setEmail(rs.getString("email"));
                        m.setAuteur(u);
                        
                        Locatie l = new Locatie();
                        l.setLatitude(rs.getDouble("Latitude"));
                        l.setLongitude(rs.getDouble("Longitude"));
                        m.setLocatie(l);
                        
                        results.add(m);
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
    public Response addMelding(Melding m){
        try (Connection conn = source.getConnection()) {
            
            if(m.getAuteur() == null){
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Een melding moet een auteur hebben.").build());
            }
            
            try (PreparedStatement stat = conn.prepareStatement("SELECT MAX(ID) FROM Melding")) {
                try (ResultSet rs = stat.executeQuery()) {
                    if (rs.next()) {
                        m.setId(rs.getInt(1) + 1);
                    } else {
                        m.setId(1);
                    }
                }
            }
            
            try (PreparedStatement stat = conn.prepareStatement("INSERT INTO Melding(Id,type,beschrijving,latitude,longitude,auteur) VALUES(?, ?, ?, ?, ?,?)")) {
                stat.setInt(1, m.getId());
                stat.setString(2, m.getType());
                stat.setString(3, m.getBeschrijving());
                stat.setDouble(4, m.getLocatie().getLatitude());
                stat.setDouble(5, m.getLocatie().getLongitude());
                stat.setInt(6, m.getAuteur().getId());
                stat.executeUpdate();
            }
            
            return Response.created(URI.create("/" + m.getId())).build();
            
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }               
    }
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Melding getMelding(@PathParam("id") int id){
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Melding INNER JOIN USER ON melding.auteur = USER.ID  WHERE Melding.ID = ?")) {
                stat.setInt(1, id);
                try (ResultSet rs = stat.executeQuery()) {
                    if (rs.next()) {
                        Melding m = new Melding();
                        m.setId(rs.getInt("Melding.ID"));
                        m.setType(rs.getString("Type"));
                        m.setBeschrijving(rs.getString("Beschrijving"));
                        
                        User u = new User();
                        u.setId(rs.getInt("User.ID"));
                        u.setNaam(rs.getString("Naam"));
                        u.setVoornaam(rs.getString("Voornaam"));
                        u.setEmail(rs.getString("email"));
                        m.setAuteur(u);
                        
                        Locatie l = new Locatie();
                        l.setLatitude(rs.getDouble("Latitude"));
                        l.setLongitude(rs.getDouble("Longitude"));
                        m.setLocatie(l);
                        
                        return m;
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
    public void updateMelding(@PathParam("id") int id, Melding m){
        try (Connection conn = source.getConnection()) {
            
            if(m.getAuteur() == null){
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Een melding moet een auteur hebben.").build());
            }
            
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Melding WHERE ID = ?")) {
                stat.setInt(1, id);
                try (ResultSet rs = stat.executeQuery()) {
                    if (!rs.next()) {
                        throw new WebApplicationException(Response.Status.NOT_FOUND);
                    }
                }
            }
            
            try (PreparedStatement stat = conn.prepareStatement("UPDATE Melding SET Type = ?, Beschrijving = ?, Auteur = ?, LATITUDE = ?, LONGITUDE =  ? WHERE ID = ?")) {
                stat.setString(1, m.getType());
                stat.setString(2, m.getBeschrijving());
                stat.setInt(3, m.getAuteur().getId());
                stat.setDouble(4, m.getLocatie().getLatitude());
                stat.setDouble(5, m.getLocatie().getLongitude());
                stat.setInt(6, id);
                stat.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
    }
    
    @Path("{id}")
    @DELETE
    public void removeMessage(@PathParam("id") int id) {
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Melding WHERE ID = ?")) {
                stat.setInt(1, id);
                try (ResultSet rs = stat.executeQuery()) {
                    if (!rs.next()) {
                        throw new WebApplicationException(Response.Status.NOT_FOUND);
                    }
                }
            }
            
            try (PreparedStatement stat = conn.prepareStatement("DELETE FROM Melding WHERE ID = ?")) {
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
    public List<Comment> getAllCommentForMessage(@PathParam("id") int meldingId) {
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Melding,Comment INNER JOIN USER ON Comment.auteur = USER.ID WHERE Melding.ID = ? AND melding.id = comment.melding")) {
                stat.setInt(1,meldingId);
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
                        
                        Melding m = new Melding();
                        m.setId(rs.getInt("Melding.id"));
                        m.setType(rs.getString("Type"));
                        m.setBeschrijving(rs.getString("Beschrijving"));
                        
                        Locatie l = new Locatie();
                        l.setLatitude(rs.getDouble("Latitude"));
                        l.setLongitude(rs.getDouble("Longitude"));
                        m.setLocatie(l);
                        c.setMelding(m);
                        
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
    public Response addCommentToMessage(@PathParam("id") int meldingId, Comment c) {
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
            
            try (PreparedStatement stat = conn.prepareStatement("INSERT INTO comment (id,inhoud,auteur,melding) VALUES(?, ?, ?, ?)")) {
                stat.setInt(1, c.getId());
                stat.setString(2, c.getInhoud());
                stat.setInt(3, c.getAuteur().getId());
                stat.setInt(4, meldingId);
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
    public Comment getComment(@PathParam("id") int meldingId, @PathParam("cid") int commentId) {
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Melding,Comment INNER JOIN User ON Comment.auteur = user.id WHERE comment.ID = ? AND melding.ID = ?")) {
                stat.setInt(1, commentId);
                stat.setInt(2, meldingId);
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
                        
                        Melding m = new Melding();
                        m.setId(rs.getInt("Melding.id"));
                        m.setType(rs.getString("Type"));
                        m.setBeschrijving(rs.getString("Beschrijving"));
                        
                        Locatie l = new Locatie();
                        l.setLatitude(rs.getDouble("Latitude"));
                        l.setLongitude(rs.getDouble("Longitude"));
                        m.setLocatie(l);
                        c.setMelding(m);
                        
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
    public void updateComment(@PathParam("id") int meldingId, @PathParam("cid") int commentId, Comment c) {
        try (Connection conn = source.getConnection()) {
            if (c.getAuteur() == null) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Een comment moet een auteur hebben.").build());
            }
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM comment WHERE ID = ? AND melding = ?")) {
                stat.setInt(1, commentId);
                stat.setInt(2, meldingId);
                try (ResultSet rs = stat.executeQuery()) {
                    if (!rs.next()) {
                        throw new WebApplicationException(Response.Status.NOT_FOUND);
                    }
                }
            }
            
            try (PreparedStatement stat = conn.prepareStatement("UPDATE comment SET Inhoud = ?, auteur = ? WHERE ID = ? AND melding = ?")) {
                stat.setString(1, c.getInhoud());
                stat.setInt(2,c.getAuteur().getId());
                stat.setInt(3, c.getId());
                stat.setInt(4, c.getMelding().getId());
                stat.executeUpdate();
            }
        }catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
    }
            
    @Path("{id}/comment/{cid}")
    @DELETE
    public void removeComment(@PathParam("id") int meldingId, @PathParam("cid") int commentId) {
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Comment WHERE ID = ? AND melding = ?")) {
                stat.setInt(1, commentId);
                stat.setInt(2, meldingId);
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
    
   @Path("{id}/foto")
    @GET
    @Produces("image/jpeg")
    public InputStream getFirstFotoForMessage(@PathParam("id") int meldingId){
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Melding,Foto INNER JOIN User ON Foto.auteur = user.id WHERE melding.ID = ? AND melding.ID = foto.melding")) {
                stat.setInt(1, meldingId);
                try (ResultSet rs = stat.executeQuery()) {
                    if(rs.next()){
                        Blob Foto = rs.getBlob("Foto");
                        InputStream image = Foto.getBinaryStream();
                        return image;
                    }
                }
                
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
        return null;
    }
   
   @Path("{id}/foto/{fid}")
    @GET
    @Produces("image/jpeg")
    public InputStream getFoto(@PathParam("id") int meldingId,@PathParam("fid") int fotoId){
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Melding,Foto INNER JOIN User ON Foto.auteur = user.id WHERE Foto.ID = ? AND melding.ID = ?")) {
                stat.setInt(1, fotoId);
                stat.setInt(2, meldingId);
                try (ResultSet rs = stat.executeQuery()) {
                    if(rs.next()){
                        Blob Foto = rs.getBlob("Foto");
                        InputStream image = Foto.getBinaryStream();
                        return image;
                    }
                }
                
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
        return null;
    }     
   
   @Path("{id}/foto")
    @POST
    @Consumes({"image/jpeg", "image/png"})
    public Response addFotoToMessage(@PathParam("id") int meldingId, InputStream in, @HeaderParam("Content-auteur") int aid, @HeaderParam("Content-Type") String fileType, @HeaderParam("Content-Length") long fileSize){
        try (Connection conn = source.getConnection()) {
            Foto f = new Foto();
            User a = new User();
            a.setId(aid);
            f.setAuteur(a);
            
            if(f.getAuteur() == null){
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Een comment moet een auteur hebben.").build());
            }
            
           if (fileSize > 1024 * 1024 * MAX_SIZE_IN_MB) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Image is larger than " + MAX_SIZE_IN_MB + "MB").build());
        }
            
            try (PreparedStatement stat = conn.prepareStatement("SELECT MAX(ID) FROM Foto")) {
                try (ResultSet rs = stat.executeQuery()) {
                    if (rs.next()) {
                        f.setId(rs.getInt(1) + 1);
                    } else {
                        f.setId(1);
                    }
                }
            }
            
            
            try (PreparedStatement stat = conn.prepareStatement("INSERT INTO Foto (id,foto,auteur,melding) VALUES(?, ?, ?, ?)")) {
                stat.setInt(1, f.getId());
                stat.setBinaryStream(2, in);
                stat.setInt(3, f.getAuteur().getId());
                stat.setInt(4, meldingId);
                stat.executeUpdate();
            }
            
            return Response.created(URI.create("/" + f.getId())).build();
            
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }        
    }
   
   @Path("{id}/foto/{fid}")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeFoto(@PathParam("id") int meldingId,@PathParam("fid") int fotoId){
         try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Foto WHERE ID = ? AND melding = ?")) {
                stat.setInt(1, fotoId);
                stat.setInt(2, meldingId);
                try (ResultSet rs = stat.executeQuery()) {
                    if (!rs.next()) {
                        throw new WebApplicationException(Response.Status.NOT_FOUND);
                    }
                }
            }
            
            try (PreparedStatement stat = conn.prepareStatement("DELETE FROM Foto WHERE ID = ?")) {
                stat.setInt(1, fotoId);
                stat.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        }
    }
}       
