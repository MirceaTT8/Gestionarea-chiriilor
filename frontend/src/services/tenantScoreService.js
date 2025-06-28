import { BASE_URL } from "@/configs/config.js";

const API = `${BASE_URL}/tenant-score`;

export const fetchAllTenantScores = async () => {
    try {
        const response = await fetch(`${API}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching tenant scores:', error);
        throw error;
    }
};
