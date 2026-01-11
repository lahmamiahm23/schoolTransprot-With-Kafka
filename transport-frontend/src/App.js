import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './styles/App.css';
import Navigation from './components/Navigation';
import Home from './pages/Home';
import Parent from './pages/Parent';
import Admin from './pages/Admin';
import Driver from './pages/Driver';
import Tracking from './pages/Tracking';

function App() {
    return (
        <Router>
            <div className="App">
                <Navigation />
                <div className="content">
                    <Routes>
                        <Route path="/" element={<Home />} />
                        <Route path="/parent" element={<Parent />} />
                        <Route path="/admin" element={<Admin />} />
                        <Route path="/driver" element={<Driver />} />
                        <Route path="/tracking" element={<Tracking />} />
                    </Routes>
                </div>
            </div>
        </Router>
    );
}

export default App;
