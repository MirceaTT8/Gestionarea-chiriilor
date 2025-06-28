export const getPropertyNameByLeaseId = (leaseId, leases = [], properties = []) => {
    const lease = leases.find(l => l.leaseId === leaseId)
    if (!lease) return 'Unknown Lease'
    const property = properties.find(p => p.propertyId === lease.propertyId)
    return property ? property.name : 'Unknown Property'
}

export const getTenantNameByLeaseId = (leaseId, leases = [], tenants = []) => {
    const lease = leases.find(l => l.leaseId === leaseId)
    if (!lease) return 'Unknown Lease'
    const tenant = tenants.find(t => t.userId === lease.tenantId)
    return tenant ? `${tenant.firstName} ${tenant.lastName}` : 'Unknown Tenant'
}
export const getUserFullName = (user) => {
    if (!user) return 'Unknown User'
    const firstName = user.firstName || ''
    const lastName = user.lastName || ''
    const fullName = `${firstName} ${lastName}`.trim()
    return fullName || 'Unknown User'
}

export const getUserNameById = (userId, users = []) => {
    if (!userId || !Array.isArray(users)) return `User #${userId || 'Unknown'}`
    const user = users.find(u => u.userId === userId || u.id === userId)
    if (user) {
        return getUserFullName(user)
    }
    return `User #${userId}`
}

export const getPropertyNameById = (propertyId, properties = []) => {
    if (!propertyId || !Array.isArray(properties)) return `Property #${propertyId || 'Unknown'}`
    const property = properties.find(p => p.propertyId === propertyId || p.id === propertyId)
    if (property?.name) {
        return property.name
    }
    return `Property #${propertyId}`
}

export const getTenantDisplayName = (tenantStats, users = []) => {
    console.log('TenantStats:', tenantStats)
    console.log('Users array:', users)

    if (!tenantStats) return 'Unknown Tenant'

    if (tenantStats.firstName && tenantStats.lastName) {
        return `${tenantStats.firstName} ${tenantStats.lastName}`
    }

    if (tenantStats.tenantId && users.length > 0) {
        const user = users.find(u => u.userId === tenantStats.tenantId)
        console.log('Found user for tenantId', tenantStats.tenantId, ':', user)
        if (user && user.firstName && user.lastName) {
            return `${user.firstName} ${user.lastName}`
        }
    }

    if (tenantStats.name && tenantStats.name !== 'undefined undefined' && tenantStats.name !== 'Unknown') {
        return tenantStats.name
    }

    return `Tenant #${tenantStats.tenantId || 'Unknown'}`
}

export const getLandlordDisplayName = (landlordStats, users = []) => {
    console.log('LandlordStats:', landlordStats)
    console.log('Users array:', users)

    if (!landlordStats) return 'Unknown Landlord'

    if (landlordStats.firstName && landlordStats.lastName) {
        return `${landlordStats.firstName} ${landlordStats.lastName}`
    }

    if (landlordStats.landlordId && users.length > 0) {
        const user = users.find(u => u.userId === landlordStats.landlordId)
        if (user && user.firstName && user.lastName) {
            return `${user.firstName} ${user.lastName}`
        }
    }

    if (landlordStats.name && landlordStats.name !== 'undefined undefined' && landlordStats.name !== 'Unknown') {
        return landlordStats.name
    }

    return `Landlord #${landlordStats.landlordId || 'Unknown'}`
}