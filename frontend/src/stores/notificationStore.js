import { defineStore } from 'pinia'
import { ref } from 'vue'
import { fetchNotificationsByEmail, markAllNotificationAsReadByEmail} from '@/services/notificationService'
import { jwtDecode } from 'jwt-decode'

export const useNotificationStore =
    defineStore('notification', () => {
    const notifications = ref([])
    const unreadCount = ref(0)
    const intervalId = ref(null)

    const fetchNotification = async () => {
        try {
            const token = localStorage.getItem('token')
            if (!token) return

            const decoded = jwtDecode(token)
            const email = decoded?.userId || decoded?.id || decoded?.sub

            const response = await fetchNotificationsByEmail(email)
            notifications.value = response
            unreadCount.value = response.filter(n => !n.read).length
        } catch (error) {
            console.error('Error fetching notifications:', error)
        }
    }

    const startPolling = () => {
        if (!intervalId.value) {
            fetchNotification()
            intervalId.value = setInterval(fetchNotification, 30000)
        }
    }

    const stopPolling = () => {
        if (intervalId.value) {
            clearInterval(intervalId.value)
            intervalId.value = null
        }
    }

    const markAllAsRead = async () => {
        try {
            await markAllNotificationAsReadByEmail();

            notifications.value.forEach(notification => {
                if (!notification.read) {
                    notification.read = true;
                    notification.status = 'READ';
                }
            });

            unreadCount.value = 0;
        } catch (error) {
            console.error('Error marking all notifications as read:', error);
            throw error;
        }
    }

    return { notifications, unreadCount, fetchNotification, startPolling, stopPolling, markAllAsRead }
})
