import { BASE_URL } from "@/configs/config.js";

const API = `${BASE_URL}/review`;

export const createReview = async (reviewData) => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(reviewData)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error creating review:', error);
        throw error;
    }
};

export const fetchPendingReviews = async () => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/pending`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            if (response.status === 404) {
                return []; // No pending reviews
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching pending reviews:', error);
        throw error;
    }
};

export const fetchPendingReviewsCount = async () => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/pending/count`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            if (response.status === 404) {
                return { count: 0, hasPendingReviews: false };
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching pending reviews count:', error);
        throw error;
    }
};

export const getReviewById = async (reviewId) => {
    try {
        const response = await fetch(`${API}/${reviewId}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching review:', error);
        throw error;
    }
};

export const getReviewsByProperty = async (propertyId) => {
    try {
        const response = await fetch(`${API}/property/${propertyId}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching property reviews:', error);
        throw error;
    }
};

export const getReviewsByTenant = async (tenantId) => {
    try {
        const response = await fetch(`${API}/tenant/${tenantId}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching tenant reviews:', error);
        throw error;
    }
};

export const getReviewsByLandlord = async (landlordId) => {
    try {
        const response = await fetch(`${API}/landlord/${landlordId}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching landlord reviews:', error);
        throw error;
    }
};

export const updateReview = async (reviewId, reviewData) => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/${reviewId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(reviewData)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error updating review:', error);
        throw error;
    }
};

export const deleteReview = async (reviewId) => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/${reviewId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return true;
    } catch (error) {
        console.error('Error deleting review:', error);
        throw error;
    }
};

export const createLandlordReview = async (reviewData) => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/landlord-to-tenant`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(reviewData)
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Review creation failed with status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error creating landlord review:', error);
        throw error;
    }
};

export const fetchReviewsForTenant = async (tenantId) => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/tenant/${tenantId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching tenant reviews:', error);
        throw error;
    }
};

export const fetchReviewsForProperty = async (propertyId) => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/property/${propertyId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching property reviews:', error);
        throw error;
    }
};

export const getReviewedLeaseIds = async () => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/landlord/reviewed-leases`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        return response.ok ? await response.json() : [];
    } catch (error) {
        console.error('Error fetching reviewed lease IDs:', error);
        return [];
    }
};

export const checkIfPropertyReviewed = async (propertyId) => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/check-reviewed/${propertyId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        return result.reviewed;
    } catch (error) {
        console.error('Error checking if property reviewed:', error);
        return false;
    }
};