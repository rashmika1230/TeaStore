
const popup = Notification();

function indexFunction() {
    checkSessionCart();
    loadHomeData();
}

async function checkSessionCart() {

    const response = await fetch("CheckSessionCart");
    if (!response.ok) {
        popup.error({
            message: "Something went wrong! Try again shortly"
        });
    }
}

async function loadHomeData() {
//    alert();


    const response = await fetch("LoadHomeData");

    if (response.ok) {

//        console.log("ok");

        const responseJson = await response.json();

        if (responseJson.status) {
//            console.log(responseJson);

//            popup.success({
//                message: "Profile Update Success"
//            });

            const home_product = document.getElementById("home-product");

            const home_product_container = document.getElementById("home-product-container");

            home_product_container.innerHTML = "";

            responseJson.productList.forEach(stock => {
                let home_product_clone = home_product.cloneNode(true);

                home_product_clone.querySelector("#home-product-img1").src = "product-images//" + stock.product.id + "//image1.png";
                home_product_clone.querySelector("#home-product-name").innerHTML = stock.product.name;
                home_product_clone.querySelector("#home-product-a1").href = "product-details.html?sid=" + stock.id;
                home_product_clone.querySelector("#home-product-price").innerHTML = "Rs. " + new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(stock.price);
                home_product_clone.querySelector("#home-peoduct-view-modal").addEventListener(
                        "click", (e) => {
                    viewSingleModal(stock.id);
                    e.preventDefault();
                });
                home_product_clone.querySelector("#home-product-add-to-cart").addEventListener(
                        "click", (e) => {
                    addToCart(stock.id, 1);
                    e.preventDefault();
                });

                home_product_container.appendChild(home_product_clone);
            });

            $('.featured-product-actives').owlCarousel({
                loop: true,
                nav: false,
                autoplay: false,
                autoplayTimeout: 5000,
                navText: ['<i class="ion-ios-arrow-back"></i>', '<i class="ion-ios-arrow-forward"></i>'],
                item: 4,
                margin: 30,
                responsive: {
                    0: {
                        items: 1
                    },
                    576: {
                        items: 2
                    },
                    768: {
                        items: 3
                    },
                    992: {
                        items: 3
                    },
                    1100: {
                        items: 3
                    },
                    1200: {
                        items: 4
                    }
                }
            });

        } else {
            popup.error({
                message: "Somthing went wrong"
            });
        }



    }

}

async function viewSingleModal(sid) {

//    console.log(sid);

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

//            document.getElementById("modal-product-size").addEventListener(
//                    "change", (e) => {
//                searchSingleMoadal(pname, desc, clr);
//                e.preventDefault();
//            });

        } else {
            popup.error({
                message: "Somthing went wrong"
            });

        }

    } else {
        popup.error({
            message: "Somthing went wrong"
        });

    }
}

async function loadSizesInModal() {

    const response = await fetch("LoadStockData");
//    console.log("list size");
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
            popup.error({
                message: "Somthing went wrong"
            });
        }

    } else {
        popup.error({
            message: "Somthing went wrong"
        });
    }
}

//async function searchSingleMoadal(pname, desc, color) {
//
//    const productName = pname;
//    const productDesc = desc;
//    const productColor = color;
//    const productSize = document.getElementById("modal-product-size").value;
//
////    console.log(productName);
////    console.log(productDesc);
////    console.log(productColor);
////    console.log(productSize);
//    const data = {
//        productName: productName,
//        productDesc: productDesc,
//        productColor: productColor,
//        productSize: productSize
//    };
//
//    const response = await fetch(
//            "SearchModalData",
//            {
//                method: "POST",
//                body: JSON.stringify(data),
//                headers: {
//                    "Content-Type": "application/json"
//                }
//            }
//    );
//
//    if (response.ok) {
//
////        console.log("ok");
//
//        const json = await response.json();
//        if (json.status) {
//
//            json.stockList.forEach(item => {
//                console.log(item.id);
//            viewSingleModal(item.id);
//            });
//
//        } else {
//
//            console.log(json.message);
//        }
//
//    } else {
//    }
//
//}
//

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
//        const response = await fetch("LoadcartItems");
//                if (response.ok) {
//        console.log("ok");
//                const responseJson = await response.json();
//                if (responseJson.status) {
//        popup.success({
//        message: responseJson.message
//        });
//                console.log(responseJson);
//                const cart_table_body = document.getElementById("home-cart-item-list");
//                cart_table_body.innerHTML = "";
//                let total = 0;
//                let totalQty = 0;
//                responseJson.cartList.forEach(cart => {
//                let subtotal = cart.stock.price * cart.qty;
//                        total += subtotal;
//                        totalQty += cart.qty;
//                        let tableContent = `<li class="single-shopping-cart">
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
//                        cart_table_body.innerHTML += tableContent;
//                });
//                document.getElementById("cart-total1").innerHTML = "Rs. " + new Intl.NumberFormat("en-US",
//        {minimumFractionDigits: 2})
//                .format(total);
//        } else {
//        popup.error({
//        message: responseJson.message
//        });
//        }
//
//        } else {
//        popup.error({
//        message: "Somthing Went Wrong"
//        });
//        }
//        }
//
