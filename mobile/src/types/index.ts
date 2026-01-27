// Product types
export interface Product {
    id: number
    sku: string
    name: string
    description?: string
    category: Category
    unit: string
    minStock: number
    maxStock: number
    currentStock: number
    price: number
    costPrice: number
    imageUrl?: string
    status: 'ACTIVE' | 'INACTIVE'
    createdAt: string
    updatedAt: string
}

// Category types
export interface Category {
    id: number
    name: string
    description?: string
    parentId?: number
    children?: Category[]
    createdAt: string
    updatedAt: string
}

// Warehouse types
export interface Warehouse {
    id: number
    code: string
    name: string
    address: string
    phone?: string
    manager?: User
    status: 'ACTIVE' | 'INACTIVE'
    createdAt: string
    updatedAt: string
}

// User types
export interface User {
    id: number
    username: string
    email: string
    fullName: string
    phone?: string
    role: 'ADMIN' | 'WAREHOUSE_MANAGER' | 'STAFF'
    avatar?: string
    status: 'ACTIVE' | 'INACTIVE'
    createdAt: string
    updatedAt: string
}

// Stock types
export interface Stock {
    id: number
    product: Product
    warehouse: Warehouse
    quantity: number
    location?: string
    lastUpdated: string
}

// Inventory transaction types
export interface InventoryTransaction {
    id: number
    transactionCode: string
    type: 'IMPORT' | 'EXPORT' | 'TRANSFER' | 'ADJUSTMENT'
    status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'COMPLETED'
    warehouse: Warehouse
    targetWarehouse?: Warehouse
    createdBy: User
    approvedBy?: User
    items: InventoryTransactionItem[]
    notes?: string
    createdAt: string
    updatedAt: string
}

export interface InventoryTransactionItem {
    id: number
    product: Product
    quantity: number
    unitPrice?: number
    notes?: string
}

// Pagination types
export interface PaginatedResponse<T> {
    content: T[]
    page: number
    size: number
    totalElements: number
    totalPages: number
    last: boolean
    first: boolean
}

// API Response types
export interface ApiResponse<T> {
    success: boolean
    message?: string
    data: T
}

export interface ApiError {
    success: boolean
    message: string
    errors?: Record<string, string[]>
}
