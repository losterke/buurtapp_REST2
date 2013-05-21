/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domein;

/**
 *
 * @author tim
 */
public class Melding {
    
    private int id;
    private String type;
    private Locatie locatie;
    private String beschrijving;
    private User auteur;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id > 0) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("Id moet > 0");
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type != null && !type.trim().equals("")) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("Het type van deze melding kan niet leeg zijn.");
        }
    }

    public Locatie getLocatie() {
        return locatie;
    }

    public void setLocatie(Locatie locatie) {
        this.locatie = locatie;
    }


    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        if (beschrijving != null && !beschrijving.trim().equals("")) {
            this.beschrijving = beschrijving;
        } else {
            throw new IllegalArgumentException("De beschrijving van u melding mag niet leeg zijn.");
        }
    }

    public User getAuteur() {
        return auteur;
    }

    public void setAuteur(User auteur) {
        this.auteur = auteur;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Melding other = (Melding) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    } 

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.id;
        return hash;
    }
}
