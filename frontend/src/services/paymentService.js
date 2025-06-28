import { BASE_URL } from "@/configs/config.js";

const API = `${BASE_URL}/payment`;

export const getAllPayments = async () => {
    try {
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
    } catch (error) {
        console.error('Error fetching all payments:', error);
        throw error;
    }
};

export const fetchPaymentsForOwner = async () => {
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
};


export const createPayment = async (leaseId, paymentRequest) => {
    try {
        const token = localStorage.getItem('token');

        const response = await fetch(`${API}/${leaseId}`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(paymentRequest)
        });

        if (!response.ok) {
            let errorData = null;
            try {
                errorData = await response.json();
            } catch (_) {}

            throw new Error(errorData?.message || `Payment failed with status: ${response.status}`);
        }

        const contentLength = response.headers.get('content-length');
        if (contentLength && parseInt(contentLength) > 0) {
            return await response.json();
        }

        return {};
    } catch (error) {
        console.error('Error creating payment:', error);
        throw error;
    }
};

export const getPaymentsByLeaseId = async (leaseId) => {
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
        console.error(`Error fetching payments for lease ${leaseId}:`, error);
        throw error;
    }
};

export const getPaymentsByOwnerId = async (userId) => {
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
        console.error(`Error fetching payments for lease ${userId}:`, error);
        throw error;
    }
};

export const getPaymentById = async (paymentId) => {
    try {
        const response = await fetch(`${API}/${paymentId}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error(`Error fetching payment ${paymentId}:`, error);
        throw error;
    }
};

export const createStripeCheckoutSession = async (leaseId) => {
    const token = localStorage.getItem('token');


    const response = await fetch(`${API}/stripe/create-session`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ leaseId })
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `Stripe session creation failed with status: ${response.status}`);
    }

    return await response.json();
};

export const confirmStripePayment = async (sessionId) => {
    const token = localStorage.getItem('token');

    const response = await fetch(`${API}/stripe/confirm?session_id=${sessionId}`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || `Failed to confirm payment. Status: ${response.status}`);
    }

    return await response.json();
};

export const isPaymentMadeThisMonth = async (leaseId) => {
    try {
        const response = await fetch(`${API}/lease/${leaseId}/paid-this-month`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        })

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`)
        }

        return await response.json() // should return true or false
    } catch (error) {
        console.error(`Error checking payment status for lease ${leaseId}:`, error)
        throw error
    }
}

export const isPaymentMadeThisCycle = async (leaseId) => {
    try {
        const response = await fetch(`${API}/lease/${leaseId}/paid-this-cycle`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json(); // true or false
    } catch (error) {
        console.error(`Error checking payment status for lease ${leaseId}:`, error);
        throw error;
    }
};

export const getLatestPaymentForLease = async (leaseId) => {
    try {
        const response = await fetch(`${API}/lease/${leaseId}/latest-payment`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json(); // returns PaymentDTO with paymentDate
    } catch (error) {
        console.error(`Error fetching latest payment for lease ${leaseId}:`, error);
        throw error;
    }
};

export const fetchPaymentsForLease = async (leaseId) => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API}/lease/${leaseId}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error(`Failed to fetch payments for lease ${leaseId}`);
    }

    return await response.json();
};

