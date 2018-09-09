package com.tdb.back.model.controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Collection;
import java.util.Comparator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.stereotype.Controller;
import com.tdb.back.model.entities.VideoGame;
import com.tdb.back.model.entities.Score;
import com.tdb.back.model.entities.KeyWord;
import com.tdb.back.model.entities.Plataforma;
import com.tdb.back.model.repository.VideoGameRepository;
import com.tdb.back.neo4j.GrafoDB;
import com.tdb.back.model.repository.KeyWordRepository;
import com.tdb.back.model.repository.ScoreRepository;
import com.tdb.back.model.repository.PlataformaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;




@CrossOrigin
@Controller  
@RequestMapping("/videogame")
public class VideoGameController {
	
	@Autowired
    private VideoGameRepository videogameRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
	private KeyWordRepository keywordRepository;
	@Autowired
	private PlataformaRepository plataformaRepository;

	@GetMapping("/listar")
	@ResponseBody
	@CrossOrigin
	public List<VideoGame> getAllVideoGame() {
		List<VideoGame> videoGames= (List<VideoGame>) videogameRepository.findAll();
		
		for (VideoGame videoGame : videoGames) {
			
			videoGame.getScores().sort(Comparator.comparing(Score::getScore_id));
		}
		
		return videoGames;
	}
	@RequestMapping(value="/delete/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public HttpStatus deleteVideogame(@PathVariable("id") Long id){
		videogameRepository.deleteById(id);
		return HttpStatus.OK;

	}
	
	
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public  VideoGame findOne(@PathVariable("id") Long id) {
		return videogameRepository.findById(id).get();
    }
    
    @RequestMapping(value = "/searchvideogame/name/{name}", method = RequestMethod.GET)
	@ResponseBody
	public  Iterable<VideoGame> findGameByName(@PathVariable("name") String name) {
		return videogameRepository.findByname(name);
    }
    @RequestMapping(value = "/searchvideogame/score/{score_id}", method = RequestMethod.GET)
	@ResponseBody
	public  Iterable<VideoGame> findGameByScore(@PathVariable("score_id") Long score_id) {
        Score score = new Score();
        score = scoreRepository.findById(score_id).get();
		return videogameRepository.findByscores(score);
    }
    
    @RequestMapping(value = "/searchvideogame/platform/{platformName}", method = RequestMethod.GET)
	@ResponseBody
	public  List<VideoGame> findGameByPlatform(@PathVariable("platformName") String platformName) {
        List<Plataforma> plt = new ArrayList<Plataforma>();
        plt = plataformaRepository.findByname(platformName);
		List<VideoGame> gms = new ArrayList<VideoGame>();
		gms = videogameRepository.findByplataformas(plt);
		List<VideoGame> retorno = new ArrayList<VideoGame>();
		int len = gms.size();
		int i = 0;
		int mayor = 0;
		int indice = 0;
		int j = 0;
		int lenScores;
		int [] score = new int [len];
		for (VideoGame game: gms){
			lenScores = game.getScores().size();
			score[i] = game.getScores().get(lenScores-2).getValoration();
			i++;
		}
		i = 0;
		while (j < len){
			for(i = 0;i < len ;i++){
				if(mayor < score[i]){
					mayor = score[i];
					indice = i;
				}
			}
			score[indice] = -1;
			mayor = 0;
			retorno.add(gms.get(indice));
			j++;
		}
		return retorno; 
    }

    @RequestMapping(value = "/searchvideogame/keyword/{keyword_id}", method = RequestMethod.GET)
	@ResponseBody
	public  Iterable<VideoGame> findGameByKeyWord(@PathVariable("keyword_id") Long keyword_id) {
        KeyWord keyword = new KeyWord();
        keyword = keywordRepository.findById(keyword_id).get();
		return videogameRepository.findBykeyWords(keyword);
	}
    
    @RequestMapping(value = "/searchvideogame/category/{category}", method = RequestMethod.GET)
	@ResponseBody
	public  Iterable<VideoGame> findGameByCategory(@PathVariable("category") Integer category) {
		return videogameRepository.findBycategory(category);
	}
    
	@RequestMapping(method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public VideoGame create(@RequestBody VideoGame resource) {
	     return videogameRepository.save(resource);
	}

	@RequestMapping(path = "/add/videogame", method = RequestMethod.POST) 
	public @ResponseBody String addVideoGame (
            @RequestParam List<Score> scores,
            @RequestParam List<KeyWord> keywords,
            @RequestParam Integer category,
            @RequestParam String name,
            @RequestParam Date release) {

		
			
			VideoGame videogame = new VideoGame();
            videogame.setName(name);
            videogame.setCategory(category);
            videogame.setScores(scores);
            videogame.setKeyWords(keywords);
            videogame.setRelease(release);
			videogameRepository.save(videogame);
			return "VideoGame registrada";
		}
	
	@CrossOrigin
	@ResponseBody
	@GetMapping("/influyentes/plataformas")
	public List<Plataforma> usuariosInfluyentesByPlataforma(){
		return (List<Plataforma>) plataformaRepository.findAll();
	}
	
	@RequestMapping(path = "/add/videogame2", method = {RequestMethod.POST, RequestMethod.GET}) 
	public @ResponseBody String addVideoGame2 (
            @RequestParam Long[] plataformas,
            @RequestParam Integer category,
            @RequestParam String name,
            @RequestParam Date release,
            @RequestParam String[] keywords) {

			List<Plataforma> plt = new ArrayList<Plataforma>();
			int largoLista = plataformas.length;
			int numKeyWords = keywords.length;
			int i = 0;
			VideoGame videogame = new VideoGame();
			Long id = videogame.getGame_id();
            videogame.setName(name);
            videogame.setCategory(category);
            List<KeyWord> kwd = new ArrayList<KeyWord>();

            for(i=0;i<numKeyWords;i++){
            	KeyWord keyword = new KeyWord();
            	keyword.setWord(keywords[i]);
            	keywordRepository.save(keyword);
            	kwd.add(keyword);

            }
            for(i=0;i<largoLista;i++){
				plt.add(plataformaRepository.findById(plataformas[i]).get());
			}
            videogame.setPlataformas(plt);
            videogame.setRelease(release);
            videogame.setKeyWords(kwd);
			videogameRepository.save(videogame);
			for(i=0;i<largoLista;i++){
				Plataforma plat = plataformaRepository.findById(plataformas[i]).get();
				List<VideoGame> vdg = new ArrayList<VideoGame>();
				vdg = plat.getVideoGames();
				vdg.add(videogame);
				plat.setVideoGames(vdg);
				plataformaRepository.save(plat);
			}
			return "VideoGame registrada";
		}
	@RequestMapping(path = "/add/videogame4", method = RequestMethod.POST) 
	public @ResponseBody String addVideoGame4 (
            @RequestParam Integer category,
            @RequestParam String name,
            @RequestParam Date release) {
		
			VideoGame videogame = new VideoGame();
            videogame.setName(name);
            videogame.setCategory(category);
            videogame.setRelease(release);
			videogameRepository.save(videogame);
			return "VideoGame registrada";
		}
	@RequestMapping(path = "/add/videogame3", method = RequestMethod.POST) 
	public @ResponseBody String addVideoGame3 (@RequestBody VideoGame videogame ) {
			videogameRepository.save(videogame);
			return "Juego Registrado";
	}

}