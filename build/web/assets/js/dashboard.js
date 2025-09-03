
//function  laodDashBoardData() {
//
//    loadOrderHistory();
//}
// track current page and page size
let currentpage1 = 1;
const pageSize1 = 10;

// on load, fetch page 1
document.addEventListener('DOMContentLoaded', () => {
    loadOrderHistory();
});

async function loadOrderHistory() {
    // 1) include page & size in the query string
    const response = await fetch(`Dashboard?page=${currentpage1}&size=${pageSize1}`);

    if (response.ok) {
        const responseJson = await response.json();

        if (responseJson.status) {
            console.log(responseJson);

            recentOrdersTable(responseJson.orderItemList);
            updateRecentOrdersPagination(responseJson.totalPages);
        } else {
            popup.error({
                message: responseJson.message
            });
        }

    } else {
        popup.error({
            message: "something went wrong"
        });
    }
}

function recentOrdersTable(orderList) {
    const tbody = document.getElementById('recent-orders');
    tbody.innerHTML = '';

    if (!orderList || orderList.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-center">No stock records found</td></tr>`;
        return;
    }

    orderList.forEach(orderItems => {
        const row = document.createElement('tr');
        row.innerHTML = `
    <td>#TEA-${orderItems.orders.id}</td>
    <td>${orderItems.stock.product.name}</td>
    <td>${orderItems.orders.order_date}</td>
    <td>Rs. ${orderItems.qty * orderItems.stock.price}.00</td>
    <td><span class="status ${orderItems.orderStatus.value === "Pending" ? "status-pending" : "status-active"}">${orderItems.orderStatus.value}</span></td>
    <td><button class="btn btn-danger" onclick="changeStatus(${orderItems.id});">Change</button></td>
`;


        tbody.appendChild(row);
    });
}

function updateRecentOrdersPagination(totalPages) {
    const pagination = document.getElementById('order-pagination');
    pagination.innerHTML = '';

    if (totalPages <= 1)
        return;

    // Previous button
    const prevItem = document.createElement('li');
    prevItem.className = `page-item ${currentpage1 <= 1 ? 'disabled' : ''}`;
    prevItem.innerHTML = `
            <a class="page-link" ${currentpage1 > 1 ? `onclick="changePage1(${currentpage1 - 1})"` : ''}>&laquo;</a>
        `;
    pagination.appendChild(prevItem);

    // Page numbers
    const startPage1 = Math.max(1, currentpage1 - 2);
    const endPage1 = Math.min(totalPages, currentpage1 + 2);

    for (let i = startPage1; i <= endPage1; i++) {
        const pageItem = document.createElement('li');
        pageItem.className = `page-item ${i === currentpage1 ? 'active' : ''}`;
        pageItem.innerHTML = `<a class="page-link" onclick="changePage1(${i})">${i}</a>`;
        pagination.appendChild(pageItem);
    }

    // Next button
    const nextItem = document.createElement('li');
    nextItem.className = `page-item ${currentpage1 >= totalPages ? 'disabled' : ''}`;
    nextItem.innerHTML = `
            <a class="page-link" ${currentpage1 < totalPages ? `onclick="changePage1(${currentpage1 + 1})"` : ''}>&raquo;</a>
        `;
    pagination.appendChild(nextItem);
}

function changePage1(page) {
    currentpage1 = page;
    loadOrderHistory();
}



async function changeStatus(orderItemsId) {
    try {
        const response = await fetch("ChangeOrderStatus?oid=" + orderItemsId);
        
        if (response.ok) {
            const responseJson = await response.json();
            
            if (responseJson.status) {
                // 1. Force reload current tab
                loadOrderHistory();
                refreshTab('dashboard');
                // 2. Show success with refresh reminder
                popup.success({
                    message: 'Status updated! Refreshing data...',
                    duration: 3000
                });
            }
        }
    } catch (error) {
        popup.error({ message: "Update failed: " + error });
    }
}


// Load dashboard stats on page load
document.addEventListener('DOMContentLoaded', () => {
    loadDashboardStats();
});

async function loadDashboardStats() {
    try {
        const response = await fetch('DashboardStats');

        if (response.ok) {
            const responseJson = await response.json();

            if (responseJson.status) {
                console.log(responseJson);
                //admin details
                document.getElementById("admin-name").innerHTML ="Admin - " + responseJson.fname +" "+ responseJson.lname;
                updateDashboardStats(responseJson.stats);
            } else {
                popup.error({
                    message: responseJson.message || "Failed to load dashboard statistics"
                });
            }
        } else {
            popup.error({
                message: "Something went wrong while loading dashboard stats"
            });
        }
    } catch (error) {
        console.error('Error loading dashboard stats:', error);
        popup.error({
            message: "Network error while loading dashboard stats"
        });
    }
}

function updateDashboardStats(stats) {



    // Update Total Users
    const usersCard = document.querySelector('.stat-card.stat-users');
    if (usersCard) {
        const userCount = usersCard.querySelector('.stat-info h3');
        const userTrend = usersCard.querySelector('.stat-trend span');

        if (userCount)
            userCount.textContent = formatNumber(stats.users.total);
        if (userTrend)
            userTrend.textContent = `${stats.users.growthPercentage}% this month`;

        // Update trend direction
        const trendIcon = usersCard.querySelector('.stat-trend i');
        const trendContainer = usersCard.querySelector('.stat-trend');
        if (stats.users.growthPercentage >= 0) {
            trendIcon.className = 'fas fa-arrow-up';
            trendContainer.className = 'stat-trend trend-up';
        } else {
            trendIcon.className = 'fas fa-arrow-down';
            trendContainer.className = 'stat-trend trend-down';
        }
    }

    // Update Tea Products
    const productsCard = document.querySelector('.stat-card.stat-products');
    if (productsCard) {
        const productCount = productsCard.querySelector('.stat-info h3');
        const productTrend = productsCard.querySelector('.stat-trend span');

        if (productCount)
            productCount.textContent = formatNumber(stats.products.total);
        if (productTrend)
            productTrend.textContent = `${stats.products.newThisMonth} new this month`;
    }

    // Update Monthly Orders
    const ordersCard = document.querySelector('.stat-card.stat-orders');
    if (ordersCard) {
        const orderCount = ordersCard.querySelector('.stat-info h3');
        const orderTrend = ordersCard.querySelector('.stat-trend span');

        if (orderCount)
            orderCount.textContent = formatNumber(stats.orders.total);
        if (orderTrend)
            orderTrend.textContent = `${stats.orders.growthPercentage}% from last month`;

        // Update trend direction
        const trendIcon = ordersCard.querySelector('.stat-trend i');
        const trendContainer = ordersCard.querySelector('.stat-trend');
        if (stats.orders.growthPercentage >= 0) {
            trendIcon.className = 'fas fa-arrow-up';
            trendContainer.className = 'stat-trend trend-up';
        } else {
            trendIcon.className = 'fas fa-arrow-down';
            trendContainer.className = 'stat-trend trend-down';
        }
    }

    // Update Monthly Revenue
    const revenueCard = document.querySelector('.stat-card.stat-revenue');
    if (revenueCard) {
        const revenueAmount = revenueCard.querySelector('.stat-info h3');
        const revenueTrend = revenueCard.querySelector('.stat-trend span');

        if (revenueAmount)
            revenueAmount.textContent = `Rs. ${formatCurrency(stats.revenue.total)}`;
        if (revenueTrend)
            revenueTrend.textContent = `${stats.revenue.growthPercentage}% from last month`;

        // Update trend direction
        const trendIcon = revenueCard.querySelector('.stat-trend i');
        const trendContainer = revenueCard.querySelector('.stat-trend');
        if (stats.revenue.growthPercentage >= 0) {
            trendIcon.className = 'fas fa-arrow-up';
            trendContainer.className = 'stat-trend trend-up';
        } else {
            trendIcon.className = 'fas fa-arrow-down';
            trendContainer.className = 'stat-trend trend-down';
        }
    }


}

// Helper function to format numbers with commas
function formatNumber(number) {
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// Helper function to format currency
function formatCurrency(amount) {
    return parseFloat(amount).toLocaleString('en-US', {
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
    });
}

// Function to refresh dashboard stats (can be called from other parts of the application)
function refreshDashboardStats() {
    loadDashboardStats();
}

