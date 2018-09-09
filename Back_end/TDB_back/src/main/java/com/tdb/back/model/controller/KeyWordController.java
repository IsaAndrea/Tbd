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
import com.tdb.back.model.entities.KeyWord;
import com.tdb.back.model.repository.KeyWordRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;




@CrossOrigin
@Controller  
@RequestMapping("/keyword")
public class KeyWordController {
	
	@Autowired
	private KeyWordRepository keywordRepository;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Iterable<KeyWord> getAllKeyWords() {
		return keywordRepository.findAll();
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public  KeyWord findOne(@PathVariable("id") Long id) {
		return keywordRepository.findById(id).get();
    }
    
    @RequestMapping(value = "searchWord/{word}", method = RequestMethod.GET)
	@ResponseBody
	public  Iterable<KeyWord> findWord(@PathVariable("word") String word) {
		return keywordRepository.findByword(word);
	}
			
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public KeyWord create(@RequestBody KeyWord resource) {
	     return keywordRepository.save(resource);
	}

	@RequestMapping(path = "/add/keyword", method = RequestMethod.POST) 
	public @ResponseBody String addKeyWord (
            
            @RequestParam String word) {

			KeyWord keyword = new KeyWord();
			keyword.setWord(word);
			keywordRepository.save(keyword);
			return "key word registrada";
		}

}