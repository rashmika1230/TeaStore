const popup = new Notification();

payhere.onCompleted = function onCompleted(orderId) {
    popup.success({
        message: "Payment completed. Processing order..."
    });

    // Call CheckOut servlet to handle database operations
    processOrderAfterPayment();
};

// Payment window closed
payhere.onDismissed = function onDismissed() {
    popup.error({
        message: "Payment Dismissed: Contact Agent"
    });

    
};

// Error occurred
payhere.onError = function onError(error) {
    console.log("Error:" + error);
    popup.error({
        message: "Payment Error: " + error
    });
};

// New function to process order after successful payment
async function processOrderAfterPayment() {
    try {
        const response = await fetch("CheckOut", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({}) // Empty body since data is in session
        });

        if (response.ok) {
            const json = await response.json();
            if (json.status) {
                popup.success({
                    message: "Order completed successfully! Order ID: " + json.orderId
                });
                
                // Wait 2 seconds then redirect to orders page or home
                setTimeout(function () {
                    window.location = "index.html"; // or "orders.html" if you have one
                }, 2000);
                
            } else {
                popup.error({
                    message: "Order processing failed: " + json.message
                });
            }
        } else {
            popup.error({
                message: "Order processing failed. Please contact support."
            });
        }
    } catch (error) {
        console.error("Error processing order:", error);
        popup.error({
            message: "Order processing failed. Please contact support."
        });
    }
}

function continueBtn(id) {
    if (id === 1) {
        document.getElementById("panel3").click();
    } else if (id === 2) {
        document.getElementById("panel4").click();
    } else if (id === 3) {
        document.getElementById("panel1").click();
    } else if (id === 4) {
        document.getElementById("panel2").click();
    }
}

async function loadCheckOutData() {
    const response = await fetch("LoadCheckOutData");

    if (response.ok) {
        console.log("ok");
        const responseJson = await response.json();
        if (responseJson.status) {
            console.log(responseJson);

            const userAddress = responseJson.userAddress;
            const cartItems = responseJson.cartList;
            const cityList = responseJson.cityList;
            const deliveryTypes = responseJson.deliveryTypes;

            // load cities
            let city_select = document.getElementById("city-select");

            cityList.forEach(city => {
                let option = document.createElement("option");
                option.value = city.id;
                option.innerHTML = city.name;
                city_select.appendChild(option);
            });

            // load address
            const current_user_address_checkBox = document.getElementById("checkout-checkbox");

            current_user_address_checkBox.addEventListener("change", function () {
                let fname = document.getElementById("firstName");
                let lname = document.getElementById("lastName");
                let email = document.getElementById("email");
                let mobile = document.getElementById("mobile");
                let addressLine = document.getElementById("addressLine");
                let postalCode = document.getElementById("postalCode");

                if (current_user_address_checkBox.checked) {
                    fname.value = userAddress.user.first_name;
                    lname.value = userAddress.user.last_name;
                    email.value = userAddress.user.email;
                    mobile.value = userAddress.user.mobile;
                    city_select.value = userAddress.city.id;
                    city_select.disabled = true;
                    city_select.dispatchEvent(new Event("change"));
                    addressLine.value = userAddress.line;
                    postalCode.value = userAddress.postal_code;

                    document.getElementById("panel2").click();
                } else {
                    fname.value = "";
                    lname.value = "";
                    email.value = "";
                    mobile.value = "";
                    city_select.value = 0;
                    city_select.disabled = false;
                    city_select.dispatchEvent(new Event("change"));
                    addressLine.value = "";
                    postalCode.value = "";

                    document.getElementById("panel2").click();
                }
            });

            // cart details
            let checkout_order_body = document.getElementById("checkout-orders-body");
            let checkout_order_foot = document.getElementById("checkout-orders-foot");

            checkout_order_body.innerHTML = "";
            checkout_order_foot.innerHTML = "";

            let total = 0;
            let item_count = 0;

            cartItems.forEach(cart => {
                item_count += cart.qty;
                let item_sub_total = Number(cart.qty) * Number(cart.stock.price);

                let cart_data = `<tr>
                                     <td>
                                        <div class="o-pro-dec">
                                             <p>${cart.stock.product.name} - ${cart.stock.size.name}</p>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="o-pro-price">
                                            <p>Rs. ${new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(cart.stock.price)}</p>
                                        </div>
                                   </td>
                                   <td>
                                        <div class="o-pro-qty">
                                            <p>${cart.qty}</p>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="o-pro-subtotal">
                                            <p>Rs. ${new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(item_sub_total)}</p>
                                        </div>
                                    </td>
                                </tr>`;

                console.log(item_sub_total);
                total += item_sub_total;

                checkout_order_body.innerHTML += cart_data;
            });

            let shipping_charges = 0;

            document.getElementById("in-colombo").innerHTML = "Fixed: Rs. " + deliveryTypes[0].price + ".00";
            document.getElementById("out-colombo").innerHTML = "Fixed: Rs. " + deliveryTypes[1].price + ".00";

            city_select.addEventListener("change", (e) => {
                let cityName = city_select.options[city_select.selectedIndex].innerHTML;

                if (cityName === "Colombo") {
                    shipping_charges = deliveryTypes[0].price;
                } else {
                    // out of colombo
                    shipping_charges = deliveryTypes[1].price;
                }

                let cart_total_amounts = `<tr>
                                        <td colspan="3">Subtotal </td>
                                        <td colspan="1">Rs. ${new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(total)}</td>
                                     </tr>
                                    <tr class="tr-f">
                                        <td colspan="3">Shipping Fee</td>
                                        <td colspan="1">Rs. ${new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(shipping_charges)}</td>
                                     </tr>
                                     <tr>
                                        <td colspan="3">Grand Total</td>
                                        <td colspan="1">Rs. ${new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(total + shipping_charges)}</td>
                                     </tr>`;

                checkout_order_foot.innerHTML = cart_total_amounts;
            });

        } else {
            if (responseJson.message === "empty-cart") {
                popup.error({
                    message: "Empty cart. Please add some product"
                });
                window.location = "index.html";
            } else {
                popup.error({
                    message: responseJson.message
                });
            }
        }

    } else {
        if (response.status === 401) {
            window.location = "login-register.html";
        }
    }
}

// Updated checkOut function - now calls PayhereProcess instead of CheckOut
async function checkOut() {
    let checkBox1 = document.getElementById("checkout-checkbox").checked;
    let fname = document.getElementById("firstName").value;
    let lname = document.getElementById("lastName").value;
    let email = document.getElementById("email").value;
    let mobile = document.getElementById("mobile").value;
    let addressLine = document.getElementById("addressLine").value;
    let postalCode = document.getElementById("postalCode").value;
    let citySelect = document.getElementById("city-select").value;

    let data = {
        isCurrentAddress: checkBox1,
        firstName: fname,
        lastName: lname,
        email: email,
        mobile: mobile,
        addressLine: addressLine,
        postalCode: postalCode,
        citySelect: citySelect
    };

    let dataJson = JSON.stringify(data);

    const response = await fetch("PayhereProcess", {  // Changed from "CheckOut" to "PayhereProcess"
        method: "POST",
        headers: {  // Fixed: was "header" should be "headers"
            "Content-Type": "application/json"
        },
        body: dataJson
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);
            // PayHere Process
            payhere.startPayment(json.payhereJson);
        } else {
            popup.error({
                message: json.message
            });
        }
    } else {
        popup.error({
            message: "Something went wrong. Please try again!"
        });
    }
}