package com.example.urlshortener.services;

import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.example.urlshortener.models.Url;
import com.example.urlshortener.repositories.UrlRepository;

@Service //Indica que es un servicio
public class UrlService {
    private final UrlRepository urlRepository; //Su función es ser la puerta de acceso a la base de datos (guardar, buscar, borrar, traer...)
    private final Random random = new Random();
    private final String CHARACTERS = "abcdefghijklmnopqrstuvwxzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public UrlService(UrlRepository urlRepository){
        this.urlRepository = urlRepository;
    }

    public Url shortenUrl(String originalUrl){ //Aqui genera guarda la url original, y la generada de manera aleatoria.
        String shortUrl = generateShortUrl();
        Url url = new Url(originalUrl, shortUrl);
        return urlRepository.save(url);
    }

    public Optional<Url> getOriginalUrl(String shortUrl){ //Esto es para obtener la url original mediante la acortada
        return urlRepository.findByShortUrl(shortUrl);
    }

    private String generateShortUrl(){//Esto genera la url corta
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<6; i++){
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
