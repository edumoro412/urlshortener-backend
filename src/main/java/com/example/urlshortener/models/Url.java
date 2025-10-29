package com.example.urlshortener.models;
import jakarta.persistence.GeneratedValue; //le dice a JPA que el id se genere autmáticamente al guardar el registro.
import jakarta.persistence.GenerationType; //Significa que la base de datos se encarga de asignar el valor único
import jakarta.persistence.Id; //Indica que el campo id es la clave primaria
import jakarta.persistence.Entity; //Indica que esta clase representa una tabla en la base de datos.



@Entity
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String originalUrl;
    private String shortUrl;

    //Este constructor vacio lo necesita Springboot + JPA si o si.
    public Url(){}

    public Url(String originalUrl, String shortUrl){
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
    }

    public Long getId(){
        return id;
    }

    public String getOriginalUrl(){
        return originalUrl;
    }
    public void setOriginalUrl(String originalUrl){
        this.originalUrl = originalUrl;
    }
    public String getShortUrl(){
        return shortUrl;
    }

    public void setShortUrl(String shortUrl){
        this.shortUrl = shortUrl;
    }
    
}
