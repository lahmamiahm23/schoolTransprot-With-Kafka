import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navigation = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const handleParentChange = (id) => {
        localStorage.setItem('parentId', id);
        window.location.reload();
    };

    return (
        <nav className="navbar">
            <div className="nav-brand">
                <Link to="/">🚌 Transport Scolaire</Link>
            </div>

            <div className="nav-links">
                <Link to="/">Accueil</Link>

                {user && user.role === 'PARENT' && (
                    <Link to="/parent">Parent</Link>
                )}

                {user && user.role === 'ADMIN' && (
                    <Link to="/admin">Admin</Link>
                )}

                {user && user.role === 'DRIVER' && (
                    <Link to="/driver">Conducteur</Link>
                )}

                {user && <Link to="/tracking">Tracking</Link>}
            </div>

            <div className="nav-actions">
                {user ? (
                    <>
            <span className="user-info">
              {user.firstName} ({user.role})
            </span>

                        {user.role === 'PARENT' && (
                            <select
                                value={localStorage.getItem('parentId') || user.userId}
                                onChange={(e) => handleParentChange(e.target.value)}
                                className="parent-select"
                            >
                                <option value={user.userId}>Mon compte</option>
                                <option value="1">Parent 1 (Démo)</option>
                                <option value="2">Parent 2 (Démo)</option>
                            </select>
                        )}

                        <button onClick={handleLogout} className="btn-logout">
                            Déconnexion
                        </button>
                    </>
                ) : (
                    <>
                        <Link to="/login" className="btn-login">Connexion</Link>
                        <Link to="/register" className="btn-register">Inscription</Link>
                    </>
                )}
            </div>
        </nav>
    );
};

export default Navigation;
