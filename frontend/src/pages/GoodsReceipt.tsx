import * as React from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { toast } from 'sonner'
import { Package, CalendarIcon, ArrowDownToLine, MapPin, Hash } from 'lucide-react'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { Calendar } from '@/components/ui/calendar'
import { format } from 'date-fns'
import { vi } from 'date-fns/locale'
import { cn } from '@/lib/utils'

const receiptSchema = z.object({
    productName: z.string().min(1, 'Vui lòng chọn sản phẩm'),
    batchNumber: z.string().min(1, 'Vui lòng nhập số Batch/Lot'),
    expiryDate: z.date({
        invalid_type_error: "Vui lòng chọn hạn sử dụng",
    }),
    quantity: z.number().min(1, 'Số lượng phải lớn hơn 0'),
    location: z.string().min(1, 'Vui lòng nhập vị trí lưu kho'),
})

type ReceiptFormValues = z.infer<typeof receiptSchema>

export function GoodsReceiptPage() {
    const form = useForm<ReceiptFormValues>({
        resolver: zodResolver(receiptSchema),
        defaultValues: {
            productName: '',
            batchNumber: '',
            quantity: 0,
        },
    })

    function onSubmit(data: ReceiptFormValues) {
        console.log('Submit Receipt:', data)
        toast.success('Nhập kho thành công!', {
            description: `Đã nhập ${data.quantity} đơn vị ${data.productName} vào vị trí ${data.location}`,
        })
        form.reset()
    }

    return (
        <div className="max-w-2xl mx-auto space-y-6">
            <div>
                <h1 className="text-2xl font-bold text-slate-900">Phiếu Nhập Kho</h1>
                <p className="text-slate-500">Khai báo thông tin hàng hóa mới nhập vào kho</p>
            </div>

            <Card className="border-slate-200 shadow-sm">
                <CardHeader className="bg-slate-50/50 border-b border-slate-100">
                    <CardTitle className="text-lg flex items-center gap-2 text-slate-700">
                        <ArrowDownToLine className="w-5 h-5 text-blue-600" />
                        Thông tin nhập hàng
                    </CardTitle>
                    <CardDescription>Điền đầy đủ các thông tin bắt buộc dưới đây</CardDescription>
                </CardHeader>
                <CardContent className="p-6">
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
                            <FormField
                                control={form.control}
                                name="productName"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel className="flex items-center gap-2">
                                            <Package className="w-4 h-4 text-slate-400" /> Sản phẩm
                                        </FormLabel>
                                        <FormControl>
                                            <Input placeholder="Tên sản phẩm hoặc SKU" {...field} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <FormField
                                    control={form.control}
                                    name="batchNumber"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel className="flex items-center gap-2">
                                                <Hash className="w-4 h-4 text-slate-400" /> Số Batch / Lot
                                            </FormLabel>
                                            <FormControl>
                                                <Input placeholder="Batch-2024-..." {...field} />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="expiryDate"
                                    render={({ field }) => (
                                        <FormItem className="flex flex-col">
                                            <FormLabel className="flex items-center gap-2 mb-2">
                                                <CalendarIcon className="w-4 h-4 text-slate-400" /> Hạn sử dụng
                                            </FormLabel>
                                            <Popover>
                                                <PopoverTrigger asChild>
                                                    <FormControl>
                                                        <Button
                                                            variant={"outline"}
                                                            className={cn(
                                                                "pl-3 text-left font-normal",
                                                                !field.value && "text-muted-foreground"
                                                            )}
                                                        >
                                                            {field.value ? (
                                                                format(field.value, "dd/MM/yyyy")
                                                            ) : (
                                                                <span>Chọn ngày tháng</span>
                                                            )}
                                                            <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                                                        </Button>
                                                    </FormControl>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-auto p-0" align="start">
                                                    <Calendar
                                                        mode="single"
                                                        selected={field.value}
                                                        onSelect={field.onChange}
                                                        disabled={(date) =>
                                                            date < new Date("1900-01-01")
                                                        }
                                                        initialFocus
                                                    />
                                                </PopoverContent>
                                            </Popover>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                            </div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <FormField
                                    control={form.control}
                                    name="quantity"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel className="flex items-center gap-2">
                                                <Hash className="w-4 h-4 text-slate-400" /> Số lượng nhập
                                            </FormLabel>
                                            <FormControl>
                                                <Input type="number" {...field} />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="location"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel className="flex items-center gap-2">
                                                <MapPin className="w-4 h-4 text-slate-400" /> Vị trí lưu kho
                                            </FormLabel>
                                            <FormControl>
                                                <Input placeholder="VD: A-01-02" {...field} />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                            </div>

                            <Button type="submit" className="w-full bg-blue-600 hover:bg-blue-700 h-11 text-lg">
                                Xác nhận Nhập kho
                            </Button>
                        </form>
                    </Form>
                </CardContent>
            </Card>
        </div>
    )
}
