package com.base.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
 
import com.base.Storage.StorageService;
 
@CrossOrigin(origins = "http://localhost:4200",allowedHeaders="*")
@RestController
@RequestMapping("/api")
public class UploadController {
 
	@Autowired
	StorageService storageService;
	List<String> files = new ArrayList<String>();
 
	@PostMapping("/post")
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
		String message = "";
		try {
			String nomFichier=(Math.random()*5000000)+file.getOriginalFilename();
			storageService.store(file,nomFichier);
			files.add(file.getOriginalFilename());
			message = "You successfully uploaded " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} 
		catch (Exception e) {
			message = "FAIL to upload " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		}
	}
	/*
	public String uploadPhoto(MultipartFile file) {
		try {
			String nomFichier=(Math.random()*5000000)+file.getOriginalFilename();
			storageService.store(file,nomFichier);
			files.add(file.getOriginalFilename());
			return nomFichier;
		} 
		catch (Exception e) {
			//message = "FAIL to upload " + file.getOriginalFilename() + "!";
			//return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
			System.out.println("Hum Echec sur l'enregistrement  du fichier passeer");
			return null;
		}
	}
	*/
	@PostMapping("/post/uploadPhoto")
	public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file) {
		//String message = "";
		try {
			String nomFichier=(Math.random()*5000000)+file.getOriginalFilename();
			storageService.store(file,nomFichier);
			files.add(file.getOriginalFilename());
			//message = "You successfully uploaded " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.OK).body(nomFichier);
		} 
		catch (Exception e) {
			//message = "FAIL to upload " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
		}
	}
 
	@GetMapping("/getallfiles")
	public ResponseEntity<List<String>> getListFiles(Model model) {
		System.out.println("Bonjour comment vous allez");
		List<String> fileNames = files
				.stream().map(fileName -> MvcUriComponentsBuilder
						.fromMethodName(UploadController.class, "getFile",fileName ).build().toString())
				.collect(Collectors.toList());
 
		return ResponseEntity.ok().body(fileNames);
	}
 
	@GetMapping("/files/{filename}")
	//@ResponseBody
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		try
		{
		System.out.println("Salut Salut");
		Resource file = storageService.loadFile(filename);
		System.out.println(file.getFilename()+"  "+file.getInputStream()+"  "+file.getURI());
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
		}
		catch(Exception e)
		{
			System.out.println("Echec  Erreur intervenant  lors de la recherche du fichier");
			return null;
		}
	}
}

