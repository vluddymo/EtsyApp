import { useState } from "react";
import axios from "axios";

import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Dashboard from "./pages/Dashboard";
import Listings from "./pages/Listings";
import CreateListing from "./pages/CreateListing";

function App() {
    return (
        <Router>
            <nav>
                <ul>
                    <li><Link to="/">Dashboard</Link></li>
                    <li><Link to="/listings">Listings</Link></li>
                    <li><Link to="/create">Create Listing</Link></li>
                </ul>
            </nav>

            <Routes>
                <Route path="/" element={<Dashboard />} />
                <Route path="/listings" element={<Listings />} />
                <Route path="/create" element={<CreateListing />} />
            </Routes>
        </Router>
    );
}

export default App;