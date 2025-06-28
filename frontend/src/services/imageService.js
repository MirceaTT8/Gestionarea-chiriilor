import { BASE_URL } from "@/configs/config.js";

const API = `${BASE_URL}/image`;

export const uploadImage = async (multipartImage) => {
    try {
        const formData = new FormData();
        formData.append('multipartImage', multipartImage);

        const token = localStorage.getItem('token');
        const response = await fetch(`${API}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error uploading image:', error);
        throw error;
    }
};

export const downloadImage = async (imageId) => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/${imageId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return response;
    } catch (error) {
        console.error('Error downloading image:', error);
        throw error;
    }
};