package com.tdb.back.model.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="plataformas")
public class Plataforma implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long plataforma_id;
	
	private String name;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name = "plataforma_game",  joinColumns = { @JoinColumn(name = "plataforma_id") },
	inverseJoinColumns = { @JoinColumn(name = "game_id") })
	private List<VideoGame> videoGames;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="plataforma_id")
	private List<UserInfluyente> userInfluyentes;
	
	public Plataforma() {
		this.videoGames=new ArrayList<VideoGame>();
		this.userInfluyentes=new ArrayList<UserInfluyente>();
	}

	public Long getPlataforma_id() {
		return plataforma_id;
	}

	public void setPlataforma_id(Long plataforma_id) {
		this.plataforma_id = plataforma_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<VideoGame> getVideoGames() {
		return videoGames;
	}

	public void setVideoGames(List<VideoGame> videoGames) {
		this.videoGames = videoGames;
	}

	public List<UserInfluyente> getUserInfluyentes() {
		return userInfluyentes;
	}

	public void setUserInfluyentes(List<UserInfluyente> userInfluyentes) {
		this.userInfluyentes = userInfluyentes;
	}
	
	
	
	

}
