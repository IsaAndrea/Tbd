package com.tdb.back.model.repository;

import org.springframework.data.repository.CrudRepository;

import com.tdb.back.model.entities.KeyWord;



public interface KeyWordRepository extends CrudRepository<KeyWord,Long> {
    Iterable<KeyWord> findByword(String word);
}