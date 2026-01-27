import { View, Text, TextInput, TouchableOpacity } from 'react-native';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { loginSchema, LoginFormData } from '@/schemas/authSchema';
import { useAuthStore } from '@/stores/authStore';
import { useRouter } from 'expo-router';

export default function Login() {
    const router = useRouter();
    const setAuth = useAuthStore((state: any) => state.setAuth);

    const { control, handleSubmit, formState: { errors } } = useForm<LoginFormData>({
        resolver: zodResolver(loginSchema)
    });

    const onSubmit = async (data: LoginFormData) => {
        // Mock login
        console.log('Login data:', data);
        await setAuth(
            { id: 1, username: data.username, email: 'test@example.com', fullName: 'Người dùng Thử nghiệm', role: 'ADMIN' },
            'mock-access-token',
            'mock-refresh-token'
        );
        router.replace('/');
    };

    return (
        <View className="flex-1 bg-white p-6 justify-center">
            <Text className="text-3xl font-bold mb-8 text-center text-gray-800">Đăng nhập</Text>

            <View className="mb-4">
                <Text className="mb-2 text-gray-600">Tên đăng nhập</Text>
                <Controller
                    control={control}
                    name="username"
                    render={({ field: { onChange, onBlur, value } }) => (
                        <TextInput
                            className={`border p-3 rounded-lg ${errors.username ? 'border-red-500' : 'border-gray-300'}`}
                            onBlur={onBlur}
                            onChangeText={onChange}
                            value={value}
                            placeholder="Nhập tên đăng nhập"
                            autoCapitalize="none"
                        />
                    )}
                />
                {errors.username && <Text className="text-red-500 mt-1">{errors.username.message}</Text>}
            </View>

            <View className="mb-6">
                <Text className="mb-2 text-gray-600">Mật khẩu</Text>
                <Controller
                    control={control}
                    name="password"
                    render={({ field: { onChange, onBlur, value } }) => (
                        <TextInput
                            className={`border p-3 rounded-lg ${errors.password ? 'border-red-500' : 'border-gray-300'}`}
                            onBlur={onBlur}
                            onChangeText={onChange}
                            value={value}
                            placeholder="Nhập mật khẩu"
                            secureTextEntry
                        />
                    )}
                />
                {errors.password && <Text className="text-red-500 mt-1">{errors.password.message}</Text>}
            </View>

            <TouchableOpacity
                className="bg-blue-600 p-4 rounded-lg items-center"
                onPress={handleSubmit(onSubmit)}
            >
                <Text className="text-white font-bold text-lg">Đăng nhập</Text>
            </TouchableOpacity>
        </View>
    );
}
