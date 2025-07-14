package com.example.biblio.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class Users implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String motDePasse;
    private String email;
    private Long numero;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profil_id")
    private ProfilFormule profilFormule;

    // Constructeur par défaut
    public Users() {}

    // Constructeur sans id (pour création)
    public Users(String userName, String motDePasse, String email, Long numero, ProfilFormule profilFormule) {
        this.userName = userName;
        this.motDePasse = motDePasse;
        this.email = email;
        this.numero = numero;
        this.profilFormule = profilFormule;
    }

    // Constructeur avec id (pour édition)
    public Users(Long id, String userName, String motDePasse, String email, Long numero, ProfilFormule profilFormule) {
        this.id = id;
        this.userName = userName;
        this.motDePasse = motDePasse;
        this.email = email;
        this.numero = numero;
        this.profilFormule = profilFormule;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public ProfilFormule getProfilFormule() {
        return profilFormule;
    }

    public void setProfilFormule(ProfilFormule profilFormule) {
        this.profilFormule = profilFormule;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", motDePasse='[PROTÉGÉ]'" +
                ", email='" + email + '\'' +
                ", numero=" + numero +
                ", profilFormule=" + profilFormule +
                '}';
    }
}