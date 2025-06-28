import {BASE_URL} from "@/configs/config.js";

const API = `${BASE_URL}/property`;

export const fetchMyProperties = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API}/user/me`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
}

export const fetchPropertyById = async (propertyId) => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/${propertyId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching properties:', error);
        throw error;
    }
};


export const addProperty = async (propertyDTO, attachments) => {
    try {
        const formData = new FormData();

        const jsonBlob = new Blob(
            [JSON.stringify(propertyDTO)],
            { type: 'application/json' }
        );

        formData.append('propertyDTO', jsonBlob);

        if (attachments?.length) {
            attachments.forEach(attachment => {
                formData.append('images', attachment.file);
            });
        }

        const response = await fetch(`${API}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: formData
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch(error) {
        console.error('Adding property failed:', error);
        throw error;
    }
}

export const fetchPendingProperties = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API}/pending`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error('Failed to fetch pending properties');
    }

    return await response.json();
};

export const fetchAllPropertiesAdmin = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
};

export const validateProperty = async (propertyId, status) => {
    const token = localStorage.getItem('token');

    const response = await fetch(`${API}/${propertyId}/validate?status=${status}`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData || `Failed to validate property ${propertyId}`);
    }

    return await response.text();
};
