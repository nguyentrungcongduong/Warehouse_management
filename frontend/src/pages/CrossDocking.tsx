import * as React from 'react'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { toast } from 'sonner'
import { RefreshCcw, Search, Package, ArrowRightLeft, Truck, CheckCircle2 } from 'lucide-react'

export function CrossDockingPage() {
    const [orderId, setOrderId] = React.useState('')
    const [orderData, setOrderData] = React.useState<any>(null)
    const [isLoading, setIsLoading] = React.useState(false)

    const handleSearchOrder = () => {
        if (!orderId) return
        setIsLoading(true)
        // Giả lập tìm kiếm đơn hàng
        setTimeout(() => {
            setOrderData({
                id: orderId,
                customer: 'Bệnh viện Chợ Rẫy',
                items: [
                    { sku: 'PHA-001', name: 'Paracetamol 500mg', qty: 1000 },
                    { sku: 'PHA-004', name: 'Ibuprofen 400mg', qty: 500 },
                ],
                priority: 'HIGH'
            })
            setIsLoading(false)
            toast.info('Đã tìm thấy đơn hàng cần Cross-docking')
        }, 800)
    }

    const handleCrossDockSubmit = () => {
        toast.success('Cross-docking hoàn tất!', {
            description: `Đơn hàng ${orderId} đã được xuất đi ngay sau khi nhận.`,
        })
        setOrderData(null)
        setOrderId('')
    }

    return (
        <div className="max-w-4xl mx-auto space-y-6">
            <div className="flex justify-between items-start">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">Cross Docking</h1>
                    <p className="text-slate-500">Quy trình nhận hàng và giao ngay (không lưu kho)</p>
                </div>
                <div className="flex items-center gap-2 px-4 py-2 bg-emerald-50 border border-emerald-100 rounded-lg">
                    <Truck className="w-5 h-5 text-emerald-600" />
                    <span className="text-sm font-bold text-emerald-700 uppercase tracking-tighter italic">DIRECT TO CUSTOMER</span>
                </div>
            </div>

            <Card className="border-slate-200">
                <CardContent className="p-6">
                    <div className="flex gap-4">
                        <div className="relative flex-1">
                            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                            <Input
                                placeholder="Nhập mã đơn hàng (Order ID)..."
                                className="pl-10 h-12"
                                value={orderId}
                                onChange={(e) => setOrderId(e.target.value)}
                                onKeyDown={(e) => e.key === 'Enter' && handleSearchOrder()}
                            />
                        </div>
                        <Button className="bg-slate-800 h-12 px-8 font-bold" onClick={handleSearchOrder} disabled={isLoading}>
                            {isLoading ? <RefreshCcw className="w-4 h-4 animate-spin" /> : 'TÌM ĐƠN HÀNG'}
                        </Button>
                    </div>
                </CardContent>
            </Card>

            {orderData && (
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 animate-in slide-in-from-bottom-4 duration-500">
                    <Card className="md:col-span-2 border-slate-200 shadow-lg border-t-4 border-t-emerald-500">
                        <CardHeader>
                            <div className="flex justify-between items-center">
                                <CardTitle className="text-lg">Danh sách sản phẩm trong đơn</CardTitle>
                                <Badge className="bg-orange-100 text-orange-700 border-none">Ưu tiên: {orderData.priority}</Badge>
                            </div>
                            <CardDescription>Đơn hàng: <span className="font-bold text-slate-800">{orderData.id}</span> - Khách hàng: {orderData.customer}</CardDescription>
                        </CardHeader>
                        <CardContent className="p-0">
                            <Table>
                                <TableHeader className="bg-slate-50">
                                    <TableRow>
                                        <TableHead className="font-bold">Sản phẩm</TableHead>
                                        <TableHead className="font-bold text-right">SL Cần giao</TableHead>
                                        <TableHead className="font-bold text-center">Trạng thái nhận</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {orderData.items.map((item: any) => (
                                        <TableRow key={item.sku}>
                                            <TableCell>
                                                <div className="flex items-center gap-3">
                                                    <Package className="w-4 h-4 text-slate-400" />
                                                    <div>
                                                        <p className="font-bold text-slate-800">{item.name}</p>
                                                        <p className="text-xs text-slate-500 font-mono">{item.sku}</p>
                                                    </div>
                                                </div>
                                            </TableCell>
                                            <TableCell className="text-right font-mono text-lg font-bold">{item.qty}</TableCell>
                                            <TableCell className="text-center">
                                                <Badge variant="outline" className="border-emerald-200 text-emerald-600 bg-emerald-50">
                                                    SẴN SÀNG
                                                </Badge>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </CardContent>
                    </Card>

                    <Card className="md:col-span-1 border-slate-200 bg-emerald-50/20 border-dash border-2">
                        <CardHeader>
                            <CardTitle className="text-sm uppercase text-slate-500">Hành động</CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-6">
                            <div className="space-y-4">
                                <div className="flex items-center gap-3 text-slate-600">
                                    <div className="w-8 h-8 rounded-full bg-white border border-slate-200 flex items-center justify-center font-bold italic">1</div>
                                    <span className="text-sm">Nhận hàng tại cổng IN</span>
                                </div>
                                <div className="flex items-center gap-3 text-slate-600">
                                    <div className="w-8 h-8 rounded-full bg-white border border-slate-200 flex items-center justify-center font-bold italic">2</div>
                                    <span className="text-sm">Kiểm tra tem nhãn khách hàng</span>
                                </div>
                                <div className="flex items-center gap-3 text-emerald-600 font-bold">
                                    <CheckCircle2 className="w-8 h-8" />
                                    <span className="text-sm">Sẵn sàng xuất Dock OUT</span>
                                </div>
                            </div>

                            <Button
                                className="w-full h-16 bg-emerald-600 hover:bg-emerald-700 text-white font-black text-xl flex flex-col gap-0 shadow-xl shadow-emerald-200"
                                onClick={handleCrossDockSubmit}
                            >
                                <span className="text-[10px] uppercase font-bold tracking-[0.2em] opacity-80">Quy trình Cross-dock</span>
                                RECEIVE & SHIP NOW
                            </Button>

                            <p className="text-[10px] text-center text-slate-400">
                                Lưu ý: Hệ thống sẽ tự động trừ tồn kho và ghi nhận Stock Movement mà không lưu vào Rack.
                            </p>
                        </CardContent>
                    </Card>
                </div>
            )}
        </div>
    )
}
