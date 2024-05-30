package com.desafio.libreria;

import com.desafio.libreria.principal.Principal;
import com.desafio.libreria.repository.AutorRepository;
import com.desafio.libreria.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibreriaApplication implements CommandLineRunner {

    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private AutorRepository autorRepository;
    public static void main(String[] args) {
        SpringApplication.run(LibreriaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Principal p = new Principal(libroRepository,autorRepository);
        p.menuPrincipal();

    }
}
