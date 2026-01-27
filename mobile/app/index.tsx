import { View, Text, TouchableOpacity } from 'react-native';
import { useRouter } from 'expo-router';
import { useAuthStore } from '@/stores/authStore';

export default function Home() {
    const router = useRouter();
    const { user, isAuthenticated, logout } = useAuthStore();

    return (
        <View className="flex-1 items-center justify-center bg-white p-4">
            <Text className="text-2xl font-bold text-blue-600 mb-4">
                Warehouse Management
            </Text>

            {isAuthenticated ? (
                <View className="items-center">
                    <Text className="text-lg mb-4">Xin chào, {user?.fullName}</Text>
                    <TouchableOpacity
                        className="bg-red-500 px-6 py-3 rounded-lg"
                        onPress={logout}
                    >
                        <Text className="text-white font-semibold">Đăng xuất</Text>
                    </TouchableOpacity>
                </View>
            ) : (
                <TouchableOpacity
                    className="bg-blue-600 px-6 py-3 rounded-lg"
                    onPress={() => router.push('/(auth)/login')}
                >
                    <Text className="text-white font-semibold">Đăng nhập ngay</Text>
                </TouchableOpacity>
            )}
        </View>
    );
}
