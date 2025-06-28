
export function formatTimeAgo(timestamp) {
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

export const formatDate = (dateString) => {
    const options = { year: 'numeric', month: 'long', day: 'numeric' }
    return new Date(dateString).toLocaleDateString(undefined, options)
}
