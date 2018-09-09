package com.tdb.back.model.repository;

import org.springframework.data.repository.CrudRepository;

import com.tdb.back.model.entities.Plataforma;
import java.util.List;



public interface PlataformaRepository extends CrudRepository<Plataforma,Long> {
    List<Plataforma> findByname(String name);
}