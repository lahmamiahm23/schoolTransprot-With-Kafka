import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import './styles/App.css';

// Pages
import Home from '../pages/Home';
import Login from '../pages/Login';
import Register from '../pages/Register';
import Logout from '../pages/Logout';
import Parent from '../pages/Parent';
import Admin from '../pages/Admin';
import Driver from '../pages/Driver';
import Tracking from '../pages/Tracking';
import Navigation from '../components/Navigation';

function App() {
    return (
        <AuthProvider>
            <Router>
                <div className="App">
                    <Navigation />
                    <div className="content">
                        <Routes>
                            <Route path="/" element={<Home />} />
                            <Route path="/login" element={<Login />} />
                            <Route path="/register" element={<Register />} />
                            <Route path="/logout" element={<Logout />} />

                            <Route path="/parent" element={
                                <ProtectedRoute roles={['PARENT']}>
                                    <Parent />
                                </ProtectedRoute>
                            } />

                            <Route path="/admin" element={
                                <ProtectedRoute roles={['ADMIN']}>
                                    <Admin />
                                </ProtectedRoute>
                            } />

                            <Route path="/driver" element={
                                <ProtectedRoute roles={['DRIVER']}>
                                    <Driver />
                                </ProtectedRoute>
                            } />

                            <Route path="/tracking" element={
                                <ProtectedRoute>
                                    <Tracking />
                                </ProtectedRoute>
                            } />
                        </Routes>
                    </div>
                </div>
            </Router>
        </AuthProvider>
    );
}

export default App;
