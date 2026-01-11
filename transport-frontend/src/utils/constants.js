// Rôles utilisateur
export const USER_ROLES = {
    ADMIN: 'ADMIN',
    PARENT: 'PARENT',
    DRIVER: 'DRIVER'
};

// Routes par rôle
export const ROLE_ROUTES = {
    ADMIN: ['/admin', '/dashboard', '/users'],
    PARENT: ['/parent', '/tracking', '/profile'],
    DRIVER: ['/driver', '/tracking', '/schedule']
};

// Messages d'erreur
export const ERROR_MESSAGES = {
    LOGIN_FAILED: 'Email ou mot de passe incorrect',
    REGISTER_FAILED: 'Erreur lors de la création du compte',
    UNAUTHORIZED: 'Accès non autorisé',
    NETWORK_ERROR: 'Erreur réseau'
};

// Validation
export const VALIDATION = {
    PASSWORD_MIN_LENGTH: 6,
    EMAIL_REGEX: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    PHONE_REGEX: /^[0-9+\-\s()]{10,}$/
};
