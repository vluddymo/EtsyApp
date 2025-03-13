package com.etsyautomation.services;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class EtsyService {

    public String createEtsyListing(String listingData) {
        // Placeholder logic
        return "Etsy listing created successfully!";
    }

    public List<String> getAllEtsyListings() {
        return Arrays.asList("Listing 1", "Listing 2", "Listing 3");
    }
}