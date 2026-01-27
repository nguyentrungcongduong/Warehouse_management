import * as React from 'react'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { toast } from 'sonner'
import { ArrowUpFromLine, Search, Sparkles, CheckCircle2, AlertCircle } from 'lucide-react'
import { motion, AnimatePresence } from 'framer-motion'

interface BatchSuggestion {
    batch: string
    expiryDate: string
    availableQty: number
    suggestedQty: number
}

export function GoodsIssuePage() {
    const [productSearch, setProductSearch] = React.useState('')
    const [targetQty, setTargetQty] = React.useState<number>(0)
    const [suggestions, setSuggestions] = React.useState<BatchSuggestion[]>([])
    const [isCalculated, setIsCalculated] = React.useState(false)

    // Giả lập logic FEFO
    const handleCalculateFEFO = () => {
        if (!productSearch || targetQty <= 0) {
            toast.error('Vui lòng chọn sản phẩm và nhập số lượng cần xuất')
            return
        }

        // Mock data của các Batch cho sản phẩm đang chọn
        const availableBatches = [
            { batch: 'BAT-001', expiryDate: '2024-05-20', availableQty: 100 },
            { batch: 'BAT-002', expiryDate: '2024-08-15', availableQty: 250 },
            { batch: 'BAT-003', expiryDate: '2024-12-01', availableQty: 500 },
        ]

        let remaining = targetQty
        const calculated: BatchSuggestion[] = []

        for (const b of availableBatches) {
            if (remaining <= 0) break
            const take = Math.min(remaining, b.availableQty)
            calculated.push({ ...b, suggestedQty: take })
            remaining -= take
        }

        if (remaining > 0) {
            toast.warning(`Không đủ hàng tồn! Thiếu ${remaining} đơn vị`)
        }

        setSuggestions(calculated)
        setIsCalculated(true)
        toast.info('Hệ thống đã đề xuất các Batch tối ưu theo FEFO')
    }

    const handleConfirmIssue = () => {
        toast.success('Xuất kho thành công!', {
            description: `Đã chuẩn bị phiếu xuất cho ${targetQty} đơn vị sản phẩm.`,
        })
        // Reset form
        setProductSearch('')
        setTargetQty(0)
        setSuggestions([])
        setIsCalculated(false)
    }

    return (
        <div className="max-w-4xl mx-auto space-y-6">
            <div className="flex justify-between items-start">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">Xuất Kho (FEFO)</h1>
                    <p className="text-slate-500">Hệ thống tự động đề xuất lô hàng theo nguyên tắc First Expired - First Out</p>
                </div>
                <Badge variant="outline" className="bg-blue-50 text-blue-700 border-blue-200 px-3 py-1">
                    Quy tắc: FEFO Priority
                </Badge>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <Card className="md:col-span-1 border-slate-200">
                    <CardHeader>
                        <CardTitle className="text-sm uppercase tracking-wider text-slate-500">Bước 1: Yêu cầu</CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <div className="space-y-2">
                            <label className="text-sm font-medium text-slate-700">Sản phẩm</label>
                            <div className="relative">
                                <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                                <Input
                                    placeholder="Tìm sản phẩm..."
                                    className="pl-10"
                                    value={productSearch}
                                    onChange={(e) => setProductSearch(e.target.value)}
                                />
                            </div>
                        </div>
                        <div className="space-y-2">
                            <label className="text-sm font-medium text-slate-700">Tổng lượng xuất</label>
                            <Input
                                type="number"
                                placeholder="Nhập số lượng"
                                value={targetQty || ''}
                                onChange={(e) => setTargetQty(Number(e.target.value))}
                            />
                        </div>
                        <Button
                            className="w-full bg-blue-600 hover:bg-blue-700 gap-2 mt-4"
                            onClick={handleCalculateFEFO}
                        >
                            <Sparkles className="w-4 h-4" /> Đề xuất Batch
                        </Button>
                    </CardContent>
                </Card>

                <Card className="md:col-span-2 border-slate-200 shadow-sm relative overflow-hidden">
                    <CardHeader className="flex flex-row items-center justify-between border-b border-slate-50 bg-slate-50/30">
                        <div>
                            <CardTitle className="text-sm uppercase tracking-wider text-slate-500">Bước 2: Hệ thống đề xuất Batch</CardTitle>
                        </div>
                        {isCalculated && (
                            <Badge className="bg-emerald-100 text-emerald-700 border-none">
                                Đã tính toán
                            </Badge>
                        )}
                    </CardHeader>
                    <CardContent className="p-0">
                        <div className="min-h-[300px] flex flex-col items-center justify-center p-6">
                            {!isCalculated ? (
                                <div className="text-center space-y-3">
                                    <div className="w-16 h-16 bg-slate-100 rounded-full flex items-center justify-center mx-auto">
                                        <ArrowUpFromLine className="w-8 h-8 text-slate-300" />
                                    </div>
                                    <p className="text-slate-400 text-sm italic">Vui lòng nhập yêu cầu để hệ thống tính toán FEFO</p>
                                </div>
                            ) : (
                                <div className="w-full space-y-4">
                                    <AnimatePresence>
                                        {suggestions.map((item, index) => (
                                            <motion.div
                                                key={item.batch}
                                                initial={{ opacity: 0, x: 20 }}
                                                animate={{ opacity: 1, x: 0 }}
                                                transition={{ delay: index * 0.1 }}
                                                className="flex items-center justify-between p-4 border border-slate-100 rounded-xl bg-white shadow-sm"
                                            >
                                                <div className="flex items-center gap-4">
                                                    <div className="w-10 h-10 bg-blue-50 rounded-lg flex items-center justify-center font-bold text-blue-600">
                                                        {index + 1}
                                                    </div>
                                                    <div>
                                                        <p className="font-bold text-slate-800">{item.batch}</p>
                                                        <p className="text-xs text-slate-500">Hết hạn: <span className="text-orange-600 font-medium">{item.expiryDate}</span></p>
                                                    </div>
                                                </div>
                                                <div className="text-right">
                                                    <p className="text-sm text-slate-500">Xuất:</p>
                                                    <p className="font-mono text-xl font-black text-blue-600">+{item.suggestedQty}</p>
                                                </div>
                                            </motion.div>
                                        ))}
                                    </AnimatePresence>

                                    <div className="pt-6 border-t border-slate-100 flex items-center justify-between mt-6">
                                        <div className="flex items-center gap-2 text-emerald-600">
                                            <CheckCircle2 className="w-5 h-5" />
                                            <span className="font-medium text-sm">Chấp nhận đề xuất này?</span>
                                        </div>
                                        <Button
                                            className="bg-emerald-600 hover:bg-emerald-700 font-bold px-8 h-12"
                                            onClick={handleConfirmIssue}
                                        >
                                            XÁC NHẬN XUẤT KHO
                                        </Button>
                                    </div>
                                </div>
                            )}
                        </div>
                    </CardContent>

                    {isCalculated && (
                        <div className="bg-blue-600 h-1 absolute bottom-0 left-0 transition-all duration-500" style={{ width: '100%' }} />
                    )}
                </Card>
            </div>

            <div className="flex items-center gap-3 p-4 bg-orange-50 border border-orange-100 rounded-lg text-orange-800 text-sm">
                <AlertCircle className="w-5 h-5 flex-shrink-0" />
                <p>
                    Hệ thống đang áp dụng nguyên tắc <strong>First Expired, First Out (FEFO)</strong>. Thủ kho không được tự ý đổi Batch trừ khi có yêu cầu đặc biệt từ quản lý.
                </p>
            </div>
        </div>
    )
}
