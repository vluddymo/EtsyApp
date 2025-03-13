import React, { useState } from "react";
import axios from "axios";

const CreateListing = () => {
    const [listingData, setListingData] = useState("");

    const handleSubmit = async () => {
        await axios.post("http://localhost:8080/api/etsy/create", { title: listingData });
        alert("Listing Created!");
    };

    return (
        <div>
            <h1>Create Etsy Listing</h1>
            <input
                type="text"
                placeholder="Enter listing title"
                value={listingData}
                onChange={(e) => setListingData(e.target.value)}
            />
            <button onClick={handleSubmit}>Create Listing</button>
        </div>
    );
};

export default CreateListing;