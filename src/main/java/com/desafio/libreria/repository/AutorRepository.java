package com.desafio.libreria.repository;

import com.desafio.libreria.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;


public interface AutorRepository extends JpaRepository<Autor,Long> {
    List<Autor> findAll();
    List<Autor> findByNombreContainingIgnoreCase(String nombre);
    @Query("SELECT a FROM Autor a WHERE a.nombre ILIKE %:nombre%")
    Optional<Autor> findByAutor(String nombre);
    @Query("SELECT a FROM Autor a WHERE :anio >= a.fechaNac AND (:anio <= a.fechaMuerte  OR a.fechaMuerte IS NULL)")
    List<Autor> buscarPorAnio(int anio);



}
