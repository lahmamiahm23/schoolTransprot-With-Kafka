import React, { useState, useEffect } from 'react';
import { simulationAPI } from '../services/api';

const Tracking = () => {
    const [vehicleId, setVehicleId] = useState('1');
    const [position, setPosition] = useState({ lat: 36.8065, lng: 10.1815 });
    const [isSimulating, setIsSimulating] = useState(false);
    const [logs, setLogs] = useState([]);

    const addLog = (message) => {
        setLogs(prev => [`[${new Date().toLocaleTimeString()}] ${message}`, ...prev.slice(0, 9)]);
    };

    const startSimulation = async () => {
        try {
            await simulationAPI.startSimulation(vehicleId);
            setIsSimulating(true);
            addLog(`Simulation démarrée pour véhicule ${vehicleId}`);
        } catch (error) {
            addLog(`Erreur: ${error.message}`);
        }
    };

    const moveRandomly = async () => {
        if (!isSimulating) return;

        const newLat = position.lat + (Math.random() * 0.02 - 0.01);
        const newLng = position.lng + (Math.random() * 0.02 - 0.01);

        try {
            await simulationAPI.updatePosition(vehicleId, newLat, newLng);
            setPosition({ lat: newLat, lng: newLng });
            addLog(`Position mise à jour: ${newLat.toFixed(4)}, ${newLng.toFixed(4)}`);
        } catch (error) {
            addLog(`Erreur: ${error.message}`);
        }
    };

    const moveTowardsParent = () => {
        // Simuler mouvement vers un parent (coordonnées fixes)
        const parentLat = 36.8165;
        const parentLng = 10.1915;

        const newLat = position.lat + (parentLat - position.lat) * 0.1;
        const newLng = position.lng + (parentLng - position.lng) * 0.1;

        simulationAPI.updatePosition(vehicleId, newLat, newLng)
            .then(() => {
                setPosition({ lat: newLat, lng: newLng });
                addLog(`Approche parent: ${newLat.toFixed(4)}, ${newLng.toFixed(4)}`);
            })
            .catch(error => addLog(`Erreur: ${error.message}`));
    };

    useEffect(() => {
        let interval;
        if (isSimulating) {
            interval = setInterval(moveRandomly, 5000);
        }
        return () => clearInterval(interval);
    }, [isSimulating, position]);

    return (
        <div className="tracking-page">
            <h1>Simulation de Tracking</h1>

            <div className="controls">
                <div className="input-group">
                    <label>ID Véhicule:</label>
                    <input
                        type="text"
                        value={vehicleId}
                        onChange={(e) => setVehicleId(e.target.value)}
                    />
                </div>

                <div className="button-group">
                    <button onClick={startSimulation} disabled={isSimulating} className="btn-primary">
                        {isSimulating ? 'Simulation en cours' : 'Démarrer simulation'}
                    </button>

                    <button onClick={moveRandomly} disabled={!isSimulating} className="btn-secondary">
                        Bouger aléatoirement
                    </button>

                    <button onClick={moveTowardsParent} disabled={!isSimulating} className="btn-alert">
                        Approcher parent
                    </button>
                </div>
            </div>

            <div className="position-info">
                <h2>Position actuelle</h2>
                <p>Latitude: {position.lat.toFixed(6)}</p>
                <p>Longitude: {position.lng.toFixed(6)}</p>
                <div className="map-preview">
                    {/* Carte simplifiée */}
                    <div className="map">
                        <div className="marker" style={{
                            left: '50%',
                            top: '50%',
                            transform: 'translate(-50%, -50%)'
                        }}>🚌</div>
                    </div>
                </div>
            </div>

            <div className="logs">
                <h2>Logs de simulation</h2>
                <div className="log-list">
                    {logs.map((log, index) => (
                        <div key={index} className="log-item">{log}</div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default Tracking;
