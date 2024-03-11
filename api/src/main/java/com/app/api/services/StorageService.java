package com.app.api.services;

import com.app.api.entities.ImageModel;
import com.app.api.repositories.StorageRepository;
import com.app.api.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class StorageService {
    @Autowired
    StorageRepository storageRepository;
     public String uploadImage(MultipartFile file) throws IOException {
         storageRepository.save(ImageModel.builder()
                         .name(file.getOriginalFilename())
                         .type(file.getContentType())
                         .imageData(ImageUtils.compressImage(file.getBytes()))
                 .build());
         return "image upload successfully";
     }

}
