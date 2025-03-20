import React, { useState } from "react";

export default function ImageProcessor() {
    const [selectedFile, setSelectedFile] = useState(null);
    const [message, setMessage] = useState("");

    const handleFileChange = (event) => {
        setSelectedFile(event.target.files[0]);
    };

    const handleUpload = async () => {
        if (!selectedFile) {
            setMessage("Please select a file first!");
            return;
        }

        const formData = new FormData();
        formData.append("file", selectedFile);

        try {
            const response = await fetch(`http://localhost:8080/api/images/process`, {
                method: "POST",
                body: formData,
            });
            const data = await response.text();
            setMessage(data);
        } catch (error) {
            setMessage("Error processing image");
        }
    };

    return (
        <div>
            <input type="file" onChange={handleFileChange} />
            <button onClick={handleUpload}>Process Image</button>
            {message && <p>{message}</p>}
        </div>
    );
}