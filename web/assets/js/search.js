const popup = new Notification();
async function loadProductData() {

//    console.log("ok");

    const response = await fetch("LoadStockData");

    if (response.ok) {
        //console.log("res.ok");
        const responseJson = await response.json();
        if (response.status) {//true
            console.log(responseJson);
//            searchProducts(0);
            updateProductView(responseJson);


            loadOptions("category", responseJson.categoryList, "name");
            loadOptions("color", responseJson.colorList, "name");
            loadSelect("selectSize", responseJson.sizeList, "name");

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

function loadOptions(prefix, list, property) {
    let options = document.getElementById(prefix + "-options");
    options.innerHTML = "";

    list.forEach(item => {
        const li = document.createElement("li");

        const radio = document.createElement("input");
        radio.type = "radio";
        radio.value = item.id;
        radio.id = `${prefix}-${item.id}`;
        radio.name = prefix + "-group"; // Same name = mutually exclusive

        const label = document.createElement("label");
        label.htmlFor = radio.id;
        label.textContent = item[property];

        li.appendChild(radio);
        li.appendChild(label);
        options.appendChild(li);
    });
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

async function searchProducts(firstResult) {
    // Get selected category (single value)
    const categoryCheckbox = document.querySelector('#category-options input[type="radio"]:checked');
    const selectedCategory = categoryCheckbox ? categoryCheckbox.value : null;

    // Get selected color (single value)
    const colorCheckbox = document.querySelector('#color-options input[type="radio"]:checked');
    const selectedColor = colorCheckbox ? colorCheckbox.value : null;

    // Get selected size
    const sizeSelect = document.getElementById('selectSize');
    const selectedSize = sizeSelect.value;

    // Get price range from slider
    const price_range_start = $("#slider-range").slider("values", 0);
    const price_range_end = $("#slider-range").slider("values", 1);

    // Get sort value
    const sort_value = document.getElementById("ad-sort").value;

    const searchParams = {
        firstResult, firstResult,
        category: selectedCategory, // Single value or null
        color: selectedColor, // Single value or null
        size: selectedSize,
        sortValue: sort_value,
        priceRange: {
            min: price_range_start,
            max: price_range_end
        }
    };

    console.log("Search parameters:", searchParams);

  
    try {
        const response = await fetch("AdvanceSearch", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(searchParams)
        });

        if (response.ok) {
            const results = await response.json();
            console.log(results);
            updateProductView(results);
            // Handle displaying results here
        } else {
            popup.error({
                message: "Search failed"
            });
        }
    } catch (error) {
        popup.error({
            message: "Something went wrong"
        });
    }
}

const st_product = document.getElementById("st-product"); // product card parent node
//let st_pagination_button = document.getElementById("st-pagination-button");
//let current_page = 0;

function updateProductView(json) {
    const product_container = document.getElementById("st-product-container");
    product_container.innerHTML = "";
    json.stockList.forEach(stock => {
        let st_product_clone = st_product.cloneNode(true);// enable child nodes cloning / allow child nodes
        st_product_clone.querySelector("#st-product-a-1").href = "product-details.html?sid=" + stock.id;
        st_product_clone.querySelector("#st-product-img-1").src = "product-images//" + stock.product.id + "//image1.png";
        st_product_clone.querySelector("#st-product-add-to-cart").addEventListener(
                "click", (e) => {
            addToCart(stock.id, 1);
            e.preventDefault();
        });
        st_product_clone.querySelector("#search-product-view-modal").addEventListener(
                "click", (e) => {
            viewSingleModal(stock.id);
            e.preventDefault();
        });
        st_product_clone.querySelector("#st-product-a-2").href = "product-details.html?sid=" + stock.id;
        st_product_clone.querySelector("#st-product-title-1").innerHTML = stock.product.name + " - " + stock.size.name;
        st_product_clone.querySelector("#st-product-title-2").innerHTML = stock.product.name + " - " + stock.size.name;
        st_product_clone.querySelector("#st-product-desc").innerHTML = stock.product.description;
        st_product_clone.querySelector("#st-product-price-1").innerHTML = "Rs. " + new Intl.NumberFormat(
                "en-US",
                {minimumFractionDigits: 2})
                .format(stock.price);
        ;
        st_product_clone.querySelector("#st-product-price-2").innerHTML = "Rs. " + new Intl.NumberFormat(
                "en-US",
                {minimumFractionDigits: 2})
                .format(stock.price);
        ;
        //append child
        product_container.appendChild(st_product_clone);
    });

    //pagination
    updatePagination(json.allProductcount);
}
let currentPage = 0; // Page index starting from 0
const productsPerPage = 9;
function updatePagination(totalProducts) {
    const paginationContainer = document.getElementById("st-pagination-container");
    if (!paginationContainer)
        return;

    document.getElementById("all-item-count").innerHTML = totalProducts;

    paginationContainer.innerHTML = "";
    const totalPages = Math.ceil(totalProducts / productsPerPage);

    // Previous button
    if (currentPage > 0) {
        const prevButton = document.createElement('a');
        prevButton.className = 'prev-next prev';
        prevButton.href = '#';
        prevButton.innerHTML = '<i class="ion-ios-arrow-left"></i> Prev';
        prevButton.addEventListener('click', (e) => {
            e.preventDefault();
            currentPage--;
            searchProducts(currentPage * productsPerPage);
        });

        const prevLi = document.createElement('li');
        prevLi.appendChild(prevButton);
        paginationContainer.appendChild(prevLi);
    }

    // Page buttons
    for (let i = 0; i < totalPages; i++) {
        const pageButton = document.createElement('a');
        pageButton.href = '#';
        pageButton.textContent = i + 1;

        if (i === currentPage) {
            pageButton.className = 'active';
        }

        pageButton.addEventListener('click', (e) => {
            e.preventDefault();
            currentPage = i;
            searchProducts(i * productsPerPage);
        });

        const pageLi = document.createElement('li');
        pageLi.appendChild(pageButton);
        paginationContainer.appendChild(pageLi);
    }

    // Next button
    if (currentPage < totalPages - 1) {
        const nextButton = document.createElement('a');
        nextButton.className = 'prev-next next';
        nextButton.href = '#';
        nextButton.innerHTML = 'Next <i class="ion-ios-arrow-right"></i>';
        nextButton.addEventListener('click', (e) => {
            e.preventDefault();
            currentPage++;
            searchProducts(currentPage * productsPerPage);
        });

        const nextLi = document.createElement('li');
        nextLi.appendChild(nextButton);
        paginationContainer.appendChild(nextLi);
    }
}


async function viewSingleModal(sid) {

    console.log(sid);

    const response = await fetch("SingleProduct?sid=" + sid);

    if (response.ok) {
        console.log("ok");
        const responseJson = await response.json();
        if (responseJson.status) {
            console.log("true");
//            loadSizesInModal();

            document.getElementById("modal-image1").src = "product-images\\" + responseJson.stock.product.id + "\\image1.png";
            document.getElementById("modal-image2").src = "product-images\\" + responseJson.stock.product.id + "\\image2.png";
            document.getElementById("modal-image3").src = "product-images\\" + responseJson.stock.product.id + "\\image3.png";
            document.getElementById("modal-image4").src = "product-images\\" + responseJson.stock.product.id + "\\image4.png";

            document.getElementById("modal-a-image1").src = "product-images\\" + responseJson.stock.product.id + "\\image1.png";
            document.getElementById("modal-a-image2").src = "product-images\\" + responseJson.stock.product.id + "\\image2.png";
            document.getElementById("modal-a-image3").src = "product-images\\" + responseJson.stock.product.id + "\\image3.png";
            document.getElementById("modal-a-image4").src = "product-images\\" + responseJson.stock.product.id + "\\image4.png";

            document.getElementById("modal-product-name").innerHTML = responseJson.stock.product.name;
            document.getElementById("modal-product-description").innerHTML = responseJson.stock.product.description;
            document.getElementById("modal-product-price").innerHTML = "Rs. " + new Intl.NumberFormat(
                    "en-US",
                    {minimumFractionDigits: 2}).format(responseJson.stock.price);
            document.getElementById("modal-product-color").innerHTML = responseJson.stock.product.color.name;

            const addToCartButton = document.getElementById("add-to-cart-btn");
            addToCartButton.addEventListener(
                    "click", (e) => {
                addToCart(responseJson.stock.id, document.getElementById("add-to-cart-qty").value);
                e.preventDefault();
            });

            let selectSizes = document.getElementById("modal-product-size");

            selectSizes.innerHTML = responseJson.stock.size.name;
//            selectSizes.value = responseJson.stock.size.id;
//            selectSizes.disabled = true;
////            selectSizes.dispatchEvent(new Event("change"));

        } else {
            console.log("false");

        }

    } else {

    }
}

async function loadSizesInModal() {

    const response = await fetch("LoadStockData");
    console.log("list size");
    if (response.ok) {
        const responseJson = await response.json();
        if (responseJson.status) {
            console.log("list size true");

            const select = document.getElementById("modal-product-size");
            select.innerHTML = "";
            responseJson.sizeList.forEach(item => {
                const option = document.createElement("option");
                option.value = item.id;
                option.innerHTML = item["name"];
                select.appendChild(option);
            });

        } else {

        }

    } else {

    }
}

async function addToCart(stolckId, qty) {
    window.console.log(stolckId + qty);
    const response = await fetch("AddToCart?sid=" + stolckId + "&qty=" + qty);

    if (response.ok) {

        console.log("ok");
        const responseJson = await response.json();

        if (responseJson.status) {

            popup.success({
                message: responseJson.message
            });
            viewCart();
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

//async function viewCart() {
//    const response = await fetch("LoadcartItems");
//    if (response.ok) {
//        console.log("ok");
//        const responseJson = await response.json();
//        if (responseJson.status) {
//            popup.success({
//                message: responseJson.message
//            });
//            console.log(responseJson);
//            const cart_table_body = document.getElementById("home-cart-item-list");
//            cart_table_body.innerHTML = "";
//            let total = 0;
//            let totalQty = 0;
//            responseJson.cartList.forEach(cart => {
//                let subtotal = cart.stock.price * cart.qty;
//                total += subtotal;
//                totalQty += cart.qty;
//                let tableContent = `<li class="single-shopping-cart">
//                                                <div class="shopping-cart-img">
//                                                    <a href="#"><img alt="" style="width:50px;" src="product-images\\${cart.stock.product.id}\\image1.png"></a>
//                                                </div>
//                                                <div class="shopping-cart-title">
//                                                    <h4><a href="#">${cart.stock.product.name}  -  ${cart.stock.size.name} </a></h4>
//                                                    <h6>Qty: ${cart.qty}</h6>
//                                                    <span>Rs. ${new Intl.NumberFormat("en-US",
//                        {minimumFractionDigits: 2})
//                        .format(cart.stock.price)}</span>
//                                                </div>
//                                                <div class="shopping-cart-delete">
//                                                    <a href="#"><i class="ion ion-close"></i></a>
//                                                </div>
//                                            </li>`;
//                cart_table_body.innerHTML += tableContent;
//            });
//            document.getElementById("cart-total").innerHTML = "Rs. " + new Intl.NumberFormat("en-US",
//                    {minimumFractionDigits: 2})
//                    .format(total);
//        } else {
//            popup.error({
//                message: responseJson.message
//            });
//        }
//
//    } else {
//        popup.error({
//            message: "Somthing Went Wrong"
//        });
//    }
//}
//
//
