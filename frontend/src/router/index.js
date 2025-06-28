import { createRouter, createWebHistory } from 'vue-router'
import { jwtDecode } from 'jwt-decode'

import ProfileView from "@/views/ProfileView.vue"
import DashboardView from "@/views/landlord/DashboardView.vue"
import LoginView from "@/views/LoginView.vue"
import RegisterView from "@/views/RegisterView.vue"
import InvitedRegisterView from "@/views/InvitedRegisterView.vue";
import LogsView from "@/views/admin/LogsView.vue"
import TenantRatingsView from "@/views/admin/TenantRatingsView.vue";
import AdminDashboardView from "@/views/admin/AdminDashboardView.vue"
import UserManagementView from "@/views/admin/UserManagementView.vue"

const PropertyView = () => import('@/views/landlord/PropertyView.vue')
const PropertyDetailsView = () => import('@/views/landlord/PropertyDetailsView.vue')
const LeaseView = () => import('@/views/landlord/LeaseView.vue')
const MaintenanceRequestView = () => import('@/views/landlord/MaintenanceRequestView.vue')
const PaymentView = () => import('@/views/landlord/PaymentView.vue')
const AddPropertyView = () => import('@/views/landlord/AddPropertyView.vue')

const LeaseTenantView = () => import('@/views/tenant/LeaseTenantView.vue')
const PaymentTenantView = () => import('@/views/tenant/PaymentTenantView.vue')
const MaintenanceRequestTenantView = () => import('@/views/tenant/MaintenanceRequestTenantView.vue')
const PaymentSuccessView = () => import('@/views/tenant/PaymentSuccessView.vue')

const AdminPaymentsView = () => import('@/views/admin/AdminPaymentView.vue')
const AdminMaintenanceView = () => import('@/views/admin/AdminMaintenanceView.vue')
const AdminPropertyManagementView = () => import('@/views/admin/AdminPropertyView.vue')
const AdminLeaseView = () => import('@/views/admin/AdminLeaseView.vue')
const LandlordRatingsView = () => import ('@/views/admin/LandlordRatingsView.vue')


const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [

        {
            path: '/:pathMatch(.*)*',
            redirect: () => {
                const token = localStorage.getItem('token')
                if (token) {
                    const { authorities } = jwtDecode(token)
                    if (authorities.includes('ROLE_LANDLORD')) return '/landlord'
                    if (authorities.includes('ROLE_TENANT')) return '/tenant/leases'
                    if (authorities.includes('ROLE_ADMIN')) return '/admin/dashboard'
                }
                return '/login'
            }
        },
        { path: '/profile/:userId?', name: 'Profile', component: ProfileView },
        { path: '/payment/success', name: 'PaymentSuccess', component: PaymentSuccessView },
        { path: '/register', name: 'Register', component: RegisterView },
        { path: '/login', name: 'Login', component: LoginView },
        { path: '/invite', name: 'InvitedRegister', component: InvitedRegisterView },
        { path: '/landlord', name: 'Dashboard', component: DashboardView },
        { path: '/landlord/properties', name: 'Property', component: PropertyView },
        { path: '/landlord/property/:id', name: 'PropertyDetails', component: PropertyDetailsView },
        { path: '/landlord/leases', name: 'Lease', component: LeaseView },
        { path: '/landlord/payments', name: 'Payment', component: PaymentView },
        { path: '/landlord/maintenance', name: 'Maintenance', component: MaintenanceRequestView },
        { path: '/landlord/add-property', name: 'AddProperty', component: AddPropertyView },

        { path: '/tenant/leases', name: 'LeaseTenant', component: LeaseTenantView },
        { path: '/tenant/payments', name: 'PaymentTenant', component: PaymentTenantView },
        { path: '/tenant/maintenance', name: 'MaintenanceTenant', component: MaintenanceRequestTenantView },

        { path: '/admin/dashboard', name: 'AdminDashboard', component: AdminDashboardView },
        { path: '/admin/users', name: 'UserManagement', component: UserManagementView },
        { path: '/admin/logs', name: 'SystemLogs', component: LogsView },
        { path: '/admin/payments', name: 'AdminPayments', component: AdminPaymentsView },
        { path: '/admin/maintenance', name: 'AdminMaintenance', component: AdminMaintenanceView },
        { path: '/admin/properties', name: 'AdminPropertyManagement', component: AdminPropertyManagementView },
        { path: '/admin/leases', name: 'AdminLease', component: AdminLeaseView },
        { path: '/admin/ratings', name: 'LandlordRatings', component: LandlordRatingsView },
        { path: '/admin/tenant-ratings', name: 'TenantRatings', component: TenantRatingsView },


    ]
})

router.beforeEach((to, from, next) => {
    const publicPages = ['/login', '/register', '/payment/success', '/invite']
    const authRequired = !publicPages.includes(to.path.split('?')[0])
    const token = localStorage.getItem('token')

    if (authRequired && !token) return next('/login')
    if (token) {
        try {
            const { authorities } = jwtDecode(token)

            if (to.path.startsWith('/tenant')
                && !authorities.includes('ROLE_TENANT') && !authorities.includes('ROLE_ADMIN')) {
                return next('/login')
            }
            if (to.path.startsWith('/landlord')
                && !authorities.includes('ROLE_LANDLORD') && !authorities.includes('ROLE_ADMIN') ) {
                return next('/login')
            }
            if (to.path.startsWith('/admin') && !authorities.includes('ROLE_ADMIN')) {
                return next('/login')
            }
        } catch (e) {
            localStorage.removeItem('token')
            return next('/login')
        }
    }

    next()
})

export default router
