package com.example.urlshortener.services;

import com.example.urlshortener.models.Url;
import com.example.urlshortener.repositories.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setup() {
        // No-op, injection via @InjectMocks
    }

    @Test
    @DisplayName("shortenUrl should save Url with original and generated short url and return persisted entity")
    void shortenUrl_savesAndReturns() {
        // Arrange
        String original = "https://example.com/some/path";
        // Echo back the saved entity as persisted (id behavior is not relevant here)
        when(urlRepository.save(any(Url.class))).thenAnswer(inv -> inv.getArgument(0, Url.class));

        // Act
        Url result = urlService.shortenUrl(original);

        // Assert
        ArgumentCaptor<Url> captor = ArgumentCaptor.forClass(Url.class);
        verify(urlRepository).save(captor.capture());
        Url saved = captor.getValue();

        assertAll(
                () -> assertThat(saved.getOriginalUrl()).isEqualTo(original),
                () -> assertThat(saved.getShortUrl()).isNotNull().isNotBlank(),
                () -> assertThat(saved.getShortUrl().length()).isEqualTo(6),
                () -> assertThat(result).isSameAs(saved)
        );
    }

    @Test
    @DisplayName("shortenUrl should generate alphanumeric short url from allowed characters only")
    void shortenUrl_generatesFromAllowedAlphabet() {
        // Arrange
        String original = "https://allowed.com";
        when(urlRepository.save(any(Url.class))).thenAnswer(inv -> inv.getArgument(0, Url.class));

        // Act
        Url result = urlService.shortenUrl(original);

        // Assert
        String shortUrl = result.getShortUrl();
        assertThat(shortUrl).matches("[a-zA-Z0-9]{6}");
    }

    @Test
    @DisplayName("shortenUrl should produce different short codes across multiple invocations with high probability")
    void shortenUrl_probabilisticUniqueness() {
        // Arrange
        when(urlRepository.save(any(Url.class))).thenAnswer(inv -> inv.getArgument(0, Url.class));

        // Act
        Url a = urlService.shortenUrl("https://a.com");
        Url b = urlService.shortenUrl("https://b.com");

        // Assert (not strictly guaranteed, but extremely likely with Random)
        assertThat(a.getShortUrl()).isNotEqualTo(b.getShortUrl());
    }

    @Test
    @DisplayName("getOriginalUrl should delegate to repository and return value when found")
    void getOriginalUrl_found() {
        // Arrange
        Url stored = new Url("https://found.com", "abc123");
        when(urlRepository.findByShortUrl("abc123")).thenReturn(Optional.of(stored));

        // Act
        Optional<Url> result = urlService.getOriginalUrl("abc123");

        // Assert
        assertThat(result).isPresent().contains(stored);
        verify(urlRepository).findByShortUrl("abc123");
    }

    @Test
    @DisplayName("getOriginalUrl should return empty when repository finds nothing")
    void getOriginalUrl_notFound() {
        // Arrange
        when(urlRepository.findByShortUrl("zzz999")).thenReturn(Optional.empty());

        // Act
        Optional<Url> result = urlService.getOriginalUrl("zzz999");

        // Assert
        assertThat(result).isEmpty();
        verify(urlRepository).findByShortUrl("zzz999");
    }
}
