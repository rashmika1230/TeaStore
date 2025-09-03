// Enhanced tab management with dashboard refresh functionality
document.addEventListener('DOMContentLoaded', function () {

    // Tab refresh handlers
    const tabRefreshHandlers = {
        dashboard: refreshDashboard,
        products: refreshProducts,
        stock: refreshStock,
        userManagement: refreshUserManagement
    };

    // Override the existing tab switching to add refresh on dashboard click
    function enhanceTabSwitching() {
        const navItems = document.querySelectorAll('.nav-item[data-tab]');

        navItems.forEach(item => {
            item.addEventListener('click', function (e) {
                e.preventDefault();
                const tabId = this.getAttribute('data-tab');

                // Check if clicking on dashboard tab
                if (tabId === 'dashboard') {
                    switchTabWithRefresh(tabId);
                } else {
                    switchTab(tabId);
                }
            });
        });
    }

    // Enhanced switch tab function with refresh capability
    function switchTabWithRefresh(tabId) {
        // First switch to the tab
        switchTab(tabId);

        // Then refresh it
        setTimeout(() => {
            refreshTab(tabId);
        }, 300); // Small delay to ensure tab is switched first
    }

    // Standard tab switching function
    function switchTab(tabId) {
        // Hide all content areas
        document.querySelectorAll('.content-area').forEach(area => {
            area.classList.remove('active');
        });

        // Show the selected content area
        const targetTab = document.getElementById(tabId);
        if (targetTab) {
            targetTab.classList.add('active');
        }

        // Update active nav item
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });

        // Find the nav item with matching data-tab and activate it
        const activeNavItem = document.querySelector(`.nav-item[data-tab="${tabId}"]`);
        if (activeNavItem) {
            activeNavItem.classList.add('active');
        }
    }

    // Global function to refresh specific tab
    window.refreshTab = function (tabId) {
        console.log(`Refreshing tab: ${tabId}`);

        if (tabRefreshHandlers[tabId]) {
            // Show loading indicator
            showTabLoading(tabId);

            // Call the specific refresh handler
            tabRefreshHandlers[tabId]()
                    .then(() => {
                        hideTabLoading(tabId);
                        showRefreshSuccess(tabId);
                    })
                    .catch((error) => {
                        hideTabLoading(tabId);
                        console.error(`Error refreshing ${tabId}:`, error);
                        showRefreshError(tabId);
                    });
        } else {
            console.warn(`No refresh handler found for tab: ${tabId}`);
        }
    };

    // Function to refresh all tabs when dashboard is clicked
    window.refreshAllTabs = function () {
        console.log('Refreshing all tabs from dashboard...');

        const tabIds = ['dashboard', 'products', 'stock', 'userManagement'];

        // Show global loading
        showGlobalLoading();

        // Refresh all tabs sequentially
        const refreshPromises = tabIds.map(tabId => {
            if (tabRefreshHandlers[tabId]) {
                return tabRefreshHandlers[tabId]();
            }
            return Promise.resolve();
        });

        Promise.all(refreshPromises)
                .then(() => {
                    hideGlobalLoading();
                    if (typeof popup !== 'undefined') {
//                    popup.success({
//                        message: 'All tabs refreshed successfully!'
//                    });
                    }
                })
                .catch((error) => {
                    hideGlobalLoading();
                    console.error('Error refreshing tabs:', error);
                    if (typeof popup !== 'undefined') {
//                    popup.error({
//                        message: 'Failed to refresh some tabs. Please try again.'
//                    });
                        popup.error({
                            message: 'Somthing went Wrong. Please try again.'
                        });
                    }
                });
    };

    // Show loading indicator for specific tab
    function showTabLoading(tabId) {
        const tabContent = document.getElementById(tabId);
        if (tabContent) {
            // Remove existing loading overlay
            const existingOverlay = tabContent.querySelector('.tab-loading-overlay');
            if (existingOverlay) {
                existingOverlay.remove();
            }

            // Add loading overlay
            const loadingOverlay = document.createElement('div');
            loadingOverlay.className = 'tab-loading-overlay';
            loadingOverlay.innerHTML = `
                <div class="loading-spinner">
                    <i class="fas fa-spinner fa-spin"></i>
                    <span>Refreshing ${getTabDisplayName(tabId)}...</span>
                </div>
            `;

            // Styling for loading overlay
            loadingOverlay.style.cssText = `
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: rgba(255, 255, 255, 0.9);
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 1000;
                backdrop-filter: blur(2px);
            `;

            // Style the spinner
            const spinner = loadingOverlay.querySelector('.loading-spinner');
            spinner.style.cssText = `
                text-align: center;
                color: var(--primary-color, #4caf50);
                font-size: 16px;
            `;

            const icon = spinner.querySelector('i');
            icon.style.cssText = `
                font-size: 32px;
                margin-bottom: 10px;
                display: block;
            `;

            tabContent.style.position = 'relative';
            tabContent.appendChild(loadingOverlay);
        }
    }

    // Show global loading for all tabs refresh
    function showGlobalLoading() {
        const mainContent = document.querySelector('.main-content');
        if (mainContent) {
            const globalOverlay = document.createElement('div');
            globalOverlay.className = 'global-loading-overlay';
            globalOverlay.innerHTML = `
                <div class="global-loading-spinner">
                    <i class="fas fa-sync-alt fa-spin"></i>
                    <h3>Refreshing Dashboard</h3>
                    <p>Updating all data...</p>
                </div>
            `;

            globalOverlay.style.cssText = `
                position: fixed;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: rgba(0, 0, 0, 0.5);
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 2000;
                backdrop-filter: blur(3px);
            `;

            const spinner = globalOverlay.querySelector('.global-loading-spinner');
            spinner.style.cssText = `
                background: white;
                padding: 40px;
                border-radius: 12px;
                text-align: center;
                box-shadow: 0 10px 30px rgba(0,0,0,0.3);
                color: var(--primary-color, #4caf50);
            `;

            document.body.appendChild(globalOverlay);
        }
    }

    // Hide global loading
    function hideGlobalLoading() {
        const globalOverlay = document.querySelector('.global-loading-overlay');
        if (globalOverlay) {
            globalOverlay.remove();
        }
    }

    // Hide loading indicator
    function hideTabLoading(tabId) {
        const tabContent = document.getElementById(tabId);
        if (tabContent) {
            const loadingOverlay = tabContent.querySelector('.tab-loading-overlay');
            if (loadingOverlay) {
                loadingOverlay.remove();
            }
        }
    }

    // Get display name for tab
    function getTabDisplayName(tabId) {
        const displayNames = {
            dashboard: 'Dashboard',
            products: 'Products',
            stock: 'Stock',
            userManagement: 'User Management'
        };
        return displayNames[tabId] || tabId;
    }

    // Show success message
    function showRefreshSuccess(tabId) {
        if (typeof popup !== 'undefined') {
//            popup.success({
//                message: `${getTabDisplayName(tabId)} refreshed successfully!`
//            });
        }
    }

    // Show error message
    function showRefreshError(tabId) {
        if (typeof popup !== 'undefined') {
//            popup.error({
//                message: `Failed to refresh ${getTabDisplayName(tabId)}. Please try again.`
//            });
        }
    }

    // Enhance refreshDashboard function
    async function refreshDashboard() {
        console.log('Refreshing dashboard...');

        try {
            // Force reload all dashboard data
            await loadDashboardStats();
            await loadOrderHistory();

            // Add visual feedback
            document.querySelectorAll('.stat-card').forEach(card => {
                card.classList.add('refresh-pulse');
                setTimeout(() => card.classList.remove('refresh-pulse'), 1000);
            });

            console.log('Dashboard refreshed successfully');
        } catch (error) {
            console.error('Error refreshing dashboard:', error);
            throw error;
        }
    }



    async function refreshProducts() {
        console.log('Refreshing products...');

        try {
            // Clear and reset product form
            clearProductForm();

//            // Reload product data
//            if (typeof loadProductData === 'function') {
//                await loadProductData();
//            }

            // Reset image previews
            resetImagePreviews();

            // Update product preview
//            if (typeof updateProductPreview === 'function') {
//                updateProductPreview();
//            }

            console.log('Products refreshed successfully');
        } catch (error) {
            console.error('Error refreshing products:', error);
            throw error;
        }
    }

    async function refreshStock() {
        console.log('Refreshing stock...');

        try {
            // Clear stock form
            clearStockForm();

//            // Reload stock product data
//            if (typeof loadStockProductData === 'function') {
//                await loadStockProductData();
//            }
//            
//            // Reload stock details
//            if (typeof loadStockDetails === 'function') {
//                await loadStockDetails();
//            }

            // Reset product preview
            resetStockProductPreview();

            console.log('Stock refreshed successfully');
        } catch (error) {
            console.error('Error refreshing stock:', error);
            throw error;
        }
    }

    async function refreshUserManagement() {
        console.log('Refreshing user management...');

        try {
            // Reload user data
            if (typeof loadUserData === 'function') {
                await loadUserData();
            } else {
                // Simulate loading user data
                await new Promise(resolve => setTimeout(resolve, 800));
            }

            console.log('User management refreshed successfully');
        } catch (error) {
            console.error('Error refreshing user management:', error);
            throw error;
        }
    }

    // Helper functions
    function updateDashboardStats() {
        // Update statistics with animation
        const statCards = document.querySelectorAll('.stat-card h3');
        statCards.forEach(stat => {
            stat.style.transform = 'scale(1.1)';
            setTimeout(() => {
                stat.style.transform = 'scale(1)';
            }, 200);
        });
    }

    function clearProductForm() {
        const fields = ['productName', 'productDescription'];
        const selects = ['productCategory', 'productColor'];

        fields.forEach(fieldId => {
            const field = document.getElementById(fieldId);
            if (field)
                field.value = '';
        });

        selects.forEach(selectId => {
            const select = document.getElementById(selectId);
            if (select)
                select.value = '0';
        });

        // Hide new forms
        const newForms = ['newCategoryForm', 'newColorForm'];
        newForms.forEach(formId => {
            const form = document.getElementById(formId);
            if (form)
                form.style.display = 'none';
        });
    }

    function resetImagePreviews() {
        for (let i = 1; i <= 6; i++) {
            const preview = document.getElementById(`imagePreview${i}`);
            const fileInput = document.getElementById(`fileInput${i}`);

            if (preview)
                preview.innerHTML = '';
            if (fileInput)
                fileInput.value = '';
        }
    }

    function clearStockForm() {
        const fields = ['productSelect', 'sizeSelect', 'stockPrice', 'qty'];

        fields.forEach(fieldId => {
            const field = document.getElementById(fieldId);
            if (field) {
                field.value = fieldId === 'productSelect' || fieldId === 'sizeSelect' ? '0' : '0';
            }
        });
    }

    function resetStockProductPreview() {
        const previewFields = ['stockProductId', 'stockProductName', 'stockProductCategory', 'stockProductColor', 'stockProductDate'];

        previewFields.forEach(fieldId => {
            const field = document.getElementById(fieldId);
            if (field)
                field.textContent = '-';
        });

        const stockProductImage = document.getElementById('stockProductImage');
        if (stockProductImage)
            stockProductImage.src = '#';
    }

    // Initialize enhanced tab switching
    enhanceTabSwitching();

    // Expose global functions
    window.switchTab = switchTab;
    window.switchTabWithRefresh = switchTabWithRefresh;

    console.log('Dashboard tab refresh system initialized');
});

// Additional utility functions
window.refreshCurrentTab = function () {
    const activeTab = document.querySelector('.content-area.active');
    if (activeTab) {
        refreshTab(activeTab.id);
    }
};

window.addRefreshButtonToDashboard = function () {
    const dashboardHeader = document.querySelector('#dashboard .page-header');
    if (dashboardHeader) {
        const refreshButton = document.createElement('button');
        refreshButton.className = 'btn btn-outline';
        refreshButton.innerHTML = '<i class="fas fa-sync-alt"></i> Refresh All';
        refreshButton.onclick = () => refreshAllTabs();

        const existingButton = dashboardHeader.querySelector('.btn');
        if (existingButton) {
            existingButton.parentNode.insertBefore(refreshButton, existingButton);
        } else {
            dashboardHeader.appendChild(refreshButton);
        }
    }
};