const { app, BrowserWindow, session } = require("electron");
const path = require("path");

let mainWindow;

app.whenReady().then(() => {
    mainWindow = new BrowserWindow({
        width: 1200,
        height: 800,
        webPreferences: {
            nodeIntegration: false,  // Prevents executing Node.js in renderer
            contextIsolation: true,  // Security best practice
            enableRemoteModule: false,  // Disables outdated remote module
            devTools: process.env.NODE_ENV === "development",
        }
    });

    const isDev = process.env.NODE_ENV === "development";
    const devServerUrl = "http://localhost:3000";

    if (isDev) {
        mainWindow.loadURL(devServerUrl);
    } else {
        mainWindow.loadFile(path.join(__dirname, "dist", "index.html"));
    }

    const CSP_POLICY = isDev
        ? "default-src 'self' http://localhost:3000 http://localhost:8080; " +
        "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
        "style-src 'self' 'unsafe-inline'; " +
        "img-src 'self' data: http://localhost:8080; " +
        "connect-src 'self' http://localhost:8080 ws://localhost:3000;"
        : "default-src 'self'; " +
        "script-src 'self'; " +
        "style-src 'self'; " +
        "img-src 'self' data:; " +
        "connect-src 'self' https://api.etsy.com;"; // Adjust if needed

    session.defaultSession.webRequest.onHeadersReceived((details, callback) => {
        callback({
            responseHeaders: {
                ...details.responseHeaders,
                "Content-Security-Policy": [CSP_POLICY]
            }
        });
    });

    mainWindow.on("closed", () => {
        mainWindow = null;
    });
});

app.on("window-all-closed", () => {
    if (process.platform !== "darwin") {
        app.quit();
    }
});