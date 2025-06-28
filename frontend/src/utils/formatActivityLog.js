import { formatTimeAgo } from './dateUtils.js'

export function formatActivityLogs(logs) {
    return logs.map(log => {
        let icon = '✉️'
        let description = log.actionType
        let parsedDetails = {}

        try {
            parsedDetails = typeof log.details === 'string'
                ? JSON.parse(log.details)
                : log.details
        } catch {
            parsedDetails = {}
        }

        // Helper: Capitalize a status string (e.g. "PENDING" → "Pending")
        const formatStatus = s =>
            typeof s === 'string' ? s.charAt(0).toUpperCase() + s.slice(1).toLowerCase().replace(/_/g, ' ') : '?'

        // Helper: Convert actionType from SNAKE_CASE to readable text
        const formatAction = s =>
            typeof s === 'string' ? s.toLowerCase().replace(/_/g, ' ').replace(/^\w/, c => c.toUpperCase()) : 'Unknown action'

        switch (log.actionType) {
            case 'CREATE_PROPERTY':
                icon = '🏠'
                description = `Created property “${parsedDetails.name || 'Unnamed'}” at ${parsedDetails.address || 'unknown address'}`
                break

            case 'DELETE_PROPERTY':
                icon = '❌'
                description = typeof log.details === 'string'
                    ? log.details
                    : `Deleted property #${log.entityId}`
                break

            case 'CREATE_LEASE':
                icon = '📄'
                description = `Created lease for property #${parsedDetails.propertyId || '?'}`
                break

            case 'PROCESS_PAYMENT':
                icon = '💰'
                description = `Processed payment of €${parsedDetails.amount || '?'} for ${parsedDetails.property || 'a property'}`
                break

            case 'SEND_PAYMENT_NOTIFICATION':
                icon = '💬'
                description = typeof log.details === 'string'
                    ? log.details
                    : 'Payment notification sent'
                break

            case 'CREATE_MAINTENANCE_REQUEST':
                icon = '🛠️'
                description = `Maintenance request: “${parsedDetails.description || 'No description'}” (Status: ${formatStatus(parsedDetails.status)})`
                break

            case 'UPDATE_MAINTENANCE_STATUS':
                icon = '🛠️'
                description = `Maintenance updated: ${formatStatus(parsedDetails.oldStatus)} → ${formatStatus(parsedDetails.newStatus)}`
                break

            default:
                icon = '🔔'
                description = formatAction(log.actionType)
        }

        return {
            id: log.id,
            icon,
            description,
            time: formatTimeAgo(log.createdAt),
            createdAt: log.createdAt,
            actionType: log.actionType,
            entityType: log.entityType
        }
    })
}
