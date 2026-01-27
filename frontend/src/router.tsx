import {
    createRouter,
    createRoute,
    createRootRoute,
    Outlet,
} from '@tanstack/react-router'
import * as React from 'react'
import { MainLayout } from './components/layout/MainLayout'
import { DashboardPage } from './pages/Dashboard'
import { InventoryPage } from './pages/Inventory'
import { GoodsReceiptPage } from './pages/GoodsReceipt'
import { GoodsIssuePage } from './pages/GoodsIssue'
import { InventoryCountPage } from './pages/InventoryCount'
import { CrossDockingPage } from './pages/CrossDocking'
import { PackagingPage } from './pages/Packaging'
import { LoginPage } from './pages/Login'

// Root route
const rootRoute = createRootRoute({
    component: () => (
        <MainLayout>
            <Outlet />
        </MainLayout>
    ),
})

// Index route (Dashboard)
const indexRoute = createRoute({
    getParentRoute: () => rootRoute,
    path: '/',
    component: DashboardPage,
})

// Inventory route
const inventoryRoute = createRoute({
    getParentRoute: () => rootRoute,
    path: '/inventory',
    component: InventoryPage,
})

// Goods Receipt route
const receiptRoute = createRoute({
    getParentRoute: () => rootRoute,
    path: '/receipt',
    component: GoodsReceiptPage,
})

// Goods Issue route
const issueRoute = createRoute({
    getParentRoute: () => rootRoute,
    path: '/issue',
    component: GoodsIssuePage,
})

// Cross Docking route
const crossDockRoute = createRoute({
    getParentRoute: () => rootRoute,
    path: '/cross-dock',
    component: CrossDockingPage,
})

// Inventory Count route
const countRoute = createRoute({
    getParentRoute: () => rootRoute,
    path: '/inventory-count',
    component: InventoryCountPage,
})

// Packaging route
const packagingRoute = createRoute({
    getParentRoute: () => rootRoute,
    path: '/packaging',
    component: PackagingPage,
})

// Login route (No Layout)
const loginRoute = createRoute({
    getParentRoute: () => rootRoute, // Temporarily under root for simplicity, but can be separate
    path: '/login',
    component: LoginPage,
})

// Create router tree
const routeTree = rootRoute.addChildren([
    indexRoute,
    inventoryRoute,
    receiptRoute,
    issueRoute,
    crossDockRoute,
    countRoute,
    packagingRoute,
    loginRoute,
])

export const router = createRouter({ routeTree })

declare module '@tanstack/react-router' {
    interface Register {
        router: typeof router
    }
}
