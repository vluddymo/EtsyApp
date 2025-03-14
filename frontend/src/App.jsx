import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Dashboard from "./pages/Dashboard";
import Listings from "./pages/Listings";
import CreateListing from "./pages/CreateListing";
import Navbar from "./components/Navbar";

function App() {
    return (
        <Router>
           <Navbar/>

            <Routes>
                <Route path="/" element={<Dashboard />} />
                <Route path="/listings" element={<Listings />} />
                <Route path="/create" element={<CreateListing />} />
            </Routes>
        </Router>
    );
}

export default App;