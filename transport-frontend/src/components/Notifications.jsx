import React from 'react';

const Notifications = ({ notifications, onMarkAllRead }) => {
    const unreadCount = notifications.filter(n => !n.read).length;

    if (notifications.length === 0) {
        return (
            <div className="notifications">
                <div className="notifications-header">
                    <h3>Notifications</h3>
                    <span className="badge">0</span>
                </div>
                <p className="empty">Aucune notification</p>
            </div>
        );
    }

    return (
        <div className="notifications">
            <div className="notifications-header">
                <h3>Notifications</h3>
                <div className="header-actions">
                    <span className="badge">{unreadCount} non lues</span>
                    <button onClick={onMarkAllRead} className="btn-small">
                        Tout marquer lu
                    </button>
                </div>
            </div>

            <div className="notifications-list">
                {notifications.slice(0, 5).map((notif, index) => (
                    <div
                        key={index}
                        className={`notification-item ${notif.read ? 'read' : 'unread'}`}
                    >
                        <div className="notification-icon">
                            {notif.type === 'ETA_ALARM' ? '⚠️' : '📢'}
                        </div>
                        <div className="notification-content">
                            <p className="notification-message">{notif.message}</p>
                            <small className="notification-time">
                                {new Date(notif.sentAt).toLocaleString()}
                            </small>
                        </div>
                    </div>
                ))}
            </div>

            {notifications.length > 5 && (
                <div className="notifications-footer">
                    <small>{notifications.length - 5} notifications plus anciennes</small>
                </div>
            )}
        </div>
    );
};

export default Notifications;
