import React, { useEffect, useState } from "react";
import axios from "axios";

export default function ImageList() {
    const [images, setImages] = useState([]);

    useEffect(() => {
        axios.get("http://localhost:8080/api/images/list")
            .then(response => setImages(response.data))
            .catch(error => console.error("Error fetching images:", error));
    }, []);

    return (
        <div className="mt-6">
            <h2 className="text-xl font-semibold">🖼 Processed Images</h2>
            <div className="grid grid-cols-3 gap-4 mt-4">
                {images.length > 0 ? images.map((img, index) => (
                    <div key={index} className="bg-gray-100 p-4 rounded-md">
                        <img
                            src={`http://localhost:8080${img.path}`}
                            alt={img.filename}
                            className="w-full h-auto rounded-md"
                        />
                        <p className="mt-2 text-sm">{img.filename}</p>
                    </div>
                )) : <p>🚫 No processed images found.</p>}
            </div>
        </div>
    );
}