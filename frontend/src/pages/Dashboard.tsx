import * as React from 'react'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Box, Layers, AlertCircle, Clock, ShieldCheck } from 'lucide-react'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'

export function DashboardPage() {
    return (
        <div className="space-y-8 max-w-7xl mx-auto">
            <div className="flex justify-between items-end">
                <div>
                    <h1 className="text-3xl font-black text-slate-900 tracking-tight">TỔNG QUAN KHO</h1>
                    <p className="text-slate-500 font-medium tracking-wide flex items-center gap-2 mt-1">
                        <ShieldCheck className="w-4 h-4 text-emerald-500" /> Hệ thống đang hoạt động ổn định
                    </p>
                </div>
                <div className="text-right">
                    <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">Cập nhật lần cuối</p>
                    <p className="text-sm font-mono font-bold text-slate-700">{new Date().toLocaleString('vi-VN')}</p>
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <Card className="border-none shadow-xl shadow-blue-500/10 bg-gradient-to-br from-blue-600 to-blue-700 text-white">
                    <CardContent className="p-6 flex items-center justify-between">
                        <div className="space-y-1">
                            <p className="text-blue-100 text-xs font-bold uppercase tracking-wider">Tổng số SKU</p>
                            <p className="text-4xl font-black">1.284</p>
                            <p className="text-[10px] text-blue-200">+12 SKU mới trong hôm nay</p>
                        </div>
                        <div className="w-14 h-14 bg-white/20 rounded-2xl flex items-center justify-center backdrop-blur-md">
                            <Box className="w-8 h-8 text-white" />
                        </div>
                    </CardContent>
                </Card>

                <Card className="border-none shadow-xl shadow-slate-900/5 bg-white border border-slate-100 italic">
                    <CardContent className="p-6 flex items-center justify-between">
                        <div className="space-y-1">
                            <p className="text-slate-500 text-xs font-bold uppercase tracking-wider italic">Tổng tồn hiện tại</p>
                            <p className="text-4xl font-black text-slate-900">452.800</p>
                            <p className="text-[10px] text-emerald-600 font-bold uppercase tracking-tighter italic">Lấp đầy 78% dung tích kho</p>
                        </div>
                        <div className="w-14 h-14 bg-slate-100 rounded-2xl flex items-center justify-center">
                            <Layers className="w-8 h-8 text-slate-400" />
                        </div>
                    </CardContent>
                </Card>

                <Card className="border-none shadow-xl shadow-orange-500/10 bg-gradient-to-br from-orange-500 to-red-600 text-white">
                    <CardContent className="p-6 flex items-center justify-between">
                        <div className="space-y-1">
                            <p className="text-orange-100 text-xs font-bold uppercase tracking-wider">Cảnh báo hết hạn</p>
                            <p className="text-4xl font-black">24</p>
                            <p className="text-[10px] text-orange-200 tracking-tight font-medium underline">CLICK ĐỂ XỬ LÝ NGAY</p>
                        </div>
                        <div className="w-14 h-14 bg-white/20 rounded-2xl flex items-center justify-center backdrop-blur-md">
                            <AlertCircle className="w-8 h-8 text-white" />
                        </div>
                    </CardContent>
                </Card>
            </div>

            <Card className="border-slate-200 border-t-4 border-t-orange-500 shadow-lg">
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-4">
                    <div className="space-y-1">
                        <CardTitle className="text-lg font-black text-slate-800 flex items-center gap-2">
                            <Clock className="w-5 h-5 text-orange-500" />
                            TOP 5 HÀNG SẮP HẾT HẠN (NEAR EXPIRY)
                        </CardTitle>
                        <CardDescription className="text-xs uppercase font-bold tracking-widest text-slate-400 italic">Cần ưu tiên xuất trước theo FEFO</CardDescription>
                    </div>
                    <Badge className="bg-red-100 text-red-700 border-none font-bold uppercase text-[10px] px-3">Ưu tiên cao</Badge>
                </CardHeader>
                <CardContent className="p-0">
                    <Table>
                        <TableHeader className="bg-slate-50 border-b border-slate-100">
                            <TableRow>
                                <TableHead className="font-bold text-slate-600 text-xs">SẢN PHẨM</TableHead>
                                <TableHead className="font-bold text-slate-600 text-xs">BATCH</TableHead>
                                <TableHead className="font-bold text-slate-600 text-xs text-right">SL TỒN</TableHead>
                                <TableHead className="font-bold text-slate-600 text-xs text-center font-black">HẠN DÙNG</TableHead>
                                <TableHead className="font-bold text-slate-600 text-xs text-right">THAO TÁC</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {[
                                { name: 'Amoxicillin 250mg', batch: 'BAT-2024-052', qty: 1200, expiry: '2024-03-15' },
                                { name: 'Cough Syrup 100ml', batch: 'BAT-2024-099', qty: 85, expiry: '2024-04-01' },
                                { name: 'Paracetamol 500mg', batch: 'BAT-2024-112', qty: 2400, expiry: '2024-04-10' },
                                { name: 'Vitamin C 1000mg', batch: 'BAT-2023-088', qty: 450, expiry: '2024-05-01' },
                                { name: 'Ibuprofen 400mg', batch: 'BAT-2024-015', qty: 300, expiry: '2024-05-15' },
                            ].map((item, i) => (
                                <TableRow key={i} className="hover:bg-orange-50/30 border-slate-100 font-medium">
                                    <TableCell className="font-bold text-slate-800">{item.name}</TableCell>
                                    <TableCell className="font-mono text-slate-500 text-xs">{item.batch}</TableCell>
                                    <TableCell className="text-right font-black text-slate-900">{item.qty.toLocaleString()}</TableCell>
                                    <TableCell className="text-center">
                                        <span className="bg-orange-100 text-orange-700 px-3 py-1 rounded-md font-bold font-mono text-xs">
                                            {item.expiry}
                                        </span>
                                    </TableCell>
                                    <TableCell className="text-right">
                                        <button className="text-xs font-bold text-blue-600 hover:underline uppercase tracking-tighter">Tạo phiếu xuất ngay</button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>
        </div>
    )
}
