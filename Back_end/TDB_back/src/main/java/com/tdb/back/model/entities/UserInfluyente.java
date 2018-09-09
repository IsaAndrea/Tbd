package com.tdb.back.model.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="users_influyentes")
public class UserInfluyente  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long useri_id;
	
	private String name;
	
	private Double followers;
	
	private Double influencia;

	public Long getUseri_id() {
		return useri_id;
	}

	public void setUseri_id(Long useri_id) {
		this.useri_id = useri_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getFollowers() {
		return followers;
	}

	public void setFollowers(Double followers) {
		this.followers = followers;
	}

	public Double getInfluencia() {
		return influencia;
	}

	public void setInfluencia(Double influencia) {
		this.influencia = influencia;
	}
	
	

}
