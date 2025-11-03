package com.example.urlshortener.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.example.urlshortener.models.Url;
import com.example.urlshortener.services.UrlService;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CorsOrigin;



@CorsOrigin(origins = "*")
@RestController //Indica que esta clase expone endpoints REST (HTTP( GET, POST, etc.))
@RequestMapping("/api") //todas las rutas comienzan con /api
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService){
        this.urlService = urlService;
    }


    @PostMapping("/shorten") //Endpoint que recibe un JSON con la URL larga y devuelve la URL corta, POST  porque creo los datos
    public ResponseEntity<Url> shortenUrl(@RequestBody Url request) { //El @RequestBody lo convierte automáticamente en JSON, el ResponseEntity es un envoltorio que envuelve tu respuesta HTTP
        Url shortened = urlService.shortenUrl(request.getOriginalUrl());
        return ResponseEntity.ok(shortened);
    }

    @GetMapping("/r/{shortUrl}") //Endpoint que recibe el codigo corto y redirige a la URL original, Get porque obtengo datos
   public RedirectView redirect(@PathVariable String shortUrl){
    Optional<Url> urlOpt = urlService.getOriginalUrl(shortUrl);
    if(urlOpt.isPresent()){return new RedirectView(urlOpt.get().getOriginalUrl());}else{
        return new RedirectView("https://tuapp.com/not-found");
    }
   }
    
    
    
}
