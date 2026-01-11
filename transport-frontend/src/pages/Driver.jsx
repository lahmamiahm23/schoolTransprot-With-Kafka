import React, { useState, useEffect } from 'react';
import {
    Bus,
    Clock,
    MapPin,
    Users,
    AlertTriangle,
    CheckCircle,
    XCircle,
    User,
    PlayCircle,
    StopCircle,
    Timer,
    Bell,
    ChevronRight
} from 'lucide-react';

const Driver = ({ api, mockData, driverId }) => {
    const [activeTrip, setActiveTrip] = useState(null);
    const [tripStops, setTripStops] = useState([]);
    const [pendingStops, setPendingStops] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedStop, setSelectedStop] = useState(null);
    const [timers, setTimers] = useState({});

    useEffect(() => {
        fetchDriverData();
        const interval = setInterval(fetchDriverData, 3000);
        return () => clearInterval(interval);
    }, [driverId]);

    useEffect(() => {
        // Mise à jour des timers chaque seconde
        const timerInterval = setInterval(() => {
            setTimers(prev => {
                const newTimers = { ...prev };
                Object.keys(newTimers).forEach(stopId => {
                    if (newTimers[stopId].isRunning) {
                        const elapsed = Date.now() - newTimers[stopId].startTime;
                        const minutesElapsed = Math.floor(elapsed / 60000);
                        newTimers[stopId].minutesElapsed = minutesElapsed;
                        newTimers[stopId].minutesRemaining = Math.max(0, 5 - minutesElapsed);

                        if (minutesElapsed >= 5) {
                            newTimers[stopId].isRunning = false;
                        }
                    }
                });
                return newTimers;
            });
        }, 1000);

        return () => clearInterval(timerInterval);
    }, []);

    const fetchDriverData = async () => {
        try {
            const tripsRes = await api.getAllTrips();
            const currentTrip = tripsRes.data.find(t => t.status === 'IN_PROGRESS');

            if (currentTrip) {
                setActiveTrip(currentTrip);
                const stopsRes = await api.getTripStops(currentTrip.id);
                setTripStops(stopsRes.data);

                const pending = stopsRes.data.filter(s => s.status === 'WAITING');
                setPendingStops(pending);
            } else {
                setActiveTrip(null);
                setTripStops([]);
                setPendingStops([]);
            }
        } catch (error) {
            console.error('Erreur:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleStartTrip = async () => {
        try {
            await api.startTrip(1); // Vehicle ID 1
            alert('Trajet démarré avec succès !');
            fetchDriverData();
        } catch (error) {
            alert('Erreur: ' + error);
        }
    };

    const handleEndTrip = async () => {
        if (!activeTrip) return;
        try {
            await api.endTrip(activeTrip.id);
            alert('Trajet terminé avec succès !');
            setActiveTrip(null);
            setTimers({});
        } catch (error) {
            alert('Erreur: ' + error);
        }
    };

    const handleArriveAtParent = async (parentId) => {
        if (!activeTrip) return;

        const parent = mockData.parents.find(p => p.id === parentId);
        try {
            const response = await api.arriveAtParent(activeTrip.id, parentId);
            if (response.data) {
                alert(`Arrivée enregistrée chez ${parent.name}`);

                // Démarrer le timer
                setTimers(prev => ({
                    ...prev,
                    [response.data.stopId]: {
                        startTime: Date.now(),
                        minutesElapsed: 0,
                        minutesRemaining: 5,
                        isRunning: true
                    }
                }));

                fetchDriverData();
            }
        } catch (error) {
            alert('Erreur: ' + error);
        }
    };

    const handleChildBoarded = async (stopId, parentName) => {
        try {
            const response = await api.childBoarded(stopId);
            const data = response.data;

            if (data.penaltyApplied) {
                alert(`⚠️ Embarcation tardive chez ${parentName}\nPénalité: ${data.penaltyAmount} DT`);
            } else {
                alert(`✅ Enfant embarqué chez ${parentName}`);
            }

            // Arrêter le timer
            setTimers(prev => {
                const newTimers = { ...prev };
                delete newTimers[stopId];
                return newTimers;
            });

            fetchDriverData();
        } catch (error) {
            alert('Erreur: ' + error);
        }
    };

    const handleParentAbsent = async (stopId, parentName) => {
        try {
            await api.parentAbsent(stopId);
            alert(`🚫 ${parentName} signalé absent`);

            // Arrêter le timer
            setTimers(prev => {
                const newTimers = { ...prev };
                delete newTimers[stopId];
                return newTimers;
            });

            fetchDriverData();
        } catch (error) {
            alert('Erreur: ' + error);
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'COMPLETED': return 'success';
            case 'LATE_BOARDING': return 'warning';
            case 'NO_SHOW': return 'danger';
            case 'WAITING': return 'info';
            default: return 'secondary';
        }
    };

    const getStatusIcon = (status) => {
        switch (status) {
            case 'COMPLETED': return <CheckCircle className="text-green-600" />;
            case 'LATE_BOARDING': return <AlertTriangle className="text-yellow-600" />;
            case 'NO_SHOW': return <XCircle className="text-red-600" />;
            case 'WAITING': return <Clock className="text-blue-600" />;
            default: return <Clock className="text-gray-600" />;
        }
    };

    if (loading) return <div className="loading">Chargement...</div>;

    const vehicle = mockData.vehicles[0];
    const currentStops = tripStops.map(stop => ({
        ...stop,
        parent: mockData.parents.find(p => p.id === stop.parentId)
    }));

    return (
        <div className="driver-container">
            {/* Header */}
            <header className="driver-header">
                <div className="driver-info">
                    <div className="avatar">
                        <User size={28} />
                    </div>
                    <div>
                        <h1>Interface Conducteur</h1>
                        <p className="subtitle">Gestion des arrêts en temps réel</p>
                    </div>
                </div>

                <div className="trip-controls">
                    {!activeTrip ? (
                        <button onClick={handleStartTrip} className="start-btn">
                            <PlayCircle size={20} /> Démarrer trajet
                        </button>
                    ) : (
                        <button onClick={handleEndTrip} className="end-btn">
                            <StopCircle size={20} /> Terminer trajet
                        </button>
                    )}
                </div>
            </header>

            {/* Trip Status */}
            <section className="trip-status-section">
                <div className="trip-card">
                    <div className="trip-header">
                        <Bus size={24} />
                        <h3>Trajet actuel</h3>
                    </div>

                    {activeTrip ? (
                        <div className="trip-details">
                            <div className="detail">
                                <span>Véhicule:</span>
                                <strong>{vehicle?.plateNumber} - {vehicle?.model}</strong>
                            </div>
                            <div className="detail">
                                <span>Heure de départ:</span>
                                <strong>{new Date(activeTrip.startTime).toLocaleTimeString()}</strong>
                            </div>
                            <div className="detail">
                                <span>Arrêts restants:</span>
                                <strong>{currentStops.filter(s => !['COMPLETED', 'NO_SHOW'].includes(s.status)).length}</strong>
                            </div>
                        </div>
                    ) : (
                        <p className="no-trip">Aucun trajet en cours</p>
                    )}
                </div>
            </section>

            {/* Stops Management */}
            <section className="stops-section">
                <div className="section-header">
                    <Users size={24} />
                    <h3>Arrêts du trajet</h3>
                    <span className="badge">{currentStops.length}</span>
                </div>

                <div className="stops-list">
                    {currentStops.map((stop, index) => (
                        <div key={stop.id} className={`stop-card ${getStatusColor(stop.status)}`}>
                            <div className="stop-header">
                                <div className="stop-number">{index + 1}</div>
                                <div className="stop-info">
                                    <h4>{stop.parent?.name}</h4>
                                    <p className="student-name">Élève: {stop.parent?.students[0]?.name}</p>
                                </div>
                                <div className="stop-status">
                                    {getStatusIcon(stop.status)}
                                    <span>{stop.status || 'SCHEDULED'}</span>
                                </div>
                            </div>

                            <div className="stop-details">
                                {stop.actualTime && (
                                    <p><Clock size={14} /> Arrivée: {stop.actualTime}</p>
                                )}
                                {stop.notes && (
                                    <p className="notes">{stop.notes}</p>
                                )}
                            </div>

                            {/* Timer for waiting stops */}
                            {stop.status === 'WAITING' && timers[stop.id] && (
                                <div className="timer-warning">
                                    <Timer size={18} />
                                    <div className="timer-display">
                                        <span className="time">{timers[stop.id].minutesElapsed}:{String(60 - (Date.now() - timers[stop.id].startTime) / 1000 % 60).split('.')[0].padStart(2, '0')}</span>
                                        <span className="label"> / 5:00</span>
                                    </div>
                                    {timers[stop.id].minutesElapsed >= 5 && (
                                        <span className="penalty-alert">⚠️ Pénalité applicable</span>
                                    )}
                                </div>
                            )}

                            {/* Actions */}
                            <div className="stop-actions">
                                {stop.status === 'SCHEDULED' && (
                                    <button
                                        onClick={() => handleArriveAtParent(stop.parentId)}
                                        className="action-btn arrive-btn"
                                    >
                                        <MapPin size={16} /> Arrivée
                                    </button>
                                )}

                                {stop.status === 'WAITING' && (
                                    <div className="action-group">
                                        <button
                                            onClick={() => handleChildBoarded(stop.id, stop.parent?.name)}
                                            className="action-btn success-btn"
                                        >
                                            <CheckCircle size={16} /> Enfant embarqué
                                        </button>
                                        <button
                                            onClick={() => handleParentAbsent(stop.id, stop.parent?.name)}
                                            className="action-btn danger-btn"
                                        >
                                            <XCircle size={16} /> Absent
                                        </button>
                                    </div>
                                )}

                                {['COMPLETED', 'LATE_BOARDING', 'NO_SHOW'].includes(stop.status) && (
                                    <button
                                        onClick={() => setSelectedStop(stop)}
                                        className="action-btn info-btn"
                                    >
                                        <Bell size={16} /> Détails
                                    </button>
                                )}
                            </div>
                        </div>
                    ))}
                </div>

                {currentStops.length === 0 && (
                    <div className="no-stops">
                        <Users size={48} />
                        <p>Aucun arrêt programmé pour ce trajet</p>
                    </div>
                )}
            </section>

            {/* Pending Stops */}
            {pendingStops.length > 0 && (
                <section className="pending-section">
                    <h3><AlertTriangle size={20} /> Arrêts en attente</h3>
                    <div className="pending-list">
                        {pendingStops.map(stop => {
                            const timer = timers[stop.id];
                            return (
                                <div key={stop.id} className="pending-card">
                                    <div className="pending-info">
                                        <h4>{stop.parent?.name}</h4>
                                        <p>En attente depuis {timer?.minutesElapsed || 0} minutes</p>
                                    </div>
                                    <div className="pending-timer">
                                        <div className="progress-bar">
                                            <div
                                                className="progress-fill"
                                                style={{ width: `${Math.min(100, (timer?.minutesElapsed || 0) * 20)}%` }}
                                            />
                                        </div>
                                        <span className="time-remaining">
                      {timer?.minutesRemaining || 5}:00 restantes
                    </span>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </section>
            )}

            {/* Selected Stop Details */}
            {selectedStop && (
                <section className="details-section">
                    <h3>Détails de l'arrêt</h3>
                    <div className="details-card">
                        <div className="detail-row">
                            <span>Parent:</span>
                            <strong>{selectedStop.parent?.name}</strong>
                        </div>
                        <div className="detail-row">
                            <span>Statut:</span>
                            <span className={`status-tag ${getStatusColor(selectedStop.status)}`}>
                {selectedStop.status}
              </span>
                        </div>
                        {selectedStop.actualTime && (
                            <div className="detail-row">
                                <span>Heure d'arrivée:</span>
                                <span>{selectedStop.actualTime}</span>
                            </div>
                        )}
                        {selectedStop.notes && (
                            <div className="detail-row">
                                <span>Notes:</span>
                                <p>{selectedStop.notes}</p>
                            </div>
                        )}
                    </div>
                </section>
            )}

            {/* Instructions */}
            <section className="instructions-section">
                <h3><ChevronRight size={20} /> Instructions</h3>
                <ol className="instructions-list">
                    <li>Cliquez sur "Arrivée" lorsque vous arrivez chez un parent</li>
                    <li>Le parent a 5 minutes pour faire monter l'enfant</li>
                    <li>Cliquez sur "Enfant embarqué" après l'embarquement</li>
                    <li>Si le parent est absent, cliquez sur "Absent"</li>
                    <li>Une pénalité automatique est appliquée après 5 minutes d'attente</li>
                </ol>
            </section>
        </div>
    );
};

export default Driver;
