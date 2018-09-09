package com.tdb.back.model.controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.stereotype.Controller;
import com.tdb.back.model.entities.Score;
import com.tdb.back.model.repository.ScoreRepository;




@CrossOrigin
@Controller  
@RequestMapping("/score")
public class ScoreController {
	
	@Autowired
	private ScoreRepository scoreRepository;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Iterable<Score> getAllScores() {
		return scoreRepository.findAll();
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public  Score findOne(@PathVariable("id") Long id) {
		return scoreRepository.findById(id).get();
    }
    
    @RequestMapping(value = "searchScore/{score}", method = RequestMethod.GET)
	@ResponseBody
	public  Iterable<Score> findWord(@PathVariable("score") Integer score) {
		return scoreRepository.findByvaloration(score);
	}
			
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public Score create(@RequestBody Score resource) {
	     return scoreRepository.save(resource);
	}
	 
}