import { BASE_URL } from "@/configs/config.js";

const API = `${BASE_URL}/notification`;

export const fetchNotifications = async (userId) => {
    try {
        const response = await fetch(`${API}/user/${userId}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching notifications:', error);
        throw error;
    }
};

export const fetchNotificationsByEmail = async (email) => {
    try {
        const token = localStorage.getItem('token')
        const response = await fetch(`http://localhost:8080/notification/user/email/${encodeURIComponent(email)}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        })

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`)
        }

        return await response.json()
    } catch (error) {
        console.error('Error fetching notifications:', error)
        throw error
    }
}


export const markNotificationAsRead = async (notificationId) => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/${notificationId}/read`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error marking notification as read:', error);
        throw error;
    }
};

export const markAllNotificationAsReadByEmail = async (email) => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`http://localhost:8080/notification/user/email/${encodeURIComponent(email)}/read-all`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error marking notification as read:', error);
        throw error;
    }
};


