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
import com.tdb.back.model.entities.Country;
import com.tdb.back.model.repository.CountryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;




@CrossOrigin
@Controller  
@RequestMapping("/country")
public class CountryController {
	
	@Autowired
	private CountryRepository countryRepository;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Iterable<Country> getAllCountries() {
		return countryRepository.findAll();
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public  Country findOne(@PathVariable("id") Long id) {
		return countryRepository.findById(id).get();
    }
    
    @RequestMapping(value = "searchCountry/name/{name}", method = RequestMethod.GET)
	@ResponseBody
	public  Iterable<Country> findCountry(@PathVariable("name") String name) {
		return countryRepository.findByname(name);
	}
    
    @RequestMapping(value = "searchCountry/continent/{continent}", method = RequestMethod.GET)
	@ResponseBody
	public  Iterable<Country> findCountryByContinent(@PathVariable("continent") String continent) {
		return countryRepository.findBycontinent(continent);
	}
    
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public Country create(@RequestBody Country resource) {
	     return countryRepository.save(resource);
	}

	@RequestMapping(path = "/add/country", method = RequestMethod.POST) 
	public @ResponseBody String addKeyWord (
            @RequestParam String name,
            @RequestParam String continent) {

			Country country = new Country();
            country.setContinent(continent);
            country.setName(name);
			countryRepository.save(country);
			return "country registrado";
		}

}