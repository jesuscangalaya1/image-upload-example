package com.example.demo.controller;


import com.example.demo.entities.Image;
import com.example.demo.repository.ImageRepository;
import com.example.demo.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageStorageService imageStorageService;
    private final HttpServletRequest request;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Image> uploadFile(@RequestParam("file") MultipartFile multipartFile,
                                                    @RequestParam("price") Double price) {
        Image image = imageStorageService.store(multipartFile, price);

        String host = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        String imageUrl = host + "/api/images/" + image.getId(); // Construye la URL de la imagen

        Image response = new Image(imageUrl, image.getPrice());

        return ResponseEntity.ok().body(response);
    }




/*    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> uploadFile(@RequestParam("file") MultipartFile multipartFile){
        String path = imageStorageService.store(multipartFile);
        String host = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        String url = ServletUriComponentsBuilder
                .fromHttpUrl(host)
                .path("/")
                .path(path)
                .toUriString();
        return Map.of("url", url);
    }*/



    @GetMapping("/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable Long id) throws IOException {
        Resource file = imageStorageService.loadAsResource(id);
        String contentType = Files.probeContentType(file.getFile().toPath());
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(file);
    }


/*
    @GetMapping("{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws IOException {
        Resource file = imageStorageService.loadAsResource(filename);
        String contentType = Files.probeContentType(file.getFile().toPath());
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(file);

    }
*/




}


