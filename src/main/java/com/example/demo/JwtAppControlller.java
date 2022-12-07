package com.example.demo;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class JwtAppControlller {

	@Autowired
	LoginRepository loginRepo;
	@GetMapping("/")
	public String home() {
		return "Home";
	}

	@PostMapping("/authenticate")
	public ResponseEntity doAuthenticate(@RequestBody Login login) {
		try {
			Login l= loginRepo.findById(login.getUserId()).get();		
			if(l.getPassword().equals(login.getPassword())) {
				String token=JWT.create().withSubject(l.getUserId())
						.withIssuedAt(new Date(System.currentTimeMillis()))
						.withExpiresAt(new Date(System.currentTimeMillis()+1000*60*10))
						.sign(Algorithm.HMAC256("glob"));
				HttpHeaders headers=new HttpHeaders();
				headers.set("Authorization", "Bearer "+token);
				
				return new ResponseEntity("true",headers,HttpStatus.OK);
				
			}
			else {
				return new ResponseEntity("false",HttpStatus.FORBIDDEN);
			}
			
		}catch(Exception e) {
			return new ResponseEntity("false",HttpStatus.NOT_FOUND);
			
		}			
		
	}
	
	@GetMapping("validate")
	public ResponseEntity doValidate(@RequestHeader(name="Authorization") String authToken) {
		String token=authToken.substring("Bearer ".length());
		
		try {
			Algorithm alg=Algorithm.HMAC256("glob");
			JWTVerifier verifier=JWT.require(alg).build();
			
			DecodedJWT decode=verifier.verify(token);
			
			String userId=decode.getSubject();
			
			return new ResponseEntity(userId,HttpStatus.OK);
		}
		catch(Exception e) {
			return new ResponseEntity("",HttpStatus.BAD_REQUEST);
		}
		
		
		
	}
	
	
		
}
