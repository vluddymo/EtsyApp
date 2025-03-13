const { app, BrowserWindow } = require("electron");
const path = require("path");
const net = require("net");

let mainWindow;

function waitForVite(port, callback) {
    const socket = new net.Socket();
    socket.connect(port, "localhost", () => {
        socket.end();
        callback();
    });

    socket.on("error", () => {
        setTimeout(() => waitForVite(port, callback), 500);
    });
}

app.whenReady().then(() => {
    mainWindow = new BrowserWindow({
        width: 1200,
        height: 800,
        webPreferences: {
            nodeIntegration: true
        }
    });

    const VITE_PORT = 3000; // Change if needed
    const devServerUrl = `http://localhost:${VITE_PORT}`;

    waitForVite(VITE_PORT, () => {
        console.log(`Loading Vite app from ${devServerUrl}`);
        mainWindow.loadURL(devServerUrl);
    });

    mainWindow.on("closed", () => {
        mainWindow = null;
    });
});