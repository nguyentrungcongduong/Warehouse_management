import * as React from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { toast } from 'sonner'
import { ClipboardCheck, Play, Save, CheckCircle2, AlertTriangle, Scale } from 'lucide-react'

interface CountItem {
    id: string
    product: string
    batch: string
    systemQty: number
    physicalQty: number | null
}

export function InventoryCountPage() {
    const [step, setStep] = React.useState<'IDLE' | 'COUNTING' | 'FINISHED'>('IDLE')
    const [items, setItems] = React.useState<CountItem[]>([
        { id: '1', product: 'Paracetamol 500mg', batch: 'BAT-2024-001', systemQty: 5000, physicalQty: null },
        { id: '2', product: 'Amoxicillin 250mg', batch: 'BAT-2024-052', systemQty: 1200, physicalQty: null },
        { id: '3', product: 'Vitamin C 1000mg', batch: 'BAT-2023-088', systemQty: 450, physicalQty: null },
    ])

    const handleStartCount = () => {
        setStep('COUNTING')
        toast.info('Bắt đầu quá trình kiểm kê. Chúc bạn hoàn thành tốt!')
    }

    const handleQtyChange = (id: string, val: string) => {
        const num = Number(val)
        setItems(items.map(item => item.id === id ? { ...item, physicalQty: num } : item))
    }

    const handleFinishCount = () => {
        // Kiểm tra đã nhập hết chưa
        if (items.some(i => i.physicalQty === null)) {
            toast.error('Vui lòng nhập đầy đủ số lượng cho tất cả sản phẩm')
            return
        }
        setStep('FINISHED')
        toast.success('Đã hoàn thành kiểm kê!')
    }

    return (
        <div className="max-w-5xl mx-auto space-y-6">
            <div className="flex justify-between items-center">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">Kiểm kê Định kỳ</h1>
                    <p className="text-slate-500">Đối soát số lượng tồn thực tế và số lượng trên hệ thống</p>
                </div>

                {step === 'IDLE' && (
                    <Button className="bg-blue-600 hover:bg-blue-700 gap-2 h-11 px-6" onClick={handleStartCount}>
                        <Play className="w-4 h-4 fill-current" /> BẮT ĐẦU KIỂM KÊ
                    </Button>
                )}

                {step === 'COUNTING' && (
                    <Button className="bg-emerald-600 hover:bg-emerald-700 gap-2 h-11 px-6" onClick={handleFinishCount}>
                        <Save className="w-4 h-4" /> HOÀN TẤT & TÍNH LỆCH
                    </Button>
                )}

                {step === 'FINISHED' && (
                    <Button variant="outline" className="gap-2 h-11" onClick={() => { setStep('IDLE'); setItems(items.map(i => ({ ...i, physicalQty: null }))) }}>
                        <RefreshCcw className="w-4 h-4" /> LÀM PHIẾU MỚI
                    </Button>
                )}
            </div>

            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                {[
                    { label: 'Trạng thái', value: step === 'IDLE' ? 'Sẵn sàng' : step === 'COUNTING' ? 'Đang thực hiện' : 'Đã đối soát', icon: ClipboardCheck },
                    { label: 'Tổng SKU', value: items.length, icon: ClipboardCheck },
                    { label: 'Tổng SL Hệ thống', value: items.reduce((a, b) => a + b.systemQty, 0).toLocaleString(), icon: Scale },
                    { label: 'Sai lệch SL', value: step === 'FINISHED' ? items.reduce((a, b) => a + (b.physicalQty || 0) - b.systemQty, 0) : '---', icon: AlertTriangle },
                ].map((stat, i) => (
                    <Card key={i} className="border-slate-200">
                        <CardContent className="p-4 flex items-center gap-4">
                            <div className="w-10 h-10 bg-slate-100 rounded-full flex items-center justify-center">
                                <stat.icon className="w-5 h-5 text-slate-500" />
                            </div>
                            <div>
                                <p className="text-xs font-medium text-slate-500 uppercase tracking-wider">{stat.label}</p>
                                <p className="text-lg font-bold text-slate-900">{stat.value}</p>
                            </div>
                        </CardContent>
                    </Card>
                ))}
            </div>

            <Card className="border-slate-200 shadow-sm">
                <CardHeader className="bg-slate-50/50 border-b border-slate-100">
                    <CardTitle className="text-base font-bold text-slate-700 uppercase tracking-tight">Chi tiết danh sách kiểm kê</CardTitle>
                </CardHeader>
                <CardContent className="p-0">
                    <Table>
                        <TableHeader>
                            <TableRow className="bg-slate-50">
                                <TableHead className="w-12 font-bold text-slate-600">STT</TableHead>
                                <TableHead className="font-bold text-slate-600">Sản phẩm / Batch</TableHead>
                                <TableHead className="font-bold text-slate-600 text-right">SL Hệ thống</TableHead>
                                <TableHead className="font-bold text-slate-600 text-center w-40">SL Thực tế</TableHead>
                                <TableHead className="font-bold text-slate-600 text-right">Chênh lệch</TableHead>
                                <TableHead className="font-bold text-slate-600 text-center">Kết luận</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {items.map((item, index) => {
                                const diff = step === 'FINISHED' ? (item.physicalQty || 0) - item.systemQty : 0
                                return (
                                    <TableRow key={item.id} className="hover:bg-slate-50/50 transition-colors">
                                        <TableCell className="font-medium text-slate-400">{index + 1}</TableCell>
                                        <TableCell>
                                            <div className="flex flex-col">
                                                <span className="font-bold text-slate-800">{item.product}</span>
                                                <span className="text-xs text-slate-500 font-mono">{item.batch}</span>
                                            </div>
                                        </TableCell>
                                        <TableCell className="text-right font-mono font-medium">{item.systemQty.toLocaleString()}</TableCell>
                                        <TableCell className="text-center">
                                            <Input
                                                type="number"
                                                className={`w-24 mx-auto text-center font-bold ${step !== 'COUNTING' ? 'bg-slate-50' : 'bg-white border-blue-200 focus:ring-blue-400'}`}
                                                disabled={step !== 'COUNTING'}
                                                value={item.physicalQty === null ? '' : item.physicalQty}
                                                onChange={(e) => handleQtyChange(item.id, e.target.value)}
                                                placeholder="0"
                                            />
                                        </TableCell>
                                        <TableCell className="text-right font-mono">
                                            {step === 'FINISHED' ? (
                                                <span className={diff > 0 ? 'text-emerald-600 font-bold' : diff < 0 ? 'text-red-600 font-bold' : 'text-slate-400'}>
                                                    {diff > 0 ? `+${diff}` : diff}
                                                </span>
                                            ) : '---'}
                                        </TableCell>
                                        <TableCell className="text-center">
                                            {step === 'FINISHED' ? (
                                                diff === 0 ? (
                                                    <Badge className="bg-emerald-100 text-emerald-700 border-none"><CheckCircle2 className="w-3 h-3 mr-1" /> Khớp</Badge>
                                                ) : (
                                                    <Badge className="bg-red-100 text-red-700 border-none"><AlertTriangle className="w-3 h-3 mr-1" /> Lệch</Badge>
                                                )
                                            ) : (
                                                <span className="text-slate-300">Chờ nhập SL</span>
                                            )}
                                        </TableCell>
                                    </TableRow>
                                )
                            })}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>
        </div>
    )
}

import { RefreshCcw } from 'lucide-react'
