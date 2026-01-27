import { z } from 'zod'

// Login schema
export const loginSchema = z.object({
    username: z.string().min(1, 'Vui lòng nhập tên đăng nhập'),
    password: z.string().min(6, 'Mật khẩu phải có ít nhất 6 ký tự'),
})

export type LoginFormData = z.infer<typeof loginSchema>

// Register schema
export const registerSchema = z.object({
    username: z.string().min(3, 'Tên đăng nhập phải có ít nhất 3 ký tự'),
    email: z.string().email('Email không hợp lệ'),
    fullName: z.string().min(1, 'Vui lòng nhập họ tên'),
    password: z.string().min(6, 'Mật khẩu phải có ít nhất 6 ký tự'),
    confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
    message: 'Mật khẩu xác nhận không khớp',
    path: ['confirmPassword'],
})

export type RegisterFormData = z.infer<typeof registerSchema>
