import * as React from 'react'
import {
    useReactTable,
    getCoreRowModel,
    flexRender,
    createColumnHelper,
    getFilteredRowModel,
    getPaginationRowModel,
} from '@tanstack/react-table'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Search, AlertTriangle, CheckCircle2, XCircle } from 'lucide-react'

// Mock types
interface InventoryItem {
    id: string
    product: string
    sku: string
    batch: string
    expiryDate: string
    qty: number
    location: string
    status: 'OK' | 'NEAR_EXPIRY' | 'EXPIRED'
}

const mockData: InventoryItem[] = [
    { id: '1', product: 'Paracetamol 500mg', sku: 'PHA-001', batch: 'BAT-2024-001', expiryDate: '2025-12-31', qty: 5000, location: 'A-01-02', status: 'OK' },
    { id: '2', product: 'Amoxicillin 250mg', sku: 'PHA-002', batch: 'BAT-2024-052', expiryDate: '2024-03-15', qty: 1200, location: 'B-04-11', status: 'NEAR_EXPIRY' },
    { id: '3', product: 'Vitamin C 1000mg', sku: 'PHA-003', batch: 'BAT-2023-088', expiryDate: '2023-10-01', qty: 450, location: 'C-02-05', status: 'EXPIRED' },
    { id: '4', product: 'Ibuprofen 400mg', sku: 'PHA-004', batch: 'BAT-2024-015', expiryDate: '2026-06-20', qty: 3000, location: 'A-10-01', status: 'OK' },
    { id: '5', product: 'Cough Syrup 100ml', sku: 'PHA-005', batch: 'BAT-2024-099', expiryDate: '2024-04-01', qty: 85, location: 'D-01-01', status: 'NEAR_EXPIRY' },
]

const columnHelper = createColumnHelper<InventoryItem>()

const columns = [
    columnHelper.accessor('product', {
        header: 'Sản phẩm',
        cell: (info) => (
            <div className="flex flex-col">
                <span className="font-bold text-slate-800">{info.getValue()}</span>
                <span className="text-xs text-slate-500 font-mono uppercase">{info.row.original.sku}</span>
            </div>
        ),
    }),
    columnHelper.accessor('batch', {
        header: 'Số Batch/Lot',
        cell: (info) => <span className="font-mono text-slate-600 font-medium">{info.getValue()}</span>,
    }),
    columnHelper.accessor('expiryDate', {
        header: 'Hạn dùng',
        cell: (info) => (
            <span className={info.row.original.status === 'EXPIRED' ? 'text-red-600 font-bold' : info.row.original.status === 'NEAR_EXPIRY' ? 'text-orange-600 font-bold' : 'text-slate-600 italic'}>
                {info.getValue()}
            </span>
        ),
    }),
    columnHelper.accessor('qty', {
        header: 'Số lượng',
        cell: (info) => <span className="font-bold text-slate-900">{info.getValue().toLocaleString()}</span>,
    }),
    columnHelper.accessor('location', {
        header: 'Vị trí',
        cell: (info) => <Badge variant="outline" className="bg-slate-50 text-slate-600 border-slate-300 font-mono">{info.getValue()}</Badge>,
    }),
    columnHelper.accessor('status', {
        header: 'Trạng thái',
        cell: (info) => {
            const status = info.getValue()
            if (status === 'OK') {
                return (
                    <Badge className="bg-emerald-100 text-emerald-700 hover:bg-emerald-200 border-none flex w-fit gap-1 items-center">
                        <CheckCircle2 className="w-3 h-3" /> OK
                    </Badge>
                )
            }
            if (status === 'NEAR_EXPIRY') {
                return (
                    <Badge className="bg-orange-100 text-orange-700 hover:bg-orange-200 border-none flex w-fit gap-1 items-center">
                        <AlertTriangle className="w-3 h-3" /> Sắp hết hạn
                    </Badge>
                )
            }
            return (
                <Badge className="bg-red-100 text-red-700 hover:bg-red-200 border-none flex w-fit gap-1 items-center">
                    <XCircle className="w-3 h-3" /> Hết hạn
                </Badge>
            )
        },
    }),
]

export function InventoryPage() {
    const [globalFilter, setGlobalFilter] = React.useState('')

    const table = useReactTable({
        data: mockData,
        columns,
        state: {
            globalFilter,
        },
        onGlobalFilterChange: setGlobalFilter,
        getCoreRowModel: getCoreRowModel(),
        getFilteredRowModel: getFilteredRowModel(),
        getPaginationRowModel: getPaginationRowModel(),
    })

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900">Danh sách Tồn kho</h1>
                    <p className="text-slate-500">Quản lý và theo dõi tồn kho theo thời gian thực</p>
                </div>
                <div className="relative w-72">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
                    <Input
                        placeholder="Tìm sản phẩm, batch, vị trí..."
                        className="pl-10"
                        value={globalFilter ?? ''}
                        onChange={(e) => setGlobalFilter(e.target.value)}
                    />
                </div>
            </div>

            <Card className="border-slate-200 shadow-sm overflow-hidden">
                <CardContent className="p-0">
                    <Table>
                        <TableHeader className="bg-slate-50">
                            {table.getHeaderGroups().map((headerGroup) => (
                                <TableRow key={headerGroup.id}>
                                    {headerGroup.headers.map((header) => (
                                        <TableHead key={header.id} className="text-slate-600 font-bold uppercase text-[11px] tracking-wider">
                                            {header.isPlaceholder ? null : flexRender(header.column.columnDef.header, header.getContext())}
                                        </TableHead>
                                    ))}
                                </TableRow>
                            ))}
                        </TableHeader>
                        <TableBody>
                            {table.getRowModel().rows.length ? (
                                table.getRowModel().rows.map((row) => (
                                    <TableRow key={row.id} className="hover:bg-blue-50/50 transition-colors border-slate-100">
                                        {row.getVisibleCells().map((cell) => (
                                            <TableCell key={cell.id}>
                                                {flexRender(cell.column.columnDef.cell, cell.getContext())}
                                            </TableCell>
                                        ))}
                                    </TableRow>
                                ))
                            ) : (
                                <TableRow>
                                    <TableCell colSpan={columns.length} className="h-24 text-center text-slate-500">
                                        Không tìm thấy dữ liệu tồn kho.
                                    </TableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>

            <div className="flex items-center justify-between">
                <p className="text-sm text-slate-500 font-medium">
                    Hiển thị {table.getRowModel().rows.length} trong tổng số {mockData.length} bản ghi
                </p>
                <div className="flex gap-2">
                    <button
                        className="px-4 py-2 border border-slate-300 rounded-lg text-sm font-medium hover:bg-slate-50 disabled:opacity-50"
                        onClick={() => table.previousPage()}
                        disabled={!table.getCanPreviousPage()}
                    >
                        Trước
                    </button>
                    <button
                        className="px-4 py-2 border border-slate-300 rounded-lg text-sm font-medium hover:bg-slate-50 disabled:opacity-50"
                        onClick={() => table.nextPage()}
                        disabled={!table.getCanNextPage()}
                    >
                        Sau
                    </button>
                </div>
            </div>
        </div>
    )
}
