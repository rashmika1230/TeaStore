

async function loadUsers() {

//    alert();
    const response = await fetch(`LoadUsers?page=${currentUserPage}&size=${userPageSize}`);

    if (response.ok) {

        const responseJson = await response.json();

        if (responseJson.status) {
            console.log(responseJson);
            userTable(responseJson.userList);
            updateUserPagination(responseJson.totalPages);
        } else {

            popup.error({
                message: responseJson.message
            });

        }

    } else {
        popup.error({
            message: "Somthing went wrong"
        });
    }
}

function userTable(userList) {
    const tbody = document.getElementById('userTableBody');
    tbody.innerHTML = '';

    if (!userList || userList.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-center">can not found users</td></tr>`;
        return;
    }

    userList.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.first_name} ${user.last_name}</td>
            <td>${user.email}</td>
            <td>${user.mobile}</td>
            <td>${user.created_at}</td>
            <td>${user.verification}</td>

        `;
        tbody.appendChild(row);
    });
}

let currentUserPage = 1;
const userPageSize = 10;

function updateUserPagination(totalPages) {
    const pagination = document.getElementById('pagination-user');
    pagination.innerHTML = '';

    if (totalPages === 0)
        return;

    // Previous button
    const prevItem = document.createElement('li');
    prevItem.className = `page-item ${currentUserPage <= 1 ? 'disabled' : ''}`;
    prevItem.innerHTML = `
        <a class="page-link"  ${currentUserPage > 1 ? 'onclick="changePage(' + (currentUserPage - 1) + ')"' : ''}>
            &laquo;
        </a>
    `;
    pagination.appendChild(prevItem);

    // Page numbers
    const startPage = Math.max(1, currentUserPage - 2);
    const endPage = Math.min(totalPages, currentUserPage + 2);

    for (let i = startPage; i <= endPage; i++) {
        const pageItem = document.createElement('li');
        pageItem.className = `page-item ${i === currentUserPage ? 'active' : ''}`;
        pageItem.innerHTML = `<a class="page-link"  onclick="changePage(${i})">${i}</a>`;
        pagination.appendChild(pageItem);
    }

    // Next button
    const nextItem = document.createElement('li');
    nextItem.className = `page-item ${currentUserPage >= totalPages ? 'disabled' : ''}`;
    nextItem.innerHTML = `
        <a class="page-link"  ${currentUserPage < totalPages ? 'onclick="changePages(' + (currentUserPage + 1) + ')"' : ''}>
            &raquo;
        </a>
    `;
    pagination.appendChild(nextItem);
}

function changePages(page) {
    currentUserPage = parseInt(page); // Ensure it's a number
    loadUsers();
}

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    loadUsers();
});