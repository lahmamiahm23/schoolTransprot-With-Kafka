import { Client } from '@stomp/stompjs';

let stompClient = null;

export const connectSocket = (onLocationReceived) => {
    stompClient = new Client({
        brokerURL: 'ws://localhost:8081/ws', // Vérifiez votre config WebSocket Spring
        onConnect: () => {
            stompClient.subscribe('/topic/vehicles', (message) => {
                onLocationReceived(JSON.parse(message.body));
            });
        },
    });
    stompClient.activate();
};

export const disconnectSocket = () => {
    if (stompClient) stompClient.deactivate();
};
