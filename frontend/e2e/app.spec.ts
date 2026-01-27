import { test, expect } from '@playwright/test'

test.describe('Warehouse Management App', () => {
    test('should load the login page', async ({ page }) => {
        await page.goto('/')
        await expect(page).toHaveTitle(/Warehouse Management/)
    })
})
