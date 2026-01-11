import React, { useState, useEffect } from 'react';
import {
    BarChart3,
    Bus,
    Users,
    Clock,
    AlertTriangle,
    CheckCircle,
    PlayCircle,
    StopCircle,
    DollarSign,
    MapPin,
    Activity,
    TrendingUp
} from 'lucide-react';

const Admin = ({ api, mockData }) => {
    const [trips, setTrips] = useState([]);
    const [vehicles, setVehicles] = useState([]);
    const [parents, setParents] = useState([]);
    const [stats, setStats] = useState({
        activeTrips: 0,
        totalVehicles: 0,
        totalParents: 0,
        pendingPenalties: 0,
        avgWaitTime: 2.5
    });

    useEffect(() => {
        fetchAdminData();
        const interval = setInterval(fetchAdminData, 10000);
        return () => clearInterval(interval);
    }, []);

    const fetchAdminData = async () => {
        try {
            const [tripsRes, vehiclesRes, parentsRes] = await Promise.all([
                api.getAllTrips(),
                api.getAllVehicles(),
                api.getAllParents()
            ]);

            setTrips(tripsRes.data || []);
            setVehicles(vehiclesRes.data || []);
            setParents(parentsRes.data || []);

            // Calculer les statistiques
            const activeTrips = tripsRes.data?.filter(t => t.status === 'IN_PROGRESS').length || 0;
            const pendingPenalties = mockData.penalties.filter(p => p.status === 'PENDING').length;

            setStats({
                activeTrips,
                totalVehicles: vehiclesRes.data?.length || 0,
                totalParents: parentsRes.data?.length || 0,
                pendingPenalties,
                avgWaitTime: 2.5
            });
        } catch (error) {
            console.error('Erreur:', error);
        }
    };

    const handleStartTrip = async (vehicleId) => {
        try {
            await api.startTrip(vehicleId);
            alert('Trajet démarré avec succès !');
            fetchAdminData();
        } catch (error) {
            alert('Erreur: ' + error);
        }
    };

    const handleEndTrip = async (tripId) => {
        try {
            await api.endTrip(tripId);
            alert('Trajet terminé avec succès !');
            fetchAdminData();
        } catch (error) {
            alert('Erreur: ' + error);
        }
    };

    const StatCard = ({ icon: Icon, title, value, color }) => (
        <div className="stat-card" style={{ borderLeftColor: color }}>
            <div className="stat-icon" style={{ backgroundColor: color + '20' }}>
                <Icon size={24} style={{ color }} />
            </div>
            <div className="stat-content">
                <h3>{title}</h3>
                <div className="stat-value">{value}</div>
            </div>
        </div>
    );

    return (
        <div className="admin-container">
            {/* Header */}
            <header className="admin-header">
                <h1>Tableau de Bord Administrateur</h1>
                <p className="subtitle">Supervision du système de transport scolaire</p>
            </header>

            {/* Stats Overview */}
            <section className="stats-section">
                <div className="stats-grid">
                    <StatCard
                        icon={Bus}
                        title="Trajets Actifs"
                        value={stats.activeTrips}
                        color="#3b82f6"
                    />
                    <StatCard
                        icon={Bus}
                        title="Véhicules"
                        value={stats.totalVehicles}
                        color="#10b981"
                    />
                    <StatCard
                        icon={Users}
                        title="Parents"
                        value={stats.totalParents}
                        color="#f59e0b"
                    />
                    <StatCard
                        icon={DollarSign}
                        title="Pénalités en attente"
                        value={stats.pendingPenalties}
                        color="#ef4444"
                    />
                    <StatCard
                        icon={Clock}
                        title="Temps d'attente moyen"
                        value={`${stats.avgWaitTime} min`}
                        color="#8b5cf6"
                    />
                </div>
            </section>

            {/* Main Content */}
            <div className="admin-content">
                {/* Left Column - Trips Management */}
                <div className="trips-section">
                    <div className="section-header">
                        <Bus size={24} />
                        <h3>Gestion des Trajets</h3>
                    </div>

                    <div className="trips-list">
                        {trips.map(trip => (
                            <div key={trip.id} className="trip-card">
                                <div className="trip-header">
                                    <div className="trip-info">
                                        <h4>Trajet #{trip.id}</h4>
                                        <div className="trip-details">
                      <span className={`status-badge ${trip.status.toLowerCase()}`}>
                        {trip.status}
                      </span>
                                            <span><Clock size={14} /> {new Date(trip.startTime).toLocaleTimeString()}</span>
                                        </div>
                                    </div>
                                    <div className="trip-actions">
                                        {trip.status === 'IN_PROGRESS' ? (
                                            <button
                                                onClick={() => handleEndTrip(trip.id)}
                                                className="action-btn danger-btn"
                                            >
                                                <StopCircle size={16} /> Terminer
                                            </button>
                                        ) : (
                                            <button
                                                onClick={() => handleStartTrip(trip.vehicleId || 1)}
                                                className="action-btn success-btn"
                                            >
                                                <PlayCircle size={16} /> Démarrer
                                            </button>
                                        )}
                                    </div>
                                </div>

                                <div className="trip-stats">
                                    <div className="stat">
                                        <span className="label">Arrêts:</span>
                                        <span className="value">{trip.stops?.length || 0}</span>
                                    </div>
                                    <div className="stat">
                                        <span className="label">Complétés:</span>
                                        <span className="value">
                      {trip.stops?.filter(s => s.status === 'COMPLETED').length || 0}
                    </span>
                                    </div>
                                    <div className="stat">
                                        <span className="label">En attente:</span>
                                        <span className="value">
                      {trip.stops?.filter(s => s.status === 'WAITING').length || 0}
                    </span>
                                    </div>
                                </div>
                            </div>
                        ))}

                        {trips.length === 0 && (
                            <div className="empty-state">
                                <Bus size={48} />
                                <p>Aucun trajet en cours</p>
                                <button
                                    onClick={() => handleStartTrip(1)}
                                    className="primary-btn"
                                >
                                    <PlayCircle size={18} /> Démarrer un trajet
                                </button>
                            </div>
                        )}
                    </div>
                </div>

                {/* Right Column - Monitoring */}
                <div className="monitoring-section">
                    {/* Vehicles Monitoring */}
                    <div className="monitoring-card">
                        <div className="card-header">
                            <Activity size={20} />
                            <h4>Véhicules</h4>
                        </div>
                        <div className="vehicles-list">
                            {vehicles.map(vehicle => (
                                <div key={vehicle.id} className="vehicle-item">
                                    <div className="vehicle-info">
                                        <Bus size={20} className="vehicle-icon" />
                                        <div>
                                            <strong>{vehicle.plateNumber}</strong>
                                            <p className="model">{vehicle.model}</p>
                                        </div>
                                    </div>
                                    <div className="vehicle-status">
                                        <span className={`status-dot ${trips.some(t => t.vehicleId === vehicle.id && t.status === 'IN_PROGRESS') ? 'active' : 'inactive'}`} />
                                        <span>{trips.some(t => t.vehicleId === vehicle.id && t.status === 'IN_PROGRESS') ? 'En service' : 'Disponible'}</span>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Penalties */}
                    <div className="monitoring-card">
                        <div className="card-header">
                            <AlertTriangle size={20} />
                            <h4>Pénalités récentes</h4>
                        </div>
                        <div className="penalties-list">
                            {mockData.penalties.map(penalty => (
                                <div key={penalty.id} className="penalty-item">
                                    <div className="penalty-info">
                                        <strong>{penalty.reason}</strong>
                                        <p>{penalty.amount} DT</p>
                                    </div>
                                    <span className={`penalty-status ${penalty.status.toLowerCase()}`}>
                    {penalty.status}
                  </span>
                                </div>
                            ))}

                            {mockData.penalties.length === 0 && (
                                <p className="no-data">Aucune pénalité récente</p>
                            )}
                        </div>
                    </div>

                    {/* System Alerts */}
                    <div className="monitoring-card alerts-card">
                        <div className="card-header">
                            <AlertTriangle size={20} className="alert-icon" />
                            <h4>Alertes système</h4>
                        </div>
                        <div className="alerts-list">
                            {stats.activeTrips === 0 && (
                                <div className="alert-item warning">
                                    <AlertTriangle size={16} />
                                    <span>Aucun trajet actif</span>
                                </div>
                            )}
                            {stats.pendingPenalties > 0 && (
                                <div className="alert-item danger">
                                    <DollarSign size={16} />
                                    <span>{stats.pendingPenalties} pénalité(s) en attente</span>
                                </div>
                            )}
                            <div className="alert-item success">
                                <CheckCircle size={16} />
                                <span>Système opérationnel</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Quick Actions */}
            <section className="actions-section">
                <h3>Actions rapides</h3>
                <div className="actions-grid">
                    <button
                        onClick={() => handleStartTrip(1)}
                        className="action-card success"
                    >
                        <PlayCircle size={24} />
                        <span>Démarrer un trajet</span>
                    </button>
                    <button className="action-card info">
                        <Bus size={24} />
                        <span>Ajouter un véhicule</span>
                    </button>
                    <button className="action-card warning">
                        <Users size={24} />
                        <span>Ajouter un parent</span>
                    </button>
                    <button className="action-card">
                        <BarChart3 size={24} />
                        <span>Générer rapport</span>
                    </button>
                </div>
            </section>
        </div>
    );
};

export default Admin;
