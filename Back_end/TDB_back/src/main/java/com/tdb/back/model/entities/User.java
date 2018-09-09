package com.tdb.back.model.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import com.tdb.back.model.entities.Country;
import org.hibernate.engine.internal.Cascade;


@Entity
@Table(name="user")
public class User implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long user_id;
	@NotEmpty
	private String name;
    @NotEmpty
	private String email;
    @NotEmpty
    private String country;
    @NotEmpty
	private String password;
	@NotEmpty
	private String rut;
    //Empresa retail 0 (ripley, paris, etc) o exclusiva videojuegos 1 (microplay, weplay, zmart)
    
	private Integer type;
	
	//0 es una empresa, 1, es administrador
	private int rol;

	
	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getCountry() {
		return country;
	}

	public void setCountries(String country) {
		this.country = country;
	}

	public String getRut() {
		return rut;
	}

	public void setRut(String rut) {
		this.rut = rut;
	}
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
    }
    
    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getRol() {
		return rol;
	}

	public void setRol(int rol) {
		this.rol = rol;
	}
	
	
	
	
	
	

}
