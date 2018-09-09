package com.tdb.back.neo4j;

import java.util.Arrays;
import java.util.List;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.tdb.back.Indice.IndiceInvertido;
import com.tdb.back.model.entities.Plataforma;
import com.tdb.back.model.entities.VideoGame;
import com.tdb.back.model.repository.VideoGameRepository;

public class GrafoDB {
	
	private Driver driver;
	private Session session;
	
	@Autowired
	private VideoGameRepository videoGameRepository;
	
	public void connect(String uri, String username, String password)
	{
	/*
	uriConnection = bolt://localhost
	username = neo4j
	password = root -> cambiar contraseña si usaron otra.
	*/
	this.driver = GraphDatabase.driver( uri, AuthTokens.basic( username, password ) );
	this.session = driver.session();
	}
	 public void disconnect() {
	        session.close();
	        driver.close();
	    }
	
	public void deleteAll() {
        this.session.run("match (a)-[r]->(b) delete r");
        this.session.run("match (n) delete n");
    }
	
	public void crearNodoUsuarios(){
		System.out.println("Entro aqui");
		MongoClient mongoo= new MongoClient(new MongoClientURI("mongodb://kimbo:feseja64@206.189.184.79:23305/twitter"));
		DB db= mongoo.getDB("twitter");
		DBCollection col=db.getCollection("statusJSONImpl");


        DBObject group = new BasicDBObject("$group", new BasicDBObject("_id", "$name")
                .append("seguidores", new BasicDBObject("$avg", "$followers")));

        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("seguidores", -1));
        DBObject limit= new BasicDBObject("$limit",100);
        AggregationOutput output = col.aggregate(group,sort,limit);
        int cantidad =output.hashCode();
        int i=0;
        for (DBObject result : output.results()) {
//            System.out.println(result);
            i++;
            session.run("create (a:User {name:'"+limpiar(result.get("_id").toString())+"', followers:"+result.get("seguidores")+"})");
        }
//

        System.out.println("Usuarios agregados");
        mongoo.close();
    }
	
    public String limpiar(String nombre){
        nombre=nombre.replace("'","");
        nombre=nombre.replace("/","");
        nombre=nombre.replace("\"","");
        nombre=nombre.replace("_","");
        nombre=nombre.replace("¯(ツ)¯","");
        nombre=nombre.replace("|","");
        nombre=nombre.replace("°","");
        nombre=nombre.replace("¬","");
        nombre=nombre.replace("!","");
        nombre=nombre.replace("#","");
        nombre=nombre.replace("$","");
        nombre=nombre.replace("%","");
        nombre=nombre.replace("&","");
        nombre=nombre.replace("/","");
        nombre=nombre.replace("(","");
        nombre=nombre.replace(")","");
        nombre=nombre.replace("=","");
        nombre=nombre.replace("?","");
        nombre=nombre.replace("\\","");
        nombre=nombre.replace("¡","");
        nombre=nombre.replace("¿","");
        nombre=nombre.replace("@","");
        nombre=nombre.replace("*","");
        nombre=nombre.replace("+","");
        nombre=nombre.replace("~","");
        nombre=nombre.replace("{","");
        nombre=nombre.replace("}","");
        nombre=nombre.replace("[","");
        nombre=nombre.replace("]","");
        nombre=nombre.replace(";","");
        nombre=nombre.replace(",","");
        nombre=nombre.replace(":","");
        nombre=nombre.replace(".","");
        nombre=nombre.replace("_","");
        nombre=nombre.replace("-","");
        nombre=nombre.replace("AND","(and)");
        if(nombre.equals("AND Noticias")){
            nombre=nombre.replace("AND","aanndd");
        }
        return nombre;
    }
    
    public void relacionJuegoPlataforma(List<VideoGame> videoGames) {
    	System.out.println("Entro a crear relaciones");
    	for (VideoGame videoGame : videoGames) {
    		
    		for(Plataforma plataforma: videoGame.getPlataformas()) {
    	
    			this.session.run("MATCH (a: Plataforma),(b:VideoGame)"
    					+ " WHERE a.name ='"+plataforma.getName()+ "' AND b.name ='"+videoGame.getName()
    					+ "' CREATE (a)-[r:POSEE]->(b)");
    			
    			this.session.run("MATCH (a: VideoGame),(b:Plataforma)"
    					+ " WHERE a.name ='"+videoGame.getName()+ "' AND b.name ='"+plataforma.getName()
    					+ "' CREATE (a)-[r:PERTENECE]->(b)");
    		}
			
		}
    	
    }
    
    public StatementResult getVideoGamesPlataforma(String name) {
    	//System.out.println("entro a gamesPlataforma");
    	
    	StatementResult result=session.run("MATCH (p:Plataforma{name:'"+name+"'})-[POSEE]-(video) return video.name as name");
    	return result;
    }
    
    public StatementResult getUsuariosByGame(String game,Integer k) {
    	//System.out.println("entro a usuariosBygame");
    	String consulta="MATCH (v:VideoGame {name:'"+game+"' })<-[r:TWITTEA]-(user:User) return r.valoration as valoration,user.name as name, user.followers as followers ORDER BY user.followers "
    			+ "DESC LIMIT "+k;
    	StatementResult result=session.run(consulta);
    	
   /* 	while(result.hasNext()) {
    		Record record=result.next();
    		System.out.println(record.get("followers"));
    	}*/
    	return result;
    }
    
    public StatementResult getUsuariosByGame(String game) {
    	//System.out.println("entro a usuariosBygame");
    	String consulta="MATCH (v:VideoGame {name:'"+game+"' })<-[r:TWITTEA]-(user:User) return r.valoration as valoration,user.name as name, user.followers as followers "
    			+ "ORDER BY user.followers ";
    	StatementResult result=session.run(consulta);
    	
   /* 	while(result.hasNext()) {
    		Record record=result.next();
    		System.out.println(record.get("followers"));
    	}*/
    	return result;
    }
    
    
    public StatementResult relacionarUsuariosTitulos() {
    	
    	IndiceInvertido indice= new IndiceInvertido();
    	StatementResult result = session.run( "MATCH (u:User) return u.name as name");
    	return result;
    }
    
    public Double getMaxFollowers() {
    	
    	StatementResult result = session.run( "MATCH (u:User) return u.followers as followers "
    			+ "ORDER BY u.followers DESC LIMIT 1");
    	
    	return Double.parseDouble(String.valueOf(result.next().get("followers")));
    }
    
    public int getGamesPlatformTw(String user,String plataforma) {
    	String consulta="MATCH (u:User{name:'"+user+"'})-[:TWITTEA]-(v:VideoGame) WITH v,u "
    			+ "MATCH (v)-[:PERTENECE]-(p:Plataforma{name:'"+plataforma+"'}) "
    					+ "return count(v) as cantidad";
    	
    	StatementResult result=this.session.run(consulta);
    	return Integer.parseInt(String.valueOf(result.next().get("cantidad")));
    }
   
    
    public void crearRealacionUsuarioGame(int valoration,String userName,String gameName) {
    	System.out.println(userName+gameName);
    	this.session.run("MATCH (u:User),(v:VideoGame) WHERE u.name='"+userName+"' AND v.name='"+gameName+"'"
    			+ " CREATE (u)-[r:TWITTEA {valoration:"+valoration+"}]->(v)");
    	/*this.session.run("MATCH (a: User),(b:VideoGame)"
				+ " WHERE a.name ='"+userName+ "' AND b.name ='"+gameName
				+ "' CREATE (a)-[r:A]->(b)");*/
    	//this.session.run("CREATE (p:Kimbo {tipo:'perro'})");
    	System.out.println("Termina de crear la relacion");
    }
    
    
  

}
