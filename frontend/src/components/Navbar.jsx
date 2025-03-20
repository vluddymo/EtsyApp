import {Link} from "react-router-dom";
import React from "react";

function Navbar() {
    return (
        <nav className="bg-wilmind text-white mb-4 flex justify-center">
            <ul className="sm:flex p-5">
                <li className="pe-5 bg-secondary"><Link to="/">Dashboard</Link></li>
                <li className="pe-5"><Link to="/listings">Listings</Link></li>
                <li className=""><Link to="/create">Create Listing</Link></li>
            </ul>
        </nav>
    );
}

export default Navbar