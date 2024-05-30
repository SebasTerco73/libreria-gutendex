package com.desafio.libreria.repository;

import com.desafio.libreria.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


//Long es por la ID de Libro
public interface LibroRepository extends JpaRepository<Libro,Long>{
    List<Libro> findAll();
    List<Libro> findTop5ByOrderByDescargasDesc();
    List<Libro> findByTituloContainingIgnoreCase(String titulo);
    List<Libro> findByIdiomaContainingIgnoreCase(String idioma);
}
