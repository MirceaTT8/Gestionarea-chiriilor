export const formatPaymentMethod = (paymentMethod) => {
    if (!paymentMethod) return ''

    const methodMap = {
        'CREDIT_CARD': 'Credit Card',
        'BANK_TRANSFER': 'Bank Transfer',
        'CASH': 'Cash'
    }

    return methodMap[paymentMethod] || paymentMethod
}
