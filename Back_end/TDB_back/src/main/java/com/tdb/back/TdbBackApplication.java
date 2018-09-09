package com.tdb.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.tdb.back.Indice.IndiceInvertido;

@SpringBootApplication
@EnableScheduling 
public class TdbBackApplication {

	public static void main(String[] args) {
	
		
		SpringApplication.run(TdbBackApplication.class, args);
		
	}
}
