//const popup = new Notification();
async function loadStockProductData() {
//    alert();

    const response = await fetch("LoadStockData");

    if (response.ok) {
        console.log("response ok");
        const responseJson = await response.json();
        if (responseJson.status) {//true
//            console.log(responseJson.productList);
//            console.log(responseJson.sizeList);

            loadSelect("productSelect", responseJson.productList, "name");
            loadSelect("sizeSelect", responseJson.sizeList, "name");
//            loadSelect("modal-sizes", responseJson.sizeList, "name");

        } else {
            console.log("false");
        }

    } else {
        popup.error({
            message: "Somthing Went Wrong"
        });
    }

}

function loadSelect(selectId, items, property) {
    const select = document.getElementById(selectId);
    items.forEach(item => {
        const option = document.createElement("option");
        option.value = item.id;
        option.innerHTML = item[property];
        select.appendChild(option);
    });
}

async function updateStock() {
    const productId = document.getElementById("productSelect").value;
    const stockPrice = document.getElementById("stockPrice").value;
    const sizeId = document.getElementById("sizeSelect").value;
    const qty = document.getElementById("qty").value;

    const form = new FormData();
    form.append("productId", productId);
    form.append("sizeId", sizeId);
    form.append("stockPrice", stockPrice);
    form.append("qty", qty);

    const response = await fetch("UpdateStock",
            {
                method: "POST",
                body: form
            }
    );

    if (response.ok) {
        console.log("res ok");
        const responseJson = await response.json();
        if (responseJson.status) {//true
            popup.success({
                message: responseJson.message
            });
            loadStockDetails();

            document.getElementById("sm").click();
        } else {
            popup.error({
                message: responseJson.message
            });

        }
    } else {
        popup.error({
            message: "Somthing Went Wrong"
        });
    }
}

async function loadProductForStock() {
    const selectProduct = document.getElementById("productSelect").value;
//    console.log(selectProduct);
    const response = await fetch("LoardProductForStock?pid=" + selectProduct);

    if (response.ok) {
        console.log("ok");
        const responseJson = await response.json();
        if (responseJson.status) {//true
//            console.log("true");
            popup.success({
                message: "Product Loaded"
            });
            console.log(responseJson);

            document.getElementById("stockProductImage").src = "product-images\\" + responseJson.product.id + "\\image1.png";
            document.getElementById("stockProductId").innerHTML = responseJson.product.id;
            document.getElementById("stockProductName").innerHTML = responseJson.product.name;
            document.getElementById("stockProductCategory").innerHTML = responseJson.product.category.name;
            document.getElementById("stockProductColor").innerHTML = responseJson.product.color.name;
            document.getElementById("stockProductDate").innerHTML = responseJson.product.created_at;
        } else {
//            console.log("false");
            popup.error({
                message: responseJson.message
            });

        }

    } else {
        popup.error({
            message: "Somthing Went Wrong"
        });
    }

}



//async function loadProductForStock() {
//    const selectProduct = document.getElementById("productSelect").value;
////    console.log(selectProduct);
//
//const productData = {
//    
//    productId:selectProduct
//};
//
//const productJson = JSON.stringify(productData);
//
//    const response = await fetch(
//            "LoadStockData",
//            {
//                method:"POST",
//                body:productJson,
//                headers:{
//                    "Content-Type": "application/json"
//                }
//            }
//
//    );
//}

let currentPage = 1;
const pageSize = 10;

async function loadStockDetails() {
    try {
        // FIXED: Proper URL construction with encodeURIComponent
        const url = `LoadStockDetails?page=${encodeURIComponent(currentPage)}&size=${encodeURIComponent(pageSize)}`;

        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();
        if (data.status) {
            populateStockTable(data.stockList);
            updatePagination(data.totalPages);
        } else {
            console.error("Server error:", data.message);
            alert("Server error: " + data.message);
        }
    } catch (error) {
        console.error("Network error:", error);
        alert("Network error: " + error.message);
    }
}

function populateStockTable(stockList) {
    const tbody = document.getElementById('stockDetailsBody');
    tbody.innerHTML = '';

    if (!stockList || stockList.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-center">No stock records found</td></tr>`;
        return;
    }

    stockList.forEach(stock => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${stock.date}</td>
            <td>${stock.productName}</td>
            <td>${stock.sizeName}</td>
            <td>${stock.price.toFixed(2)}</td>
            <td>${stock.quantity}</td>
            <td><button  onclick="loadUpdateStockData(${stock.id});" type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#updateStockModal">
        acction
    </button></td>
        `;
        tbody.appendChild(row);
    });
}

async function loadUpdateStockData(sid) {

    console.log(sid);
    const response = await fetch("LoadUpdateStockData?sid=" + sid);

    if (response.ok) {
        console.log("ok");
        const responseJson = await response.json();
        if (responseJson.status) {//true
//            console.log(responseJson.size);

            const sizeSelect = document.getElementById("modal-sizes");
            sizeSelect.innerHTML = "";

            responseJson.sizeList.forEach(size => {
                let option = document.createElement("option");
                option.innerHTML = size.name;
                option.value = size.id;
                sizeSelect.appendChild(option);

            });

            sizeSelect.value = responseJson.size;
            document.getElementById("alert-message").innerHTML = "";
            document.getElementById("modal-product").value = responseJson.productName;
            document.getElementById("modal-product").disabled = true;
            document.getElementById("stockId").value = responseJson.stockId;
            document.getElementById("stockId").disabled = true;
            document.getElementById("modal-price").value = responseJson.price;
            document.getElementById("modal-qty").value = responseJson.qty;


        } else {
//            console.log("false");
            popup.error({
                message: responseJson.message
            });

        }

    } else {
        popup.error({
            message: "Somthing Went Wrong"
        });
    }


}

async function updateStockModal() {
    const stockId = document.getElementById("stockId").value;
    const price = document.getElementById("modal-price").value;
    const qty = document.getElementById("modal-qty").value;
    const sizeId = document.getElementById("modal-sizes").value; // <- fixed

    const payload = {
        stockId: stockId,
        sizeId: sizeId,
        stockPrice: price,
        qty: qty
    };

    const response = await fetch("UpdateStockDataModal", {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(payload)
    });

    if (response.ok) {
        const responseJson = await response.json();
        if (responseJson.status) {

            setTimeout(function () {
                document.getElementById("cancel-btn").click();
            }, 1000);
            loadStockDetails();
            popup.success({message: responseJson.message});


        } else {
        document.getElementById("alert-message").innerHTML = responseJson.message;
        }
    } else {

        popup.error({message: "Something went wrong"});
    }
}


function updatePagination(totalPages) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';

    if (totalPages === 0)
        return;

    // Previous button
    const prevItem = document.createElement('li');
    prevItem.className = `page-item ${currentPage <= 1 ? 'disabled' : ''}`;
    prevItem.innerHTML = `
        <a class="page-link"  ${currentPage > 1 ? 'onclick="changePage(' + (currentPage - 1) + ')"' : ''}>
            &laquo;
        </a>
    `;
    pagination.appendChild(prevItem);

    // Page numbers
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);

    for (let i = startPage; i <= endPage; i++) {
        const pageItem = document.createElement('li');
        pageItem.className = `page-item ${i === currentPage ? 'active' : ''}`;
        pageItem.innerHTML = `<a class="page-link"  onclick="changePage(${i})">${i}</a>`;
        pagination.appendChild(pageItem);
    }

    // Next button
    const nextItem = document.createElement('li');
    nextItem.className = `page-item ${currentPage >= totalPages ? 'disabled' : ''}`;
    nextItem.innerHTML = `
        <a class="page-link"  ${currentPage < totalPages ? 'onclick="changePage(' + (currentPage + 1) + ')"' : ''}>
            &raquo;
        </a>
    `;
    pagination.appendChild(nextItem);
}

function changePage(page) {
    currentPage = parseInt(page); // Ensure it's a number
    loadStockDetails();
}

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    loadStockDetails();
});