#  Etsy Automation App

###  Automate Your Etsy Shop – From Design to Listing
A desktop application built with **Electron + React (Frontend) and Spring Boot (Backend)** to streamline **Etsy shop management**, including **AI-powered design processing, mockup generation, and automated listings**.

---

## Features
✅ **Etsy API Integration** – Auto-create & update listings  
✅ **Automated Image Processing** – Resize, format, and organize poster designs  
✅ **Google Drive Support** – Store and manage design files in the cloud  
✅ **AI-Powered Listings** – Generate SEO-optimized descriptions & tags with ChatGPT  
✅ **Mockup Generation** – Automate product mockups using Photopea API  
✅ **Sales & Analytics Dashboard** – Track performance, orders, and revenue  
✅ **Batch Processing** – Upload multiple products at once  
✅ **Electron Desktop App** – Runs locally on macOS (and other OS)

---

## Installation & Setup**

### 🔹 1. Prerequisites**
- **Node.js** (v18+) → [Download](https://nodejs.org/)
- **Java JDK 17+** → [Download](https://www.oracle.com/java/technologies/javase-downloads.html)
- **Maven** (for backend) → [Install Guide](https://maven.apache.org/download.cgi)

---

### 🔹 2. Clone the Repository
```
git clone https://github.com/yourusername/etsy-automation-app.git
cd etsy-app
```

### 🔹 3. Setup the Backend (Spring Boot)
```
cd backend
mvn clean install
mvn spring-boot:run
```
📌 The backend will start on http://localhost:8080.

### 🔹 4. Setup the Frontend (React + Electron)
```
cd frontend
npm install
npm run start
```
 Electron will open, loading Vite’s frontend.


## Configuration
1️⃣ Backend Environment Variables (backend/src/main/resources/application.properties)
```
etsy.api.key=YOUR_ETSY_API_KEY
google.drive.folder=YOUR_GOOGLE_DRIVE_FOLDER_ID
photopea.api.key=YOUR_PHOTOPEA_API_KEY
```

2️⃣ Frontend Configuration (.env)
```
VITE_API_BASE_URL=http://localhost:8080
VITE_ETSY_SHOP_ID=YOUR_SHOP_ID
```

## Usage

1️⃣ Launch Electron App <br>
2️⃣ Upload Design Files → Choose images to process<br>
3️⃣ Generate Mockups → Auto-create Etsy-ready product images<br>
4️⃣ Automate Listings → Auto-generate Etsy titles, descriptions, tags<br>
5️⃣ Publish to Etsy → Sync directly with Etsy API<br>
6️⃣ Track Sales & Analytics → View dashboard stats<br>


## Deployment
1. Build Electron App for macOS
```
cd frontend
npm run build:electron
```

2. Deploy Backend to Production
```
mvn clean package
```

3. Run on a server:
```
java -jar target/backend-1.0.0.jar
```

## API Reference
### Backend APIs
| **Endpoint**               | **Method** | **Description**                               |
|----------------------------|-----------|-----------------------------------------------|
| `/api/images/process`      | `POST`    | Upload and process design images            |
| `/api/mockups/generate`    | `POST`    | Create mockups using Photopea               |
| `/api/etsy/create`         | `POST`    | Auto-create Etsy listings                   |
| `/api/stats/sales`         | `GET`     | Fetch Etsy sales data                       |

### Frontend API Calls (React)
Example of making an API call in React:
```javascript
import axios from "axios";

const uploadImage = async (filePath) => {
    const response = await axios.post("http://localhost:8080/api/images/process", {
        path: filePath
    });
    return response.data;
};
```
## Troubleshooting

### Frontend Issues
🔹 **Electron App is Blank**  
**Fix:** Open Electron DevTools (`Cmd + Alt + I` on macOS or `Ctrl + Shift + I` on Windows/Linux) and check the console logs.

🔹 **Vite Dev Server Not Found**  
**Fix:** Restart the frontend by running:
```bash
npm run dev
npm run electron
```
🔹 **Failed to Load Resources (CSS/JS Not Found in Electron)**
**Fix:** Ensure vite.config.js has the correct base path:
```
export default defineConfig({
  plugins: [react()],
  base: "./",
  build: {
    outDir: "dist",
    emptyOutDir: true,
  },
});
```
Fix: Ensure vite.config.js has the correct base path:

🔹 **Maven Build Fails**
**Fix:** Try cleaning and re-building:
```
mvn clean install
mvn spring-boot:run
```

🔹 **API Calls Failing (404 or CORS Error)**
**Fix:** Check if the backend is running at http://localhost:8080, and update your frontend API calls if needed.

### General Fixes

✅ **Restart Everything:** <br>
If issues persist, restart all services:
```
Ctrl + C  # Stop running processes
npm run dev
npm run electron
```

✅ **Check Logs:** <br>
•	Frontend: Open DevTools (Cmd + Alt + I)
•	Backend: Check logs in the terminal (mvn spring-boot:run output)

✅ **Reinstall Dependencies:** <br>
If errors persist, try reinstalling everything
```
rm -rf node_modules package-lock.json
npm install
```
Now your Etsy Automation App should work smoothly! 🔥

##  Technologies Used
	•	🖥️ Frontend: React, Electron, Vite
	•	🚀 Backend: Spring Boot (Java), Maven
	•	🛒 Etsy API: Automated product listing
	•	☁️ Google Drive API: Cloud storage
	•	🖼️ Photopea API: Mockup generation

##  Future Features
📌 Batch Listing Creation – List multiple products at once <br>
📌 Automated Price & SEO Optimization – Improve Etsy rankings <br>
📌 Multi-Shop Support – Manage multiple Etsy stores from one app <br>
📌 Auto-Reply & Customer Support – Handle customer messages <br>

## ** Summary**
✅ **Detailed README covering features, setup, API reference, troubleshooting.**  
✅ **Includes deployment instructions for Electron & Spring Boot.**  
✅ **Developer-friendly structure for easy onboarding.**  

**Would you like a guide on packaging your Electron app into a `.dmg` file for macOS?** Let me know! 😃🔥
