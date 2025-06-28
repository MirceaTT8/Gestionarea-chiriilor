const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const GOOGLE_API_KEY = import.meta.env.VITE_GOOGLE_MAPS_API_KEY || ''
const STRIPE_PUBLIC_KEY = import.meta.env.VITE_STRIPE_PUBLIC_KEY || ''

export {
    BASE_URL,
    GOOGLE_API_KEY,
    STRIPE_PUBLIC_KEY,
};