const popup = new Notification();
// track current page & page size for users
let userPage = 1;
const userPageSize = 10;

document.addEventListener('DOMContentLoaded', () => {
    loadUserOrders();
});

async function loadUserOrders() {
    const resp = await fetch(`OrderHistory?page=${userPage}&size=${userPageSize}`);
    if (!resp.ok) {
        return popup.error({message: 'Unable to load your orders'});
    }

    const json = await resp.json();
    if (!json.status) {
        return popup.error({message: json.message});
    }

    renderUserOrdersTable(json.orderItemList);
    renderUserOrdersPagination(json.totalPages);
}

function renderUserOrdersTable(items) {
    const tbody = document.getElementById('user-orders-body');
    tbody.innerHTML = '';

    if (!items || !items.length) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-center">No orders found</td></tr>`;
        return;
    }

    items.forEach(o => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
                <td>#TEA-${o.orders.id}</td>
                <td>${o.orders.order_date}</td>
                <td>${o.stock.product.name}</td>
                <td>Rs. ${o.stock.price}.00</td>
                <td>${o.qty}</td>
                <td>Rs. ${o.qty * o.stock.price}.00</td>
                <td><span class="status">${o.orderStatus.value}</span></td>
            `;
        tbody.appendChild(tr);
    });
}

function renderUserOrdersPagination(totalPages) {
    const ul = document.getElementById('user-orders-pagination');
    ul.innerHTML = '';
    if (totalPages < 2)
        return;

    // Prev
    ul.appendChild(pageLi(userPage > 1, userPage - 1, 'Prev'));

    // Numbers
    const start = Math.max(1, userPage - 2);
    const end = Math.min(totalPages, userPage + 2);
    for (let i = start; i <= end; i++) {
        ul.appendChild(pageLi(true, i, i, i === userPage));
    }

    // Next
    ul.appendChild(pageLi(userPage < totalPages, userPage + 1, 'Next'));
}

function pageLi(enabled, page, label, active = false) {
    const li = document.createElement('li');
    li.className = `page-item  ${!enabled ? 'disabled' : ''} ${active ? 'active' : ''}`;
    li.innerHTML = `<a class="page-success " ${enabled ? `onclick="goToUserPage(${page})"` : ''}>${label}</a>`;
    return li;
}

function goToUserPage(p) {
    userPage = p;
    loadUserOrders();
}