import {BASE_URL} from "@/configs/config.js";
const API = `${BASE_URL}/maintenance`;

export const fetchMaintenanceRequestsByLease = async (leaseId) => {
    try {
        const response = await fetch(`${API}/lease/${leaseId}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching maintenance requests:', error);
        throw error;
    }
};

export const fetchMaintenanceRequestsByOwner = async (userId) => {
    try {
        const response = await fetch(`${API}/owner/${userId}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching maintenance requests:', error);
        throw error;
    }
};

export const fetchMaintenanceRequestsByLoggedInOwner = async () => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/owner/me`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching maintenance requests:', error);
        throw error;
    }
};


export const setStatus = async (requestId, status) => {
    try {
        const token = localStorage.getItem('token');

        const response = await fetch(`${API}/${requestId}/status?status=${status}`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to update status. HTTP status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error setting status:', error);
        throw error;
    }
};


export const addMaintenanceRequest = async (leaseId, requestData) => {
    try {
        const token = localStorage.getItem('token');
        if (!token) throw new Error("Unauthorized: No token found");

        const formData = new FormData();

        const jsonBlob = new Blob(
            [JSON.stringify({ description: requestData.description })],
            { type: 'application/json' }
        );

        formData.append('requestDTO', jsonBlob);

        if (requestData.attachments?.length) {
            requestData.attachments.forEach(attachment => {
                formData.append('images', attachment.file);
            });
        }

        const response = await fetch(`${API}/lease/${leaseId}`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

        if (!response.ok) {
            const errText = await response.text();
            throw new Error(`HTTP error! status: ${response.status}, message: ${errText}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error adding maintenance request:', error);
        throw error;
    }
};

export const setMaintenanceCost = async (requestId, cost) => {
    try {
        const token = localStorage.getItem('token')
        const response = await fetch(`${API}/${requestId}/cost?cost=${cost}`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })

        if (!response.ok) {
            throw new Error(`Failed to set cost. HTTP status: ${response.status}`)
        }

        return await response.json()
    } catch (error) {
        console.error('Error setting maintenance cost:', error)
        throw error
    }
}

export const fetchAllMaintenanceRequests = async () => {
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
        console.error('Error fetching all maintenance requests:', error);
        throw error;
    }
};

export const markMaintenanceAsNotFixed = async (requestId) => {
    try {
        const token = localStorage.getItem('token')
        const response = await fetch(`${API}/${requestId}/mark-not-fixed`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to mark maintenance as not fixed: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error marking maintenance as not fixed:', error);
        throw error;
    }
};

