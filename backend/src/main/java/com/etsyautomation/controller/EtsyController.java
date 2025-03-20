package com.etsyautomation.controller;

import com.etsyautomation.services.EtsyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etsy")
public class EtsyController {

    private final EtsyService etsyService;

    public EtsyController(EtsyService etsyService) {
        this.etsyService = etsyService;
    }

    @PostMapping("/create")
    public String createListing(@RequestBody String listingData) {
        return etsyService.createEtsyListing(listingData);
    }

    @GetMapping("/listings")
    public List<String> getAllListings() {
        return etsyService.getAllEtsyListings();
    }
}