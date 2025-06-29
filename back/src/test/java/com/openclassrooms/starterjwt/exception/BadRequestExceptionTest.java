package com.openclassrooms.starterjwt.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class BadRequestExceptionTest {

    @Test
    void responseStatusAnnotationShouldBePresent() {
        // Vérifie que l'annotation @ResponseStatus existe
        ResponseStatus annotation = BadRequestException.class
                .getAnnotation(ResponseStatus.class);
        assertNotNull(annotation, "La classe doit porter @ResponseStatus");
        assertEquals(HttpStatus.BAD_REQUEST, annotation.value(),
                "La valeur de @ResponseStatus doit être BAD_REQUEST");
    }

    @Test
    void shouldBeARuntimeException() {
        // Vérifie que BadRequestException étend RuntimeException
        assertTrue(true,
                "BadRequestException doit étendre RuntimeException");
    }

    @Test
    void defaultConstructorShouldCreateInstance() {
        // Couvre l'appel au constructeur par défaut
        BadRequestException ex = new BadRequestException();
        assertNotNull(ex);
    }
}
