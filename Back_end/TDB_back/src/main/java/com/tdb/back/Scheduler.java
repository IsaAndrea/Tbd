package com.tdb.back;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tdb.back.Indice.IndiceInvertido;
import com.tdb.back.analisis.Classifier;
import com.tdb.back.model.entities.KeyWord;
import com.tdb.back.model.entities.Plataforma;
import com.tdb.back.model.entities.Score;
import com.tdb.back.model.entities.ScoreCountry;
import com.tdb.back.model.entities.ScoreInfluence;
import com.tdb.back.model.entities.UserInfluyente;
import com.tdb.back.model.entities.VideoGame;
import com.tdb.back.model.repository.PlataformaRepository;
import com.tdb.back.model.repository.VideoGameRepository;
import com.tdb.back.neo4j.GrafoDB;

@Component
public class Scheduler {
	
	@Autowired
	private Classifier classifier;
	
	@Autowired
	private VideoGameRepository videoGameRepository;
	
	@Autowired
	private PlataformaRepository plataformaRepository;
	
	private IndiceInvertido indice= new IndiceInvertido();
	private  GrafoDB grafo=new GrafoDB();
			
	@Scheduled(fixedRate = 18000000)
	@Transactional
	public void realizarAnalisis() {
		System.out.println("Iniciando analisis de sentimientos");
		//this.indice.crear();
		//this.generarAnalisis();
		this.generarAnalisisPaises();
		//this.crearGrafo();
		//this.generarAnalisisByinfluencia();
		//this.generarAnalisisByPlataforma();
		System.out.println("Analisis de sentimiento finalizado");
	}
	
	public void generarAnalisisByPlataforma() {
		
		
		List<Plataforma> plataformas=(List<Plataforma>) plataformaRepository.findAll();
		
		
		for (Plataforma plataforma : plataformas) {
			this.grafo.connect("bolt://138.68.239.45:7687", "neo4j", "secret");
			Double maxFollowers=this.grafo.getMaxFollowers();
			List<Double> followers=new ArrayList<Double>();
			followers.add(0.0);
			List<Double> influencias=new ArrayList<Double>();
			influencias.add(0.0);
			List<String> usuarios=new ArrayList<String>();
			usuarios.add("u1");
			StatementResult games=grafo.getVideoGamesPlataforma(plataforma.getName());
			while(games.hasNext()) {
				Record record=games.next();
				StatementResult users=grafo.getUsuariosByGame(record.get("name").asString(),25);
				
				while(users.hasNext()) {
					Record recordU=users.next();
					String nombre=recordU.get("name").asString();
					int cantidad=Integer.parseInt(String.valueOf(record.get("valoration", 0)));
					//int cantidad=this.grafo.getGamesPlatformTw(nombre, plataforma.getName());
					Double follow=Double.parseDouble(String.valueOf(recordU.get("followers")));
					double influencia=(0.5*follow+0.5*cantidad)/maxFollowers;
					if(usuarios.indexOf(nombre)==-1) {
						for (String usuario: usuarios) {
							int posicion=usuarios.indexOf(usuario);
							
							if(influencia>=influencias.get(posicion)) {
								followers.add(posicion,follow);
								usuarios.add(posicion,nombre);
								influencias.add(posicion, influencia);
								
								if(followers.size()>25) {
									System.out.println(25);
									
									followers.remove(25);
									usuarios.remove(25);
									influencias.remove(25);
								}
								break;
							}
						}			
					}
				}
				
			}
			for(String usuario:usuarios) {
				int posicion=usuarios.indexOf(usuario);
				UserInfluyente user=new UserInfluyente();
				user.setName(usuario);
				user.setFollowers(followers.get(posicion));
				user.setInfluencia(influencias.get(posicion));
				plataforma.getUserInfluyentes().add(user);
				plataformaRepository.save(plataforma);
				
			}
		}
		this.grafo.disconnect();
	}
	
	
	public void crearGrafo() {
		List<VideoGame> videoGames=(List<VideoGame>) videoGameRepository.findAll();
		System.out.println("Inicio creacion del grafo");
		this.grafo.connect("bolt://138.68.239.45:7687", "neo4j", "secret");
		//this.grafo.deleteAll();
		System.out.println("se eliminan grafos");
		this.grafo.relacionJuegoPlataforma(videoGames);
		System.out.println("Me conecto al grafo");
		this.grafo.crearNodoUsuarios();
		this.relacionarUsuariosGame(videoGames);
		System.out.println("Termino la creacion de nodos");
		
	}
	
	public void generarAnalisisByinfluencia() {
		
		List<VideoGame> videoGames=(List<VideoGame>) videoGameRepository.findAll();
		this.grafo.connect("bolt://138.68.239.45:7687", "neo4j", "secret");
		System.out.print("me conecto");
		Double maxFollowers=this.grafo.getMaxFollowers();
		System.out.println("obtengo maxFollowers");
		
		for(VideoGame videoGame:videoGames) {
			
			int actualValoration=videoGame.getScores().get(videoGame.getScores().size()-2).getValoration();
			System.out.println("Creando users");
			StatementResult users=this.grafo.getUsuariosByGame(videoGame.getName());
			
			while(users.hasNext()) {
				
				Record record=users.next();
				Double followers=Double.parseDouble(String.valueOf(record.get("followers")));
				int cantidad=Integer.parseInt(String.valueOf(record.get("valoration", 0)));
				
				double influencia=(0.7*followers+0.3*cantidad)/maxFollowers;
				System.out.println(influencia+"-> "+videoGame.getName());
				actualValoration=(int)((actualValoration)+(influencia*cantidad));
				System.out.println(actualValoration+"-> "+videoGame.getName());
			}
			
			ScoreInfluence score=new ScoreInfluence();
			score.setValoration(actualValoration);
			videoGame.getScoresI().add(score);
			videoGameRepository.save(videoGame);
		}
		
		this.grafo.disconnect();
		
	}
	
	public void relacionarUsuariosGame(List<VideoGame> videoGames) {
		
		StatementResult result=this.grafo.relacionarUsuariosTitulos();
		while(result.hasNext()) {
			
			Record record=result.next();
			String userName=record.get("name").asString();
			for(VideoGame videoGame:videoGames) {
				String consulta=generarNombres(videoGame);
				List<String> tweets=this.indice.buscarPorUsuario(consulta, userName);
				List<Integer> valoration=generarValorcion(tweets);
				if(valoration.get(0)>0) {
					System.out.println("ssddddddd");
					//this.grafo.connect("bolt://localhost:7687", "neo4j", "feseja64");
					this.grafo.crearRealacionUsuarioGame(valoration.get(0), userName,videoGame.getName());
				}
				
			}
		}
		
	}
	
	public void generarAnalisis() {
		
		List<VideoGame> videoGames=(List<VideoGame>) videoGameRepository.findAll();
		//VideoGame video=videoGameRepository.findById((long) 1).orElse(null);
		//ArrayList<VideoGame> videoGames=new ArrayList<VideoGame>();
		//videoGames.add(video);
		for (VideoGame videoGame : videoGames) {
			
			String nombre=generarNombres(videoGame);
			List<String> tweets=generarTweets(nombre);
			List<Integer> valoration=generarValorcion(tweets);
			
			
			Calendar cal= Calendar.getInstance();
			Date date=cal.getTime();
			Timestamp lastUpdate=new Timestamp(date.getTime());
			
			Score score=new Score();
			score.setValoration(valoration.get(0));
			score.setLastUpdate(lastUpdate);
			Score scoreN=new Score();
			scoreN.setValoration(valoration.get(1));
			scoreN.setLastUpdate(lastUpdate);
			System.out.println("Esta es la consulta"+nombre);
			videoGame.getScores().add(score);
			videoGame.getScores().add(scoreN);
			videoGameRepository.save(videoGame);
			
		
		}
		
	}
	
	public void generarAnalisisPaises() {
		
		List<VideoGame> videoGames=(List<VideoGame>) videoGameRepository.findAll();
		
		for (VideoGame videoGame : videoGames) {
			
			if(videoGame.getCategory()==0) {
				String nombre=generarNombres(videoGame);
				generarValoracionPais(nombre,"Argentina",1,videoGame);
				generarValoracionPais(nombre,"Bolivia",2,videoGame);
				generarValoracionPais(nombre,"Brazil",3,videoGame);
				generarValoracionPais(nombre,"Chile",4,videoGame);
				generarValoracionPais(nombre,"Colombia",5,videoGame);
				generarValoracionPais(nombre,"Ecuador",6,videoGame);
				generarValoracionPais(nombre,"Falkland Islands",8,videoGame);
				generarValoracionPais(nombre,"French Guiana",9,videoGame);
				generarValoracionPais(nombre,"Guyana",10,videoGame);
				generarValoracionPais(nombre,"Peru",11,videoGame);
				generarValoracionPais(nombre,"Paraguay",12,videoGame);
				generarValoracionPais(nombre,"Suriname",13,videoGame);
				generarValoracionPais(nombre,"Uruguay",14,videoGame);
				generarValoracionPais(nombre,"Venezuela",15,videoGame);
				
				
			}
			
		}
		System.out.println("Fin de analisis pais");
	}
	
	
	public String generarNombres(VideoGame videoGame){
			List<KeyWord> keyWords=videoGame.getKeyWords();
			String acumulador="(AJSJSLKSE3NSDSUNSW)";
			for (KeyWord keyWord : keyWords) {		
				acumulador=acumulador + " OR "+"("+ keyWord.getWord()+")";
		}
		return acumulador;
	}
	
	
	public List<String> generarTweets(String nombre){
		
		List<String> tweets=this.indice.buscar(nombre);
		return tweets;
		
	}
	
	
	public List<Integer> generarValorcion(List<String> tweets) {
		HashMap<String, Double> valoraciones;
		List<Integer> resultados=new ArrayList<Integer>();
		int score=0;
		int scoreN=0;
		
		for (String tweet : tweets) {
			valoraciones=classifier.classify(tweet);
			if(valoraciones.get("positive") > valoraciones.get("negative")) {
				score=score+1;
			}
			else if(valoraciones.get("negative")>valoraciones.get("positive")) {
				scoreN=scoreN+1;
			}
		}
		resultados.add(score);
		resultados.add(scoreN);
		return resultados;
		
	}
	
	public void generarValoracionPais(String consulta,String pais,int tipo,VideoGame videoGame) {
		
		List<String> tweets=this.indice.buscarPorPais(consulta, pais);
		List<Integer> valoration=generarValorcion(tweets);
		
		Calendar cal= Calendar.getInstance();
		Date date=cal.getTime();
		Timestamp lastUpdate=new Timestamp(date.getTime());
		
		ScoreCountry scoreC=new ScoreCountry();
		scoreC.setValoration(valoration.get(0));
		scoreC.setLastUpdate(lastUpdate);
		scoreC.setPais(pais);
		scoreC.setId(tipo);
		scoreC.setTipo(0);
		ScoreCountry scoreN=new ScoreCountry();
		scoreN.setValoration(valoration.get(1));
		scoreN.setLastUpdate(lastUpdate);
		scoreN.setPais(pais);
		scoreN.setId(tipo);
		scoreN.setTipo(1);
		//System.out.println("Esta es la consulta"+nombre);
		videoGame.getScoresC().add(scoreC);
		videoGame.getScoresC().add(scoreN);
		videoGameRepository.save(videoGame);
		
	}

}
