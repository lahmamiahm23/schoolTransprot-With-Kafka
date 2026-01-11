import React from 'react';
import { Link } from 'react-router-dom';

const Home = () => {
    return (
        <div className="home-page">
            <div className="hero">
                <h1>🚍 Système de Transport Scolaire</h1>
                <p>Suivi en temps réel • Alertes • Gestion des pénalités</p>
            </div>

            <div className="features">
                <div className="feature-card">
                    <div className="feature-icon">👨‍👦</div>
                    <h3>Parents</h3>
                    <p>Suivez le bus de votre enfant en temps réel</p>
                    <Link to="/parent" className="btn-primary">Accéder</Link>
                </div>

                <div className="feature-card">
                    <div className="feature-icon">👮‍♂️</div>
                    <h3>Administration</h3>
                    <p>Gérez les trajets et véhicules</p>
                    <Link to="/admin" className="btn-secondary">Accéder</Link>
                </div>

                <div className="feature-card">
                    <div className="feature-icon">🚐</div>
                    <h3>Conducteurs</h3>
                    <p>Interface de navigation</p>
                    <Link to="/driver" className="btn-tertiary">Accéder</Link>
                </div>

                <div className="feature-card">
                    <div className="feature-icon">📍</div>
                    <h3>Tracking Test</h3>
                    <p>Testez la simulation GPS</p>
                    <Link to="/tracking" className="btn-alert">Tester</Link>
                </div>
            </div>

            <div className="instructions">
                <h2>Comment tester:</h2>
                <ol>
                    <li>Aller à <strong>Tracking</strong> pour simuler un bus</li>
                    <li>Choisir un parent en haut à droite</li>
                    <li>Aller à <strong>Parent</strong> pour voir le suivi</li>
                    <li>Cliquer "Approcher parent" pour déclencher l'alarme 5min</li>
                    <li>Confirmer ramassage pour éviter les pénalités</li>
                </ol>
            </div>
        </div>
    );
};

export default Home;
