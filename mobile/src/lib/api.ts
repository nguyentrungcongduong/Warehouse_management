import axios from 'axios'
import * as SecureStore from 'expo-secure-store'
import { API_BASE_URL } from '@/config'

export const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
})

// Request interceptor để thêm token
api.interceptors.request.use(
    async (config) => {
        const token = await SecureStore.getItemAsync('accessToken')
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

            // Xóa tokens và redirect về login
            await SecureStore.deleteItemAsync('accessToken')
            await SecureStore.deleteItemAsync('refreshToken')
            // Navigation sẽ được xử lý ở auth store
        }

        return Promise.reject(error)
    }
)

export default api
