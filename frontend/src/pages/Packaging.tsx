import * as React from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { Progress } from '@/components/ui/progress'
import { Package, Plus, AlertCircle, TrendingDown, DollarSign } from 'lucide-react'
import { toast } from 'sonner'

interface Material {
    id: string
    name: string
    qty: number
    maxQty: number
    costPerUnit: number
    unit: string
    status: 'IN_STOCK' | 'LOW' | 'OUT_OF_STOCK'
}

export function PackagingPage() {
    const [materials, setMaterials] = React.useState<Material[]>([
        { id: '1', name: 'Thùng Carton Size L', qty: 120, maxQty: 500, costPerUnit: 15000, unit: 'Cái', status: 'IN_STOCK' },
        { id: '2', name: 'Băng keo trong 5cm', qty: 25, maxQty: 100, costPerUnit: 12000, unit: 'Cuộn', status: 'LOW' },
        { id: '3', name: 'Màng xốp nổ 50cm', qty: 5, maxQty: 50, costPerUnit: 85000, unit: 'Mét', status: 'LOW' },
        { id: '4', name: 'Túi giao hàng PE', qty: 0, maxQty: 1000, costPerUnit: 500, unit: 'Cái', status: 'OUT_OF_STOCK' },
    ])

    const handleAddStock = (id: string) => {
        toast.success('Đã tạo phiếu yêu cầu bổ sung vật tư!')
    }

    return (
        <div className="max-w-5xl mx-auto space-y-6">
            <div className="flex justify-between items-center">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">Vật tư Đóng gói</h1>
                    <p className="text-slate-500">Quản lý tồn kho và chi phí bao bì, vật tư vận hành</p>
                </div>
                <Button className="bg-blue-600 hover:bg-blue-700 gap-2">
                    <Plus className="w-4 h-4" /> Thêm vật tư mới
                </Button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <Card className="border-slate-200 shadow-sm">
                    <CardHeader className="pb-2">
                        <CardTitle className="text-xs font-medium text-slate-500 uppercase tracking-wider flex items-center justify-between">
                            Tổng giá trị tồn
                            <DollarSign className="w-4 h-4 text-emerald-500" />
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <p className="text-2xl font-bold text-slate-900">
                            {materials.reduce((a, b) => a + (b.qty * b.costPerUnit), 0).toLocaleString()} VNĐ
                        </p>
                        <p className="text-xs text-slate-500 mt-1 flex items-center gap-1">
                            <TrendingDown className="w-3 h-3 text-red-500" /> +5% so với tháng trước
                        </p>
                    </CardContent>
                </Card>

                <Card className="border-slate-200 shadow-sm">
                    <CardHeader className="pb-2">
                        <CardTitle className="text-xs font-medium text-slate-500 uppercase tracking-wider flex items-center justify-between">
                            Vật tư sắp hết
                            <AlertCircle className="w-4 h-4 text-orange-500" />
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <p className="text-2xl font-bold text-slate-900">
                            {materials.filter(m => m.status !== 'IN_STOCK').length} mục
                        </p>
                        <p className="text-xs text-orange-600 mt-1 font-medium">Cần bổ sung ngay lập tức</p>
                    </CardContent>
                </Card>

                <Card className="border-slate-200 shadow-sm">
                    <CardHeader className="pb-2">
                        <CardTitle className="text-xs font-medium text-slate-500 uppercase tracking-wider flex items-center justify-between">
                            Tỷ lệ lấp đầy
                            <Package className="w-4 h-4 text-blue-500" />
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <p className="text-2xl font-bold text-slate-900">
                            {Math.round((materials.reduce((a, b) => a + b.qty, 0) / materials.reduce((a, b) => a + b.maxQty, 0)) * 100)}%
                        </p>
                        <Progress value={24} className="h-1.5 mt-2" />
                    </CardContent>
                </Card>
            </div>

            <Card className="border-slate-200 shadow-sm overflow-hidden">
                <CardHeader className="bg-slate-50 border-b border-slate-100 py-3">
                    <CardTitle className="text-sm font-bold text-slate-700">DANH MỤC VẬT TƯ CHI TIẾT</CardTitle>
                </CardHeader>
                <CardContent className="p-0">
                    <Table>
                        <TableHeader>
                            <TableRow className="hover:bg-transparent">
                                <TableHead className="font-bold">Loại vật tư</TableHead>
                                <TableHead className="font-bold">Đơn giá</TableHead>
                                <TableHead className="font-bold text-center">Tồn kho</TableHead>
                                <TableHead className="font-bold">Mức độ dự phòng</TableHead>
                                <TableHead className="font-bold text-center">Trạng thái</TableHead>
                                <TableHead className="font-bold text-right">Thao tác</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {materials.map((m) => (
                                <TableRow key={m.id} className="hover:bg-slate-50/50">
                                    <TableCell className="font-bold text-slate-800">{m.name}</TableCell>
                                    <TableCell className="text-slate-500 italic">{m.costPerUnit.toLocaleString()} / {m.unit}</TableCell>
                                    <TableCell className="text-center font-black text-slate-900">{m.qty} <span className="text-[10px] text-slate-400 font-normal ml-1">{m.unit}</span></TableCell>
                                    <TableCell className="w-48">
                                        <div className="space-y-1">
                                            <div className="flex justify-between text-[10px]">
                                                <span>{m.qty} / {m.maxQty}</span>
                                                <span>{Math.round((m.qty / m.maxQty) * 100)}%</span>
                                            </div>
                                            <Progress
                                                value={(m.qty / m.maxQty) * 100}
                                                className={`h-1.5 ${m.status === 'LOW' ? 'bg-orange-100' : m.status === 'OUT_OF_STOCK' ? 'bg-red-100' : 'bg-slate-100'}`}
                                            // This depends on shadcn progress implementation, usually it's internal.
                                            />
                                        </div>
                                    </TableCell>
                                    <TableCell className="text-center">
                                        {m.status === 'IN_STOCK' ? (
                                            <Badge className="bg-emerald-100 text-emerald-700 border-none">An toàn</Badge>
                                        ) : m.status === 'LOW' ? (
                                            <Badge className="bg-orange-100 text-orange-700 border-none">Sắp hết</Badge>
                                        ) : (
                                            <Badge className="bg-red-100 text-red-700 border-none">Đã hết</Badge>
                                        )}
                                    </TableCell>
                                    <TableCell className="text-right">
                                        <Button
                                            variant="outline"
                                            size="sm"
                                            className="text-blue-600 hover:text-blue-700 hover:bg-blue-50 border-blue-200"
                                            onClick={() => handleAddStock(m.id)}
                                        >
                                            Bổ sung
                                        </Button>
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
