import axios from 'axios'
import { API_BASE_URL } from '@/config'

export const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
})

// Request interceptor để thêm token
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('accessToken')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// Response interceptor để xử lý lỗi
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config

        // Nếu lỗi 401 và chưa retry
        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true

            // Xử lý refresh token ở đây
            // ...

            // Tạm thời redirect về login
            localStorage.removeItem('accessToken')
            localStorage.removeItem('refreshToken')
            window.location.href = '/login'
        }

        return Promise.reject(error)
    }
)

export default api
