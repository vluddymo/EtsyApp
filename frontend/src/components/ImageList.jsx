import React, { useEffect, useState } from "react";
import axios from "axios";

export default function ImageList() {
    const [images, setImages] = useState([]);
    const [selectedImages, setSelectedImages] = useState({}); // Track selected images
    const [imagePaths, setImagePaths] = useState([]);

    useEffect(() => {
        axios.get("http://localhost:8080/api/images/list")
            .then(response => setImages(response.data))
            .catch(error => console.error("Error fetching images:", error));
    }, []);

    // Toggle selection of an image
    const handleCheckboxChange = (img) => {
        setSelectedImages(prevState => ({
            ...prevState,
            [img.filename]: !prevState[img.filename],
            "path": [img.path],
        }));
        setImagePaths(prevState => ([
            ...prevState, img.absolutePath
        ]))
        console.table(selectedImages)
    };

    const handleUploadToGoogleDrive = async () => {
        const selectedPreviews = Object.keys(imagePaths).filter(path => imagePaths[path]);
        console.table(selectedPreviews);
        if (selectedPreviews.length === 0) {
            alert("No images selected for upload!");
            return;
        }

        try {
            const response = await axios.post("http://localhost:8080/api/images/upload", imagePaths, {
                headers: {
                "Content-Type": "application/json"
            }
        });

            alert(response.data.message || "✅ Folders uploaded successfully!");
        } catch (error) {
            console.error("❌ Error uploading folders:", error);
            alert("❌ Upload failed. Please try again.");
        }
    };

    return (
        <div className="mt-6">
            <h2 className="text-xl font-semibold">Processed Images</h2>

            {images.length > 0 ? (
                <>
                    <div className="grid grid-cols-3 gap-4 mt-4">
                        {images.map((img, index) => (
                            <div key={index} className="bg-gray-100 p-4 rounded-md flex flex-col items-center">
                                <input
                                    type="checkbox"
                                    checked={!!selectedImages[img.filename]}
                                    onChange={() => handleCheckboxChange(img)}
                                    className="mb-2"
                                />
                                <img
                                    src={`http://localhost:8080${img.path}`}
                                    alt={img.filename}
                                    className="w-full h-auto rounded-md"
                                />
                                <p className="mt-2 text-sm">{img.filename}</p>
                            </div>
                        ))}
                    </div>

                    <button
                        onClick={handleUploadToGoogleDrive}
                        className="mt-6 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                    >
                        📤 Upload Selected to Google Drive
                    </button>
                </>
            ) : (
                <p>🚫 No processed images found.</p>
            )}
        </div>
    );
}