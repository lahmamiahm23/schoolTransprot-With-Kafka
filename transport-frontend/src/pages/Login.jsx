import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authAPI } from '../services/api';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const response = await authAPI.login(email, password);

            if (response.data && response.data.userId) {
                // Stocker l'ID utilisateur et le token
                localStorage.setItem('userId', response.data.userId);
                localStorage.setItem('token', response.data.token);
                localStorage.setItem('userRole', response.data.role);

                // Rediriger selon le rôle
                if (response.data.role === 'PARENT') {
                    localStorage.setItem('parentId', response.data.userId);
                    navigate('/parent');
                } else if (response.data.role === 'ADMIN') {
                    navigate('/admin');
                } else if (response.data.role === 'DRIVER') {
                    navigate('/driver');
                } else {
                    navigate('/');
                }
            } else {
                setError('Réponse invalide du serveur');
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Erreur de connexion');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-card">
                <h2>Connexion</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Email</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            placeholder="votre@email.com"
                        />
                    </div>

                    <div className="form-group">
                        <label>Mot de passe</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            placeholder="••••••••"
                        />
                    </div>

                    {error && <div className="error-message">{error}</div>}

                    <button type="submit" disabled={loading} className="btn-primary">
                        {loading ? 'Connexion...' : 'Se connecter'}
                    </button>
                </form>

                <div className="auth-links">
                    <Link to="/register">Créer un compte</Link>
                    <Link to="/">Retour à l'accueil</Link>
                </div>

                <div className="demo-credentials">
                    <p><strong>Démonstration :</strong></p>
                    <p>Parent: parent@test.com / parent123</p>
                    <p>Admin: admin@test.com / admin123</p>
                    <p>Conducteur: driver@test.com / driver123</p>
                </div>
            </div>
        </div>
    );
};

export default Login;
