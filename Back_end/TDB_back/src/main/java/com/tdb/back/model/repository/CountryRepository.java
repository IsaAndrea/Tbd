package com.tdb.back.model.repository;

import org.springframework.data.repository.CrudRepository;

import com.tdb.back.model.entities.Country;



public interface CountryRepository extends CrudRepository<Country,Long> {
    Iterable<Country> findByname(String name);
    Iterable<Country> findBycontinent(String continent);
}