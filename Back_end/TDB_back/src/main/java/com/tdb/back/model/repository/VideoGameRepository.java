package com.tdb.back.model.repository;

import org.springframework.data.repository.CrudRepository;

import com.tdb.back.model.entities.VideoGame;
import com.tdb.back.model.entities.Score;
import com.tdb.back.model.entities.KeyWord;
import com.tdb.back.model.entities.Plataforma;
import java.util.List;





public interface VideoGameRepository extends CrudRepository<VideoGame,Long> {
    Iterable<VideoGame> findByname(String name);
    Iterable<VideoGame> findBycategory(int category);
    Iterable<VideoGame> findByscores(Score score);
   Iterable<VideoGame> findBykeyWords(KeyWord keyWords);
   List<VideoGame> findByplataformas(List<Plataforma> plataformas);

}
