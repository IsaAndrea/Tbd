package com.tdb.back.model.controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;
import com.tdb.back.model.entities.User;
import com.tdb.back.model.entities.Country;
import com.tdb.back.model.repository.UserRepository;
import com.tdb.back.model.repository.CountryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;




@CrossOrigin
@RestController  
@RequestMapping("/user")
public class UserController {
	
	@Autowired
    private UserRepository userRepository;
    @Autowired
    private CountryRepository countryRepository;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Iterable<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	@RequestMapping(value = "/{id}", method=RequestMethod.DELETE)
	@ResponseBody
	public HttpStatus deleteUser(@PathVariable("id") Long id) {
		
		userRepository.deleteById(id);
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/{id}", method=RequestMethod.PUT)
	@ResponseBody
	public HttpStatus updateUsuario(@PathVariable("id")Long id,@RequestBody User user) {
		
		User currentUser=userRepository.findById(id).orElse(null);
		if(currentUser==null)return HttpStatus.NO_CONTENT;
		
		currentUser.setName(user.getName());
		currentUser.setPassword(user.getPassword());
		currentUser.setRut(user.getRut());
		currentUser.setCountry(user.getCountry());
		currentUser.setRol(user.getRol());
		currentUser.setType(user.getType());
		userRepository.save(currentUser);
		return HttpStatus.OK;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public  User findOne(@PathVariable("id") Long id) {
		return userRepository.findById(id).get();
    }
    
    @RequestMapping(value = "searchuser/country/{name}", method = RequestMethod.GET)
	@ResponseBody
	public  Iterable<User> findUserByCountry(@PathVariable("name") String name) {
		return userRepository.findBycountry(name);
    }
    @RequestMapping(value = "searchuser/type/{type}", method = RequestMethod.GET)
	@ResponseBody
	public  Iterable<User> findGameByScore(@PathVariable("type") Integer type) {
		return userRepository.findBytype(type);
    }
    @RequestMapping(value = "searchuser/rut/{rut}", method = RequestMethod.GET)
	@ResponseBody
	public  User findGameByRut(@PathVariable("rut") String rut) {
		return userRepository.findByrut(rut);
    }
    
    @RequestMapping(value = "login/{email}/{password}", method = RequestMethod.GET)
	@ResponseBody
	public  User findGameByKeyWord(@PathVariable("password") String password, @PathVariable("email") String email) {
        User user = new User();
        user = userRepository.findByemail(email);
        if(password==user.getPassword()){
            return user;
        }
        else{
            return null;
        }
	}

	@RequestMapping(path = "/add/user", method = RequestMethod.POST) 
	public @ResponseBody String addVideoGame (@RequestBody User user ) {
			userRepository.save(user);
			return "Usuario registrado";
	}

	@RequestMapping(path = "/update/user", method = RequestMethod.POST) 
	public @ResponseBody String updateUser (@RequestBody User user ) {

			User updated_user = userRepository.findById(user.getUser_id()).get();
			updated_user.setName(user.getName());
			updated_user.setPassword(user.getPassword());
			updated_user.setEmail(user.getEmail());
			updated_user.setRut(user.getRut());
			userRepository.save(updated_user);
			return "Usuario actualizado";
	}
	
	@RequestMapping(path = "/auth", method = RequestMethod.POST) 
	@Transactional
	public User auth(@RequestBody String json) throws JSONException {
		
		JSONObject response = new JSONObject(json);
		String email= response.getString("email");
		String password=response.getString("password");
		
		User user=userRepository.findByemail(email);
		
		if(user.equals(null)){

		
			return user;
		}
		else{
			if(!user.getPassword().equals(password)){
				return null;
			}
		else{
			return user;
			}
		}
		
		
	}

}