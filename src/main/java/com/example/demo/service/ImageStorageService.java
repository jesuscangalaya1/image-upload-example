package com.example.demo.service;

import com.example.demo.entities.Image;
import com.example.demo.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final ImageRepository imageRepository;

    private Path rootLocation;

    @Value("${media.location}")
    private String mediaLocation;

    @PostConstruct
    public void init() throws IOException {
        rootLocation = Paths.get(mediaLocation);
        Files.createDirectories(rootLocation);

    }


    public Image store(MultipartFile file, Double price){
        try {
            if (file.isEmpty()){
                throw new RuntimeException("Failed to store");
            }

            String filename = file.getOriginalFilename();
            Path destinationFile = rootLocation.resolve(Paths.get(filename))
                    .normalize().toAbsolutePath();
            try (InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            Image image = new Image();
            image.setImage(filename);
            image.setPrice(price);
            imageRepository.save(image);

            return image;
        }catch (IOException e){
            throw new RuntimeException("Failed to store file", e);

        }
    }

/*    public String store(MultipartFile file){
        try {
            if (file.isEmpty()){
                throw new RuntimeException("Failed to store");
            }

            String filename = file.getOriginalFilename();
            Path destinationFile = rootLocation.resolve(Paths.get(filename))
                    .normalize().toAbsolutePath();
            try (InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }



            return filename;
        }catch (IOException e){
            throw new RuntimeException("Failed to store file", e);

        }
    }*/


    public Resource loadAsResource(Long id) {
        try {
            // Buscar la entidad Image en la base de datos por ID
            Optional<Image> optionalImage = imageRepository.findById(id);
            if (optionalImage.isEmpty()) {
                throw new RuntimeException("Image not found with ID: " + id);
            }
            Image image = optionalImage.get();

            // Obtener el nombre de archivo de la entidad Image
            String filename = image.getImage();

            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + e);
        }
    }






/*    public Resource loadAsResource(String filename){
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource((file.toUri()));

            if (resource.exists() || resource.isReadable()){
                return resource;
            }else {
                throw new RuntimeException("could not read file " + filename);
            }

        }catch (MalformedURLException e){
            throw new RuntimeException("could not read file "+ filename);

        }
    }*/

}

