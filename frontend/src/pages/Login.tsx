import * as React from 'react'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Box, Lock, User, ArrowRight } from 'lucide-react'
import { useNavigate } from '@tanstack/react-router'
import { toast } from 'sonner'
import { motion } from 'framer-motion'

export function LoginPage() {
    const [username, setUsername] = React.useState('')
    const [password, setPassword] = React.useState('')
    const navigate = useNavigate()

    const handleLogin = (e: React.FormEvent) => {
        e.preventDefault()
        if (username === 'admin' && password === 'admin') {
            toast.success('Đăng nhập thành công')
            navigate({ to: '/' })
        } else {
            toast.error('Sai tài khoản hoặc mật khẩu (Gợi ý: admin/admin)')
        }
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-slate-50 p-6 relative overflow-hidden">
            {/* Background Decor */}
            <div className="absolute top-0 left-0 w-full h-full opacity-5 pointer-events-none">
                <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-blue-600 rounded-full blur-[120px]" />
                <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] bg-emerald-600 rounded-full blur-[120px]" />
            </div>

            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5 }}
                className="w-full max-w-md z-10"
            >
                <div className="flex flex-col items-center mb-10 space-y-3">
                    <div className="w-16 h-16 bg-blue-600 rounded-2xl flex items-center justify-center shadow-2xl shadow-blue-500/40">
                        <Box className="w-10 h-10 text-white" />
                    </div>
                    <div className="text-center">
                        <h1 className="text-3xl font-black text-slate-900 tracking-tight italic">WMS CORE</h1>
                        <p className="text-slate-500 font-bold uppercase tracking-[0.3em] text-[10px]">Hệ thống Quản lý Kho Nội bộ</p>
                    </div>
                </div>

                <Card className="border-none shadow-2xl shadow-slate-200/50 bg-white/80 backdrop-blur-xl">
                    <CardHeader className="text-center pb-2">
                        <CardTitle className="text-xl font-bold text-slate-800 uppercase tracking-tighter">Đăng nhập hệ thống</CardTitle>
                        <CardDescription>Vui lòng nhập thông tin định danh nhân viên</CardDescription>
                    </CardHeader>
                    <CardContent className="p-8">
                        <form onSubmit={handleLogin} className="space-y-6">
                            <div className="space-y-2">
                                <label className="text-xs font-bold text-slate-500 uppercase tracking-wider ml-1">Tên đăng nhập</label>
                                <div className="relative">
                                    <User className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                                    <Input
                                        placeholder="Nhập username..."
                                        className="pl-10 h-12 bg-slate-50/50 border-slate-200 focus:bg-white transition-all font-medium"
                                        value={username}
                                        onChange={(e) => setUsername(e.target.value)}
                                    />
                                </div>
                            </div>

                            <div className="space-y-2">
                                <label className="text-xs font-bold text-slate-500 uppercase tracking-wider ml-1">Mật khẩu</label>
                                <div className="relative">
                                    <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                                    <Input
                                        type="password"
                                        placeholder="••••••••"
                                        className="pl-10 h-12 bg-slate-50/50 border-slate-200 focus:bg-white transition-all"
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                    />
                                </div>
                            </div>

                            <Button type="submit" className="w-full h-14 bg-blue-600 hover:bg-blue-700 text-white font-bold text-lg group shadow-xl shadow-blue-500/20">
                                VÀO HỆ THỐNG
                                <ArrowRight className="w-5 h-5 ml-2 group-hover:translate-x-1 transition-transform" />
                            </Button>
                        </form>
                    </CardContent>
                </Card>

                <p className="mt-8 text-center text-slate-400 text-xs font-medium uppercase tracking-widest">
                    Developed for Internal Logistics Operations v1.0
                </p>
            </motion.div>
        </div>
    )
}
