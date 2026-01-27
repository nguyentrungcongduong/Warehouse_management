import { Stack } from 'expo-router';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useEffect } from 'react';
import { useAuthStore } from '@/stores/authStore';
import "../global.css";

const queryClient = new QueryClient();

export default function RootLayout() {
    const loadAuth = useAuthStore((state: any) => state.loadAuth);

    useEffect(() => {
        loadAuth();
    }, [loadAuth]);

    return (
        <QueryClientProvider client={queryClient}>
            <Stack>
                <Stack.Screen name="index" options={{ title: 'Warehouse Management' }} />
                <Stack.Screen name="(auth)/login" options={{ title: 'Đăng nhập', headerShown: false }} />
            </Stack>
        </QueryClientProvider>
    );
}
