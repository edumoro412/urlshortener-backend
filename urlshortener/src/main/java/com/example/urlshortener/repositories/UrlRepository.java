package com.example.urlshortener.repositories;

import com.example.urlshortener.models.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UrlRepository extends JpaRepository<Url, Long>{ //Esto le dice a Spring oye este repositorio maneja objectos de Url y la clave primaria es de tipo Long
    //Con añadir eso ya tienes el save() para guardar, el findBy() el findAll(), el delete(). y el que acabamos de añadir el findByShortUrl()
    Optional<Url> findByShortUrl(String shortUrl);
    
}
