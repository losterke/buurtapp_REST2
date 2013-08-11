/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import com.sun.jersey.core.header.FormDataContentDisposition;
import domein.Event;
import domein.Foto;
import domein.Locatie;
import domein.Melding;
import domein.User;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author tim
 */
@Stateless
@Path("foto")
public class Fotoservice {
    
    private static final int MAX_SIZE_IN_MB = 1;
    
    @Resource(name = "jdbc/buurtapp")
    private DataSource source;
    
    /*
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Foto geefFoto(@PathParam("id") int fotoNr) {

        try (Connection conn = source.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM foto WHERE id =?");
            pstmt.setInt(1, fotoNr);
            ResultSet rs = pstmt.executeQuery();

            Blob Foto = rs.getBlob("Foto");
            InputStream imageBlobStream = Foto.getBinaryStream();
            BufferedImage foto = ImageIO.read(imageBlobStream);

            F = new Foto(rs.getInt("id"), foto , (User)null , (Melding)null, (Event)null);


        } catch (SQLException ex) {
            throw new WebApplicationException(ex);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return F;
    }
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public int nieuweFoto(Foto F) {
        int primkey = 0;


        try (Connection conn = source.getConnection()) {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(F.getFoto(), "jpeg", out);
            byte[] buf = out.toByteArray();
            ByteArrayInputStream inStream = new ByteArrayInputStream(buf);


            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO foto VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, F.getId());
            pstmt.setBinaryStream(2, inStream);


            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs != null && rs.next()) {
                primkey = rs.getInt(1);
            }

        } catch (SQLException sqlException) {
            System.out.print("Database Error nieuwefoto");
            sqlException.getMessage();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return primkey;
    }*/
    
    @Path("{id}/foto")
    @GET
    @Produces("image/jpeg")
    public InputStream getFirstFotoForMessage(@PathParam("id") int meldingId){
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM Melding,Foto INNER JOIN User ON Foto.auteur = user.id WHERE melding.ID = ? AND melding.ID = foto.melding")) {
                stat.setInt(1, meldingId);
                try (ResultSet rs = stat.executeQuery()) {
                    if(rs.next()){
                       /* Foto f = new Foto();
                        f.setId(rs.getInt("Foto.Id"));
                        Blob Foto = rs.getBlob("Foto");
                        InputStream imageBlobStream = Foto.getBinaryStream();
                        f.setFoto(ImageIO.read(imageBlobStream));
                        
                        User u = new User();
                        u.setId(rs.getInt("User.id"));
                        u.setNaam(rs.getString("Naam"));
                        u.setVoornaam(rs.getString("Voornaam"));
                        u.setEmail(rs.getString("email"));
                        f.setAuteur(u);
                        
                        Melding m = new Melding();
                        m.setId(rs.getInt("Melding.id"));
                        m.setType(rs.getString("Type"));
                        m.setBeschrijving(rs.getString("Beschrijving"));
                        
                        Locatie l = new Locatie();
                        l.setLatitude(rs.getDouble("Latitude"));
                        l.setLongitude(rs.getDouble("Longitude"));
                        m.setLocatie(l);
                        f.setMelding(m);
                        
                        results.add(f);*/
                        
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
                       /* Foto f = new Foto();
                        f.setId(rs.getInt("Foto.Id"));
                        Blob Foto = rs.getBlob("Foto");
                        InputStream imageBlobStream = Foto.getBinaryStream();
                        f.setFoto(ImageIO.read(imageBlobStream));
                        
                        User u = new User();
                        u.setId(rs.getInt("User.id"));
                        u.setNaam(rs.getString("Naam"));
                        u.setVoornaam(rs.getString("Voornaam"));
                        u.setEmail(rs.getString("email"));
                        f.setAuteur(u);
                        
                        Melding m = new Melding();
                        m.setId(rs.getInt("Melding.id"));
                        m.setType(rs.getString("Type"));
                        m.setBeschrijving(rs.getString("Beschrijving"));
                        
                        Locatie l = new Locatie();
                        l.setLatitude(rs.getDouble("Latitude"));
                        l.setLongitude(rs.getDouble("Longitude"));
                        m.setLocatie(l);
                        f.setMelding(m);
                        
                        return f;
                        */
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
    
    /**
     *
     * @param meldingId
     * @param f
     * @param uploadedInputStream
     * @param fileDetail
     * @return
     */
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
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("Image is larger than " + MAX_SIZE_IN_MB + "MB").build());
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
    /*
    @Path("{id}/foto/{fid}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateFoto(@PathParam("id") int meldingId,@PathParam("fid") int fotoId,Foto f){
        try (Connection conn = source.getConnection()) {
            if (f.getAuteur() == null) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Een comment moet een auteur hebben.").build());
            }
            try (PreparedStatement stat = conn.prepareStatement("SELECT * FROM foto WHERE ID = ? AND melding = ?")) {
                stat.setInt(1, fotoId);
                stat.setInt(2, meldingId);
                try (ResultSet rs = stat.executeQuery()) {
                    if (!rs.next()) {
                        throw new WebApplicationException(Response.Status.NOT_FOUND);
                    }
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(f.getFoto(), "jpeg", out);
            byte[] buf = out.toByteArray();
            ByteArrayInputStream inStream = new ByteArrayInputStream(buf);
            
            try (PreparedStatement stat = conn.prepareStatement("UPDATE foto SET foto = ?, auteur = ? WHERE ID = ? AND melding = ?")) {
                stat.setBinaryStream(1, inStream);
                stat.setInt(2,f.getAuteur().getId());
                stat.setInt(3, f.getId());
                stat.setInt(4, f.getMelding().getId());
                stat.executeUpdate();
            }
        }catch (SQLException ex) {
            throw new WebApplicationException(ex);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        } 
    }*/
    
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
