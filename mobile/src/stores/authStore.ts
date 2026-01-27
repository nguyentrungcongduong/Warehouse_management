import { create } from 'zustand'
import * as SecureStore from 'expo-secure-store'

export interface User {
    id: number
    username: string
    email: string
    fullName: string
    role: 'ADMIN' | 'WAREHOUSE_MANAGER' | 'STAFF'
    avatar?: string
}

interface AuthState {
    user: User | null
    accessToken: string | null
    refreshToken: string | null
    isAuthenticated: boolean
    isLoading: boolean
    setAuth: (user: User, accessToken: string, refreshToken: string) => Promise<void>
    logout: () => Promise<void>
    updateUser: (user: Partial<User>) => void
    loadAuth: () => Promise<void>
}

export const useAuthStore = create<AuthState>((set) => ({
    user: null,
    accessToken: null,
    refreshToken: null,
    isAuthenticated: false,
    isLoading: true,

    setAuth: async (user, accessToken, refreshToken) => {
        await SecureStore.setItemAsync('accessToken', accessToken)
        await SecureStore.setItemAsync('refreshToken', refreshToken)
        await SecureStore.setItemAsync('user', JSON.stringify(user))
        set({
            user,
            accessToken,
            refreshToken,
            isAuthenticated: true,
        })
    },

    logout: async () => {
        await SecureStore.deleteItemAsync('accessToken')
        await SecureStore.deleteItemAsync('refreshToken')
        await SecureStore.deleteItemAsync('user')
        set({
            user: null,
            accessToken: null,
            refreshToken: null,
            isAuthenticated: false,
        })
    },

    updateUser: (userData) => {
        set((state) => ({
            user: state.user ? { ...state.user, ...userData } : null,
        }))
    },

    loadAuth: async () => {
        try {
            const accessToken = await SecureStore.getItemAsync('accessToken')
            const refreshToken = await SecureStore.getItemAsync('refreshToken')
            const userStr = await SecureStore.getItemAsync('user')

            if (accessToken && userStr) {
                const user = JSON.parse(userStr)
                set({
                    user,
                    accessToken,
                    refreshToken,
                    isAuthenticated: true,
                    isLoading: false,
                })
            } else {
                set({ isLoading: false })
            }
        } catch (error) {
            set({ isLoading: false })
        }
    },
}))
