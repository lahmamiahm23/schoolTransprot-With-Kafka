import React, { useState, useEffect } from 'react';
import { parentAPI } from '../services/api';
import VehicleMap from '../components/VehicleMap';
import Notifications from '../components/Notifications';

const Parent = () => {
    const [eta, setEta] = useState(null);
    const [position, setPosition] = useState(null);
    const [notifications, setNotifications] = useState([]);
    const [penalties, setPenalties] = useState([]);

    useEffect(() => {
        loadData();
        const interval = setInterval(loadData, 10000); // Rafraîchir toutes les 10s
        return () => clearInterval(interval);
    }, []);

    const loadData = async () => {
        try {
            const [etaRes, positionRes, notifRes, penaltiesRes] = await Promise.all([
                parentAPI.getETA(),
                parentAPI.getBusPosition(),
                parentAPI.getNotifications(),
                parentAPI.getPenalties()
            ]);

            setEta(etaRes.data);
            setPosition(positionRes.data);
            setNotifications(notifRes.data.notifications || []);
            setPenalties(penaltiesRes.data || []);
        } catch (error) {
            console.error('Erreur chargement données:', error);
        }
    };

    const handleConfirmPickup = async () => {
        try {
            // Simuler un ID d'arrêt (à remplacer par un vrai)
            await parentAPI.confirmPickup(1);
            alert('Ramassage confirmé !');
        } catch (error) {
            alert('Erreur: ' + error.message);
        }
    };

    return (
        <div className="parent-dashboard">
            <h1>Tableau de bord Parent</h1>

            <div className="dashboard-grid">
                <div className="card">
                    <h2>ETA du Bus</h2>
                    {eta ? (
                        <>
                            <p className="eta-time">{eta.estimatedMinutes} minutes</p>
                            <p className={eta.isClose ? 'alert-close' : ''}>
                                {eta.isClose ? '⚠️ Bus proche !' : 'Bus en route'}
                            </p>
                            <p>Distance: {eta.distanceKm.toFixed(2)} km</p>
                        </>
                    ) : (
                        <p>Chargement...</p>
                    )}
                </div>

                <div className="card">
                    <h2>Position du Bus</h2>
                    {position?.available ? (
                        <div>
                            <p>📍 Lat: {position.position.latitude.toFixed(4)}</p>
                            <p>📍 Lng: {position.position.longitude.toFixed(4)}</p>
                            <p>Bus: {position.vehicle.plateNumber}</p>
                        </div>
                    ) : (
                        <p>Position non disponible</p>
                    )}
                </div>

                <div className="card">
                    <h2>Pénalités</h2>
                    <p>Total: {penalties.length}</p>
                    {penalties.length > 0 && (
                        <button onClick={handleConfirmPickup} className="btn-primary">
                            Confirmer ramassage
                        </button>
                    )}
                </div>
            </div>

            <div className="row">
                <div className="map-container">
                    <h2>Carte en temps réel</h2>
                    <VehicleMap position={position} />
                </div>

                <div className="notifications-container">
                    <Notifications
                        notifications={notifications}
                        onMarkAllRead={() => parentAPI.markNotificationsRead().then(loadData)}
                    />
                </div>
            </div>
        </div>
    );
};

export default Parent;
