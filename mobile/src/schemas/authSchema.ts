import { z } from 'zod'

// Login schema
export const loginSchema = z.object({
    username: z.string().min(1, 'Vui lòng nhập tên đăng nhập'),
    password: z.string().min(6, 'Mật khẩu phải có ít nhất 6 ký tự'),
})

export type LoginFormData = z.infer<typeof loginSchema>
