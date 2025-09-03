const popup = new Notification();
async function loadCartItems() {
    const response = await fetch("LoadcartItems");
    if (response.ok) {
//        console.log("ok cload");
        const responseJson = await response.json();
        if (responseJson.status) {
//            popup.success({
//                message: responseJson.message
//            });
            console.log(responseJson);
            const cart_table_body = document.getElementById("cart-table-body");
            cart_table_body.innerHTML = "";
            let total = 0;
            let totalQty = 0;
            
            responseJson.cartList.forEach(cart => {
                let subtotal = cart.stock.price * cart.qty;
                total += subtotal;
                totalQty += cart.qty;
                let tableContent = `<tr>
                                            <td class="product-thumbnail">
                                                <a href="#"><img style="width:75px;" src="product-images\\${cart.stock.product.id}\\image1.png" alt=""></a>
                                            </td>
                                            <td class="product-name">${cart.stock.product.name}  -  ${cart.stock.size.name} </a></td>
                                            <td class="product-price-cart"><span class="amount">Rs. ${new Intl.NumberFormat("en-US",
                        {minimumFractionDigits: 2})
                        .format(cart.stock.price)}</span></td>
                                            <td class="product-quantity">
                                                <div class="pro-dec-cart">
                                                    <input class="cart-plus-minus-box" type="text" value="${cart.qty}" name="qtybutton">
                                                </div>
                                            </td>
                                            <td class="product-subtotal">Rs. ${new Intl.NumberFormat("en-US",
                        {minimumFractionDigits: 2})
                        .format(subtotal)}</td>
                                            <td class="product-remove">
                                                
                                                <a onclick = "deleteFromCart(${cart.id},${cart.stock.id});"><i class="fa fa-times"></i></a>
                                           </td>
                                        </tr>`;
                cart_table_body.innerHTML += tableContent;
            });

            document.getElementById("cart-total-qty").innerHTML = totalQty;
            document.getElementById("cart-total").innerHTML = "Rs. " + new Intl.NumberFormat("en-US",
                    {minimumFractionDigits: 2})
                    .format(total);
        } else {
//            popup.error({
//                message: responseJson.message
//            });
        }

    } else {
        popup.error({
            message: "Somthing went wrong"
        });
    }
}

async function deleteFromCart(cid,sid) {


    const cartData = {
        cartId: cid,
        stockId: sid
    };

    const response = await fetch(
            "DeleteCartItem",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(cartData),
                cache: "no-store"
            }
    );

    if (response.ok) {

        console.log("ok");

        const responseJson = await response.json();

        if (responseJson.status) {
            await loadCartItems();
            
            popup.success({
                message: responseJson.message
            });
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

async function deleteAll() {
//    alert();


    const response = await fetch(
            "DeleteCartItem",
            {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json"
                }

            }
    );

    if (response.ok) {

        const responseJson = await response.json();

        if (responseJson.status) {
            loadCartItems();
            window.location.reload();
            popup.success({
                message: responseJson.message
            });

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