package com.etsyautomation.controllers;

import com.etsyautomation.services.EtsyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etsy")
@CrossOrigin(origins = "http://localhost:3000") // Allow frontend access
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