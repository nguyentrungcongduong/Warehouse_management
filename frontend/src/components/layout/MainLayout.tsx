import * as React from 'react'
import {
    LayoutDashboard,
    Box,
    ArrowDownToLine,
    ArrowUpFromLine,
    RefreshCcw,
    ClipboardCheck,
    Package,
    LogOut,
} from 'lucide-react'
import { Link, useLocation } from '@tanstack/react-router'
import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarHeader,
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
    SidebarProvider,
    SidebarTrigger,
} from '@/components/ui/sidebar'
import { Toaster } from '@/components/ui/sonner'

const navItems = [
    { title: 'Dashboard', icon: LayoutDashboard, to: '/' },
    { title: 'Tồn kho', icon: Box, to: '/inventory' },
    { title: 'Nhập kho', icon: ArrowDownToLine, to: '/receipt' },
    { title: 'Xuất kho (FEFO)', icon: ArrowUpFromLine, to: '/issue' },
    { title: 'Cross Docking', icon: RefreshCcw, to: '/cross-dock' },
    { title: 'Kiểm kê', icon: ClipboardCheck, to: '/inventory-count' },
    { title: 'Vật tư đóng gói', icon: Package, to: '/packaging' },
]

export function MainLayout({ children }: { children: React.ReactNode }) {
    const location = useLocation()

    // Don't show sidebar on login page
    if (location.pathname === '/login') {
        return <main className="min-h-screen bg-slate-50 w-full">{children}</main>
    }

    return (
        <SidebarProvider>
            <div className="flex min-h-screen w-full bg-slate-50">
                <Sidebar className="border-r border-slate-200">
                    <SidebarHeader className="h-16 flex items-center px-6 border-b border-slate-200 bg-white">
                        <div className="flex items-center gap-2">
                            <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
                                <Box className="w-5 h-5 text-white" />
                            </div>
                            <span className="font-bold text-lg tracking-tight text-slate-800">WMS CORE</span>
                        </div>
                    </SidebarHeader>
                    <SidebarContent className="p-4 bg-white">
                        <SidebarMenu>
                            {navItems.map((item) => (
                                <SidebarMenuItem key={item.to}>
                                    <SidebarMenuButton
                                        asChild
                                        isActive={location.pathname === item.to}
                                        tooltip={item.title}
                                        className="h-11 px-3"
                                    >
                                        <Link
                                            to={item.to}
                                            className={`flex items-center gap-3 w-full transition-colors ${location.pathname === item.to
                                                    ? 'text-blue-600 bg-blue-50 font-semibold'
                                                    : 'text-slate-600 hover:text-blue-600 hover:bg-slate-50'
                                                }`}
                                        >
                                            <item.icon className={`w-5 h-5 ${location.pathname === item.to ? 'text-blue-600' : 'text-slate-500'}`} />
                                            <span>{item.title}</span>
                                        </Link>
                                    </SidebarMenuButton>
                                </SidebarMenuItem>
                            ))}
                        </SidebarMenu>
                    </SidebarContent>
                    <SidebarFooter className="p-4 border-t border-slate-200 bg-white">
                        <SidebarMenu>
                            <SidebarMenuItem>
                                <SidebarMenuButton className="h-11 px-3 text-slate-600 hover:text-red-600 hover:bg-red-50">
                                    <LogOut className="w-5 h-5" />
                                    <span>Đăng xuất</span>
                                </SidebarMenuButton>
                            </SidebarMenuItem>
                        </SidebarMenu>
                    </SidebarFooter>
                </Sidebar>
                <main className="flex-1 flex flex-col min-w-0">
                    <header className="h-16 border-b border-slate-200 bg-white flex items-center px-6 justify-between sticky top-0 z-10">
                        <div className="flex items-center gap-4">
                            <SidebarTrigger />
                            <h2 className="font-semibold text-slate-700">
                                {navItems.find((n) => n.to === location.pathname)?.title || 'Hệ thống Quản lý Kho'}
                            </h2>
                        </div>
                        <div className="flex items-center gap-4">
                            <div className="text-right">
                                <p className="text-sm font-medium text-slate-900">Admin User</p>
                                <p className="text-xs text-slate-500 uppercase tracking-wider">Kho HCM-01</p>
                            </div>
                            <div className="w-10 h-10 rounded-full bg-slate-200 flex items-center justify-center border border-slate-300">
                                <span className="text-sm font-bold text-slate-600">AD</span>
                            </div>
                        </div>
                    </header>
                    <div className="flex-1 p-6 overflow-auto">
                        {children}
                    </div>
                </main>
            </div>
            <Toaster position="top-right" richColors />
        </SidebarProvider>
    )
}
