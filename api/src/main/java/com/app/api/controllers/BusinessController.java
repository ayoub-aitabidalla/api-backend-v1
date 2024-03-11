package com.app.api.controllers;


import com.app.api.entities.business.Business;
import com.app.api.entities.business.BusinessCategory;
import com.app.api.entities.business.BusinessType;
import com.app.api.responses.BusinessResponse;
import com.app.api.services.BusinessService;
import com.app.api.services.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/business")
public class BusinessController {
    @Autowired
    BusinessService businessService;

    @Autowired
    private StorageService storageService;
    @PostMapping(value="/add/{id}")
    public void createBusiness(@RequestBody Business business, @PathVariable long id)

    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        businessService.addBusiness(username,business,id);
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("image")MultipartFile file) throws IOException {
        String uploadImage = storageService.uploadImage(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(uploadImage);
    }

    @GetMapping("/getBusiness/{name}")
    public BusinessResponse getBusinessByName(@PathVariable String name)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return businessService.getBusinessByName(name,username);

    }

    @GetMapping("/list-types")
    public List<BusinessType> getAllTypes(@RequestParam(value = "page") int page,
                                          @RequestParam(value="limit") int limit)
    {
        return businessService.getListOfTypes(page, limit);

    }
    @GetMapping("/list-categories")
    public List<BusinessCategory> getAllCategories(@RequestParam(value = "page") int page,
                                                   @RequestParam(value="limit") int limit)
    {
        return businessService.getListOfCategories(page,limit);

    }
    @GetMapping("/list-types/{id}")
    public List<BusinessType> getTypesByCategory(@PathVariable long id)
    {
        return businessService.getListOfTypesByCatgId(id);

    }
    @GetMapping("/list-businesses")
    public List<BusinessResponse> getTBusinesses()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return businessService.getListBusinessByUserId(username);

    }

    @DeleteMapping("/delete")
    public void deleteBusiness(@RequestBody Business business)
    {
        businessService.deleteBusiness(business);
    }

    //pas encore tester
    @PutMapping
    public ResponseEntity<Business> updateBusiness(@RequestBody Business updatedBusiness) {
        Business updatedBusinessObject = businessService.updateBusiness(updatedBusiness);
        return new ResponseEntity<>(updatedBusinessObject, HttpStatus.OK);
    }



}
