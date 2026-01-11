import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI } from '../services/api';

const Logout = () => {
    const navigate = useNavigate();

    useEffect(() => {
        const logout = async () => {
            try {
                // Appeler l'API de déconnexion si elle existe
                // await authAPI.logout();
            } catch (error) {
                console.error('Erreur lors de la déconnexion:', error);
            } finally {
                // Nettoyer le localStorage
                localStorage.removeItem('token');
                localStorage.removeItem('userId');
                localStorage.removeItem('userRole');
                localStorage.removeItem('parentId');

                // Rediriger vers la page de connexion
                navigate('/login');
            }
        };

        logout();
    }, [navigate]);

    return (
        <div className="logout-page">
            <h2>Déconnexion en cours...</h2>
            <p>Merci d'avoir utilisé notre service.</p>
        </div>
    );
};

export default Logout;
