import { BASE_URL } from "@/configs/config.js";
const API = `${BASE_URL}/user`;

export const fetchUserById = async (userId) => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/${userId}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching user:', error);
        throw error;
    }
};

export const fetchUserByEmail = async (email) => {
    try {
        const token = localStorage.getItem('token');

        const response = await fetch(`${API}/email/${encodeURIComponent(email)}`, {
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
        console.error('Error fetching user:', error);
        throw error;
    }
};

export const fetchAllUsers = async () => {
    try {
        const token = localStorage.getItem('token');

        const response = await fetch(`${API}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        return await response.json();
    } catch (error) {
        console.error('Error fetching users:', error);
        throw error;
    }
};

export const createUser = async (userData) => {
    try {
        const response = await fetch(API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error creating user:', error);
        throw error;
    }
};

export const deleteUser = async (userId) => {
    try {
        const response = await fetch(`${API}?id=${userId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error deleting user:', error);
        throw error;
    }
};

export const approveUsers = async (userIds) => {
    try {
        const response = await fetch(`${API}/approve`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userIds)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error approving users:', error);
        throw error;
    }
};

export const suspendUsers = async (userIds) => {
    try {
        const response = await fetch(`${API}/suspend`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userIds)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error suspending users:', error);
        throw error;
    }
};

export const updateUserRole = async (userId, role) => {
    try {
        const response = await fetch(`${API}/${userId}/role`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ role })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error updating user role:', error);
        throw error;
    }
};
export const activateUser = async (userId) => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/${userId}/activate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        })

        if (!response.ok) {
            const errorData = await response.json()
            throw new Error(errorData.message || 'User activation failed')
        }

        const contentLength = response.headers.get('Content-Length')
        if (contentLength && parseInt(contentLength) > 0) {
            return await response.json()
        }
        return { success: true }
    } catch (error) {
        console.error('Error activating user:', error)
        throw error
    }
}

export const deactivateUser = async (userId) => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/${userId}/deactivate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        })

        if (!response.ok) {
            const errorData = await response.json()
            throw new Error(errorData.message || 'User deactivation failed')
        }

        const contentLength = response.headers.get('Content-Length')
        if (contentLength && parseInt(contentLength) > 0) {
            return await response.json()
        }
        return { success: true }
    } catch (error) {
        console.error('Error deactivating user:', error)
        throw error
    }
}

export const updateUserPhone = async (userId, phone) => {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API}/${userId}/phone`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ phone })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error updating phone number:', error);
        throw error;
    }
};