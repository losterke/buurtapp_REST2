/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domein;

import java.sql.Date;
import java.util.List;

/**
 *
 * @author tim
 */
public class Event {
    
    private int id;
    private String naam;
    private String info;
    private Locatie locatie;
    private Date start;
    private Date end;
    private User auteur;
    private List<User> deelnemers;
   

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaam() {
        return naam;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public Locatie getLocatie() {
        return locatie;
    }

    public void setLocatie(Locatie locatie) {
        this.locatie = locatie;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public User getAuteur() {
        return auteur;
    }

    public void setAuteur(User auteur) {
        this.auteur = auteur;
    }

    public List<User> getDeelnemers() {
        return deelnemers;
    }

    public void setDeelnemers(List<User> deelnemers) {
        this.deelnemers = deelnemers;
    }  
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Event other = (Event) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    } 

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.id;
        return hash;
    }
}
