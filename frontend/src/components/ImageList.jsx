import React, { useEffect, useState } from "react";
import axios from "axios";

export default function ImageList() {
    const [images, setImages] = useState([]);
    const [selectedImages, setSelectedImages] = useState({}); // Track selected images

    useEffect(() => {
        axios.get("http://localhost:8080/api/images/list")
            .then(response => setImages(response.data))
            .catch(error => console.error("Error fetching images:", error));
    }, []);

    // Toggle selection of an image
    const handleCheckboxChange = (filename) => {
        setSelectedImages(prevState => ({
            ...prevState,
            [filename]: !prevState[filename]
        }));
    };

    // Upload selected images to Google Drive
    const handleUploadToGoogleDrive = async () => {
        const selectedFiles = Object.keys(selectedImages).filter(filename => selectedImages[filename]);

        if (selectedFiles.length === 0) {
            alert("No images selected for upload!");
            return;
        }

        const formData = new FormData();

        for (const filename of selectedFiles) {
            const imageMeta = images.find(img => img.filename === filename);
            if (imageMeta) {
                try {
                    const response = await fetch(`http://localhost:8080${imageMeta.path}`);
                    const blob = await response.blob();
                    const file = new File([blob], filename, { type: blob.type });
                    formData.append("files", file);
                } catch (err) {
                    console.error(`⚠️ Failed to fetch file ${filename}`, err);
                }
            }
        }

        if (formData.has("files")) {
            try {
                const response = await axios.post("http://localhost:8080/api/images/upload", formData, {
                    headers: {
                        "Content-Type": "multipart/form-data"
                    }
                });

                alert(response.data.message || "✅ Images uploaded successfully!");
            } catch (error) {
                console.error("❌ Error uploading images:", error);
                alert("❌ Upload failed. Please try again.");
            }
        } else {
            alert("❌ No valid files found to upload.");
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
                                    onChange={() => handleCheckboxChange(img.filename)}
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