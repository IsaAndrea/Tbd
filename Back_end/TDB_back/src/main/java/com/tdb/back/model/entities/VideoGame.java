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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.hibernate.engine.internal.Cascade;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name="video_games")
public class VideoGame implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long game_id;
	
	@NotEmpty
	private String name;
	
	//Se refiere a si ya fue lanzado, o saldra en un futuro
	// 1= saldra en el futuro, 0= ya fue lanzado
	@NotNull
	private int category;
	
	@Column(name = "release_at")
	@Temporal(TemporalType.DATE)
	private Date release;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="game_id")
	private List<Score> scores;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="game_id")
	private List<ScoreCountry> scoresC;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="game_id")
	private List<ScoreInfluence> scoresI;
	
	@ManyToMany(fetch=FetchType.LAZY,mappedBy="videoGames")
	@JsonIgnore
	private List<Plataforma> plataformas;
	
	/*Corresponde a los diferentes nombres que tiene un titulo*/
	/*Por ejemplo god of war tambien se puede encontrar como "gow"*/
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="game_id")
	private List<KeyWord> keyWords;
	
	public VideoGame() {
		
		this.scores=new ArrayList<Score>();
		this.scoresC=new ArrayList<ScoreCountry>();
		this.scoresI=new ArrayList<ScoreInfluence>();
		this.keyWords= new ArrayList<KeyWord>();
		this.plataformas=new ArrayList<Plataforma>();
	}
	
	public Long getGame_id() {
		return game_id;
	}

	public void setGame_id(Long game_id) {
		this.game_id = game_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public List<Score> getScores() {
		return scores;
	}

	public void setScores(List<Score> scores) {
		this.scores = scores;
	}

	public List<KeyWord> getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(List<KeyWord> keyWords) {
		this.keyWords = keyWords;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public Date getRelease() {
		return release;
	}

	public void setRelease(Date release) {
		this.release = release;
	}

	public List<ScoreCountry> getScoresC() {
		return scoresC;
	}

	public void setScoresC(List<ScoreCountry> scoresC) {
		this.scoresC = scoresC;
	}

	public List<Plataforma> getPlataformas() {
		return plataformas;
	}

	public void setPlataformas(List<Plataforma> plataformas) {
		this.plataformas = plataformas;
	}

	public List<ScoreInfluence> getScoresI() {
		return scoresI;
	}

	public void setScoresI(List<ScoreInfluence> scoresI) {
		this.scoresI = scoresI;
	}
	
	
	
	
	

}
