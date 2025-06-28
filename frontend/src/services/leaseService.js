import {BASE_URL} from "@/configs/config.js";

const API = `${BASE_URL}/lease`;

export const fetchAllLeases= async () => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error(`Failed to fetch leases: ${response.status}`);
    }

    return await response.json();
};

export const fetchActiveLeasesByOwnerId = async (userId) => {
    try {
        const response = await fetch(`${API}/active/${userId}`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
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

export const fetchMyLease = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API}/user/me`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    const text = await response.text();
    console.log(text)
    return text ? JSON.parse(text) : null;
}

export const fetchAllTenantLeases = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API}/tenant/all`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        if (response.status === 404) {
            return [];
        }
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    const text = await response.text();
    return text ? JSON.parse(text) : [];
};

export const fetchMyLeases = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API}/owner/me`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    const text = await response.text();
    return text ? JSON.parse(text) : [];
}

export const approveLeaseTermination = async (leaseId) => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API}/${leaseId}/terminate-decision?decision=APPROVED`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to approve lease termination');
    }

    return await response.json();
};

export const fetchPendingLeaseTerminations = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API}/termination-requests`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error('Failed to fetch pending lease terminations');
    }

    return await response.json();
};

export const fetchPendingLeases = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${BASE_URL}/lease/pending`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error('Failed to fetch pending leases');
    }

    return await response.json();
};

export const approveLease = async (leaseId) => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API}/${leaseId}/approve`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to approve lease');
    }

    return await response.json();
};

export const rejectLease = async (leaseId) => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API}/${leaseId}/reject`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to reject lease');
    }

    return await response.json();
};

export const requestLeaseTermination = async (leaseId) => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API}/${leaseId}/terminate-request`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || 'Failed to request lease termination');
    }

    const text = await response.text();
    return text ? JSON.parse(text) : null;
};

export const createInvitation = async (leaseData) => {
    const response = await fetch(`${API}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(leaseData)
    })

    if (!response.ok) {
        throw new Error(`Failed to send invitation: ${response.statusText}`)
    }

    return await response.text()
}

export const fetchLeaseTrends = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API}/dashboard/lease-trends`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error('Failed to fetch lease trends');
    }

    return await response.json();
};

export const fetchActiveTenantLease = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API}/tenant/active`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        if (response.status === 404) {
            return null;
        }
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    const text = await response.text();
    return text ? JSON.parse(text) : null;
};

export const checkTenantHasLease = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API}/tenant/exists`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json(); // Should return boolean
};





