import React, { useEffect, useState } from "react";
import axios from "axios";
import Main from "../components/Main.jsx";
import ImageProcessor from "../components/ImageProcessor.jsx";
import ImageList from "../components/ImageList.jsx";

const Dashboard = () => {
    const [stats, setStats] = useState({});
    const [activeTab, setActiveTab] = useState("stats"); // Default tab is Etsy Stats

    useEffect(() => {
        axios.get("http://localhost:8080/api/dashboard/stats")
            .then(response => setStats(response.data))
            .catch(error => console.error("Error fetching dashboard data:", error));
    }, []);

    return (
        <Main>
            <h1 className="font-bold text-2xl">Etsy Dashboard</h1>

            {/* Tab Navigation */}
            <div className="mt-5 flex border-b">
                <button
                    className={`px-4 py-2 ${activeTab === "stats" ? "border-b-2 border-blue-500 font-semibold" : "text-gray-500"}`}
                    onClick={() => setActiveTab("stats")}
                >
                    Etsy Stats 📈
                </button>
                <button
                    className={`px-4 py-2 ml-4 ${activeTab === "images" ? "border-b-2 border-blue-500 font-semibold" : "text-gray-500"}`}
                    onClick={() => setActiveTab("images")}
                >
                    Image Processing 🖼
                </button>
            </div>

            {/* Tab Content */}
            <div className="mt-5">
                {activeTab === "stats" && (
                    <div className="grid grid-cols-3 gap-4">
                        <div className="shadow-lg p-4">
                            <h2 className="font-bold text-lg">Total Sales</h2>
                            <p className="mt-3 float-right text-wilmind">{stats.totalSales} orders</p>
                        </div>
                        <div className="shadow-lg p-4">
                            <h2 className="font-bold text-lg">Total Revenue</h2>
                            <p className="mt-3 float-right">${stats.totalRevenue}</p>
                        </div>
                        <div className="shadow-lg p-4">
                            <h2 className="font-bold text-lg">Top Selling Item</h2>
                            <p className="mt-3 float-right">{stats.topSelling}</p>
                        </div>
                        <div className="shadow-lg p-4">
                            <h2 className="font-bold text-lg">Recent Orders</h2>
                            <p className="mt-3 float-right">{stats.recentOrders} new orders</p>
                        </div>
                    </div>
                )}

                {activeTab === "images" && (
                    <div>
                        <ImageList />
                    </div>
                )}
            </div>
        </Main>
    );
};

export default Dashboard;