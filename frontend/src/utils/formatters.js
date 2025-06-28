
export const formatDate = (dateString, options = {}) => {
    const defaultOptions = {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    }

    const finalOptions = { ...defaultOptions, ...options }
    return new Date(dateString).toLocaleDateString('en-US', finalOptions)
}

export const formatDateShort = (dateString) => {
    return formatDate(dateString, { month: 'short' })
}

export const formatDateCompact = (dateString) => {
    return formatDate(dateString, { month: 'numeric' })
}

export const formatCurrency = (amount, options = {}) => {
    const defaultOptions = {
        style: 'currency',
        currency: 'USD'
    }

    const finalOptions = { ...defaultOptions, ...options }
    return new Intl.NumberFormat('en-US', finalOptions).format(amount)
}

export const formatCurrencyCompact = (amount) => {
    return formatCurrency(amount, {
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
    })
}

export const formatCurrencyPrecise = (amount) => {
    return formatCurrency(amount, {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    })
}

export const formatTimeAgo = (timestamp) => {
    const time = new Date(timestamp)
    const now = new Date()
    const diff = Math.floor((now - time) / 1000 / 60)

    if (diff < 1) return 'just now'
    if (diff < 60) return `${diff} min ago`

    const hours = Math.floor(diff / 60)
    if (hours < 24) return `${hours} hr${hours > 1 ? 's' : ''} ago`

    const days = Math.floor(hours / 24)
    if (days === 1) return 'yesterday'
    return `${days} day${days > 1 ? 's' : ''} ago`
}

export const formatStatus = (status) => {
    if (typeof status !== 'string') return '?'
    return status.charAt(0).toUpperCase() + status.slice(1).toLowerCase().replace(/_/g, ' ')
}

export const formatAction = (actionType) => {
    if (typeof actionType !== 'string') return 'Unknown action'
    return actionType.toLowerCase().replace(/_/g, ' ').replace(/^\w/, c => c.toUpperCase())
}

export const formatPercentage = (value, options = {}) => {
    const defaultOptions = {
        style: 'percent',
        minimumFractionDigits: 0,
        maximumFractionDigits: 1
    }

    const finalOptions = { ...defaultOptions, ...options }
    return new Intl.NumberFormat('en-US', finalOptions).format(value / 100)
}

export const formatNumber = (value, options = {}) => {
    const defaultOptions = {
        minimumFractionDigits: 0,
        maximumFractionDigits: 2
    }

    const finalOptions = { ...defaultOptions, ...options }
    return new Intl.NumberFormat('en-US', finalOptions).format(value)
}