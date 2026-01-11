import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authAPI } from '../services/api';

const Register = () => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        password: '',
        confirmPassword: '',
        role: 'PARENT'
    });

    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        // Validation
        if (formData.password !== formData.confirmPassword) {
            setError('Les mots de passe ne correspondent pas');
            return;
        }

        if (formData.password.length < 6) {
            setError('Le mot de passe doit faire au moins 6 caractères');
            return;
        }

        setLoading(true);

        try {
            const response = await authAPI.register(formData);

            setSuccess('Compte créé avec succès !');

            // Auto-login après inscription
            setTimeout(async () => {
                try {
                    const loginResponse = await authAPI.login(formData.email, formData.password);

                    localStorage.setItem('userId', loginResponse.data.userId);
                    localStorage.setItem('token', loginResponse.data.token);
                    localStorage.setItem('userRole', loginResponse.data.role);

                    if (loginResponse.data.role === 'PARENT') {
                        localStorage.setItem('parentId', loginResponse.data.userId);
                        navigate('/parent');
                    } else {
                        navigate('/');
                    }
                } catch (loginErr) {
                    navigate('/login');
                }
            }, 2000);

        } catch (err) {
            setError(err.response?.data?.message || 'Erreur lors de l\'inscription');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-card">
                <h2>Créer un compte</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-row">
                        <div className="form-group">
                            <label>Prénom</label>
                            <input
                                type="text"
                                name="firstName"
                                value={formData.firstName}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label>Nom</label>
                            <input
                                type="text"
                                name="lastName"
                                value={formData.lastName}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Email</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Téléphone</label>
                        <input
                            type="tel"
                            name="phone"
                            value={formData.phone}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Type de compte</label>
                        <select
                            name="role"
                            value={formData.role}
                            onChange={handleChange}
                            className="role-select"
                        >
                            <option value="PARENT">Parent</option>
                            <option value="DRIVER">Conducteur</option>
                        </select>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Mot de passe</label>
                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label>Confirmation</label>
                            <input
                                type="password"
                                name="confirmPassword"
                                value={formData.confirmPassword}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    {error && <div className="error-message">{error}</div>}
                    {success && <div className="success-message">{success}</div>}

                    <button type="submit" disabled={loading} className="btn-primary">
                        {loading ? 'Inscription...' : 'S\'inscrire'}
                    </button>
                </form>

                <div className="auth-links">
                    <Link to="/login">Déjà un compte ? Se connecter</Link>
                    <Link to="/">Retour à l'accueil</Link>
                </div>
            </div>
        </div>
    );
};

export default Register;
