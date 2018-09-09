package com.tdb.back.model.repository;

import org.springframework.data.repository.CrudRepository;

import com.tdb.back.model.entities.Score;



public interface ScoreRepository extends CrudRepository<Score,Long> {
    Iterable<Score> findByvaloration(Integer valoration);
}