package com.tdb.back.model.repository;

import org.springframework.data.repository.CrudRepository;

import com.tdb.back.model.entities.User;
import com.tdb.back.model.entities.Country;



public interface UserRepository extends CrudRepository<User,Long> {
    Iterable<User> findBytype(Integer type);
    Iterable<User> findBycountry(String country);
    User findByrut(String rut);
    User findByemail(String email);

}