import React, { useEffect, useState } from "react";
import axios from "axios";

const Listings = () => {
    const [listings, setListings] = useState([]);

    useEffect(() => {
        axios.get(encodeURIComponent("http://localhost:8080/api/etsy/listings"))
            .then(response => setListings(response.data))
            .catch(error => console.error("Error fetching listings:", error));
    }, []);

    return (
        <div className="bg-secondary">
            <h1>Etsy Listings</h1>
            <ul>
                {listings.map((listing, index) => (
                    <li key={index}>{listing}</li>
                ))}
            </ul>
        </div>
    );
};

export default Listings;