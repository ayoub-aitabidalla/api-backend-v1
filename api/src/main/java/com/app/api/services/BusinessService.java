package com.app.api.services;

import com.app.api.entities.business.Business;
import com.app.api.entities.business.BusinessCategory;
import com.app.api.entities.business.BusinessType;
import com.app.api.exceptions.BusinessException;
import com.app.api.repositories.BusinessCategoryReposiroty;
import com.app.api.repositories.BusinessRepository;
import com.app.api.repositories.BusinessTypeRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.responses.BusinessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;


@Service
public class BusinessService {

    @Autowired
    BusinessRepository businessRepository;
    @Autowired
    BusinessTypeRepository businessTypeRepository;
    @Autowired
    BusinessCategoryReposiroty businessCategoryReposiroty;
    @Autowired
    UserRepository userRepository;

    public void addBusiness(String username,Business newBusiness, long id){
        var user = userRepository.findByUsername(username);
        var checkBusiness = businessRepository.findByBusinessName(newBusiness.getBusinessName());
        if(checkBusiness!=null)
        {
            throw new BusinessException("business name already exist!!");
        }
        newBusiness.setUser(user);
        BusinessType businessType = businessTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Business type not found"));
        newBusiness.setType(businessType);

        businessRepository.save(newBusiness);
    }

    public List<BusinessResponse> getListBusinessByUserId(String username)
    {

        var user = userRepository.findByUsername(username);

        List<Business> businesses = (List<Business>) businessRepository.findByUserId(user.getId());
        List<BusinessResponse> businessResponses = new ArrayList<>();
        for (Business business : businesses) {
            ModelMapper modelMapper = new ModelMapper();
            BusinessResponse businessResp = modelMapper.map(business, BusinessResponse.class);
            businessResp.setCategoryName(business.getType().getCategory().getCategoryName());
            businessResponses.add(businessResp);
        }
        return businessResponses;

    }







    public List<BusinessType> getListOfTypes(int page, int limit) {
        PageRequest pageRequest = PageRequest.of(page,limit);
        Page<BusinessType> typePage = businessTypeRepository.findAll(pageRequest);
        if(typePage.isEmpty())
        {
            throw new BusinessException("no types found");
        }
        return typePage.getContent();

    }
    public List<BusinessType> getListOfTypesByCatgId(long id) {

        List<BusinessType> listOfTypes = businessTypeRepository.findAllByCatgId(id);
        if(listOfTypes.isEmpty())
        {
            throw new BusinessException("no types found with this category id");
        }
        return listOfTypes;

    }
    public List<BusinessCategory> getListOfCategories(int page, int limit){
        PageRequest pageRequest = PageRequest.of(page,limit);
        Page<BusinessCategory> categoryPage = businessCategoryReposiroty.findAll(pageRequest);
        if(categoryPage.isEmpty())
        {
            throw new BusinessException("no category found");
        }
        return categoryPage.getContent();
    }

    public void deleteBusiness(Business business)
    {
        Business businessToDelete = businessRepository.findById(business.getId())
                .orElseThrow(()->new BusinessException("no business found to delete"));
        businessRepository.delete(businessToDelete);

    }

    public BusinessResponse getBusinessByName(String businessName, String username)
    {
        var user = userRepository.findByUsername(username);
        Business business=businessRepository.findByBusinessNameAndUser(businessName,user.getId());
        ModelMapper modelMapper = new ModelMapper();
        BusinessResponse businessResp = modelMapper.map(business, BusinessResponse.class);
        businessResp.setCategoryName(business.getType().getCategory().getCategoryName());
        return businessResp;
    }

    public Business updateBusiness(Business updatedBusiness) {
        Business existingBusiness = businessRepository.findByBusinessName(updatedBusiness.getBusinessName());
        existingBusiness.setEmail(updatedBusiness.getEmail());
        existingBusiness.setPhone(updatedBusiness.getPhone());
        existingBusiness.setAddress(updatedBusiness.getAddress());
        existingBusiness.setFacebookLink(updatedBusiness.getFacebookLink());
        existingBusiness.setInstagramLink(updatedBusiness.getInstagramLink());
        existingBusiness.setGoogleLink(updatedBusiness.getGoogleLink());
        existingBusiness.setCoverImageUrl(updatedBusiness.getCoverImageUrl());
        return businessRepository.save(existingBusiness);
    }


}
