import { BASE_URL } from "@/configs/config.js";

const API = `${BASE_URL}/activity-log`;

export const fetchAllLogs = async () => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching logs:', error);
        throw error;
    }
};

export const fetchRecentLogs = async () => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/recent`, {
            method: 'GET',
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
        console.error('Error fetching recent logs:', error);
        throw error;
    }
};
