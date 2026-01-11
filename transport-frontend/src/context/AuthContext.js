import React, { createContext, useState, useContext, useEffect } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext({});

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        checkAuth();
    }, []);

    const checkAuth = async () => {
        const token = localStorage.getItem('token');

        if (!token) {
            setLoading(false);
            return;
        }

        try {
            const response = await authAPI.getProfile();
            setUser(response.data);
        } catch (error) {
            authAPI.logout();
        } finally {
            setLoading(false);
        }
    };

    const login = async (email, password) => {
        const response = await authAPI.login(email, password);
        const userData = response.data;

        localStorage.setItem('token', userData.token);
        localStorage.setItem('userId', userData.userId);
        localStorage.setItem('userRole', userData.role);

        if (userData.role === 'PARENT') {
            localStorage.setItem('parentId', userData.userId);
        }

        setUser(userData);
        return userData;
    };

    const register = async (userData) => {
        const response = await authAPI.register(userData);
        return response.data;
    };

    const logout = () => {
        authAPI.logout();
        setUser(null);
        window.location.href = '/login';
    };

    const value = {
        user,
        login,
        register,
        logout,
        loading
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};
