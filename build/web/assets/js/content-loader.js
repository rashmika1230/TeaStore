// Custom Header and Footer Loader Functions

function loadHeader() {
const headerData = `
    <div class="header-bottom">
        <div class="container">
            <div class="row">
                <div class="col-lg-3 col-md-4 col-6">
                    <div class="logo">
                        <a href="index.html">
                            <img alt="" src="assets/img/logo/logo.png">
                        </a>
                    </div>
                </div>
                <div class="col-lg-9 col-md-8 col-6">
                    <div class="header-bottom-right">
                        <div class="main-menu">
                            <nav>
                                <ul>
                                    <li class="top-hover"><a href="index.html">home</a></li>
                                    <li><a href="about-us.html">about</a></li>
                                    <li class="mega-menu-position top-hover"><a href="shop.html">shop</a></li>
                                    
                                    <li><a href="my-account.html">My Account</a></li>
                                    <li><a href="contact.html">contact</a></li>
                                    
                                </ul>
                            </nav>
                        </div>
                        <div class="header-currency">
                            
                        </div>
                        <div class="header-cart">
                            <a href="#">
                                <div class="cart-icon">
                                    <i class="ti-shopping-cart" onclick="viewCart();"></i>
                                </div>
                            </a>
                            <div class="shopping-cart-content">
                                <ul id="home-cart-item-list">
                                    
                                    
                                </ul>
                                <div class="shopping-cart-total">
                                   
                                    <h4>Total : <span id="cart-total1" class="shop-total">0.00</span></h4>
                                </div>
                                <div class="shopping-cart-btn">
                                    <a href="cart-page.html">view cart</a>
                                    <a href="checkout.html">checkout</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="mobile-menu-area">
                <div class="mobile-menu">
                    <nav id="mobile-menu-active">
                        <ul class="menu-overflow">
                            <li><a href="#">HOME</a>
                                <ul>
                                    <li><a href="index.html">home version 1</a></li>
                                    <li><a href="index-2.html">home version 2</a></li>
                                </ul>
                            </li>
                            <li><a href="#">pages</a>
                                <ul>
                                    <li><a href="about-us.html">about us </a></li>
                                    <li><a href="shop.html">shop Grid</a></li>
                                    <li><a href="shop-list.html">shop list</a></li>
                                    <li><a href="product-details.html">product details</a></li>
                                    <li><a href="cart-page.html">cart page</a></li>
                                    <li><a href="checkout.html">checkout</a></li>
                                    <li><a href="wishlist.html">wishlist</a></li>
                                    <li><a href="my-account.html">my account</a></li>
                                    <li><a href="login-register.html">login / register</a></li>
                                    <li><a href="contact.html">contact</a></li>
                                </ul>
                            </li>
                            <li><a href="shop.html"> Shop </a>
                                <ul>
                                    <li><a href="#">Categories 01</a>
                                        <ul>
                                            <li><a href="shop.html">Aconite</a></li>
                                            <li><a href="shop.html">Ageratum</a></li>
                                            <li><a href="shop.html">Allium</a></li>
                                            <li><a href="shop.html">Anemone</a></li>
                                            <li><a href="shop.html">Angelica</a></li>
                                            <li><a href="shop.html">Angelonia</a></li>
                                        </ul>
                                    </li>
                                    <li><a href="#">Categories 02</a>
                                        <ul>
                                            <li><a href="shop.html">Balsam</a></li>
                                            <li><a href="shop.html">Baneberry</a></li>
                                            <li><a href="shop.html">Bee Balm</a></li>
                                            <li><a href="shop.html">Begonia</a></li>
                                            <li><a href="shop.html">Bellflower</a></li>
                                            <li><a href="shop.html">Bergenia</a></li>
                                        </ul>
                                    </li>
                                    <li><a href="#">Categories 03</a>
                                        <ul>
                                            <li><a href="shop.html">Caladium</a></li>
                                            <li><a href="shop.html">Calendula</a></li>
                                            <li><a href="shop.html">Carnation</a></li>
                                            <li><a href="shop.html">Catmint</a></li>
                                            <li><a href="shop.html">Celosia</a></li>
                                            <li><a href="shop.html">Chives</a></li>
                                        </ul>
                                    </li>
                                    <li><a href="#">Categories 04</a>
                                        <ul>
                                            <li><a href="shop.html">Daffodil</a></li>
                                            <li><a href="shop.html">Dahlia</a></li>
                                            <li><a href="shop.html">Daisy</a></li>
                                            <li><a href="shop.html">Diascia</a></li>
                                            <li><a href="shop.html">Dusty Miller</a></li>
                                            <li><a href="shop.html">Dame's Rocket</a></li>
                                        </ul>
                                    </li>
                                </ul>
                            </li>
                            <li><a href="#">BLOG</a>
                                <ul>
                                    <li><a href="blog-masonry.html">Blog Masonry</a></li>
                                    <li><a href="#">Blog Standard</a>
                                        <ul>
                                            <li><a href="blog-left-sidebar.html">left sidebar</a></li>
                                            <li><a href="blog-right-sidebar.html">right sidebar</a></li>
                                            <li><a href="blog-no-sidebar.html">no sidebar</a></li>
                                        </ul>
                                    </li>
                                    <li><a href="#">Post Types</a>
                                        <ul>
                                            <li><a href="blog-details-standerd.html">Standard post</a></li>
                                            <li><a href="blog-details-audio.html">audio post</a></li>
                                            <li><a href="blog-details-video.html">video post</a></li>
                                            <li><a href="blog-details-gallery.html">gallery post</a></li>
                                            <li><a href="blog-details-link.html">link post</a></li>
                                            <li><a href="blog-details-quote.html">quote post</a></li>
                                        </ul>
                                    </li>
                                </ul>
                            </li>
                            <li><a href="contact.html"> Contact us </a></li>
                        </ul>
                    </nav>
                </div>
            </div>
        </div>
    </div>`;
        const headerElement = document.querySelector("header");
        if (headerElement) {
headerElement.innerHTML = headerData;
        }
}

function loadFooter() {
const footerData = `
    <div class="footer-top gray-bg-3 pb-35">
        <div class="container">
            <div class="row">
                <div class="col-lg-3 col-md-6 col-sm-6">
                    <div class="footer-widget mb-40">
                        <div class="footer-title mb-25">
                            <h4>My Account</h4>
                        </div>
                        <div class="footer-content">
                            <ul>
                                <li><a href="my-account.html">My Account</a></li>
                                
                                
                                <li><a href="#">Newsletter</a></li>
                                <li><a href="order-history.html">Order History</a></li>
                                
                            </ul>
                        </div>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 col-sm-6">
                    <div class="footer-widget mb-40">
                        <div class="footer-title mb-25">
                            <h4>Information</h4>
                        </div>
                        <div class="footer-content">
                            <ul>
                                <li><a href="about-us.html">About Us</a></li>
                                <li><a href="#">Delivery Information</a></li>
                                <li><a href="#">Privacy Policy</a></li>
                                <li><a href="#">Terms & Conditions</a></li>
                                <li><a href="#">Customer Service</a></li>
                                <li><a href="#">Return Policy</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 col-sm-6">
                    <div class="footer-widget mb-40">
                        <div class="footer-title mb-25">
                            <h4>Quick Links</h4>
                        </div>
                        <div class="footer-content">
                            <ul>
                                <li><a href="#">Support Center</a></li>
                                <li><a href="#">Term & Conditions</a></li>
                                <li><a href="#">Shipping</a></li>
                                <li><a href="#">Privacy Policy</a></li>
                                <li><a href="#">Help</a></li>
                                <li><a href="#">FAQS</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 col-sm-6">
                    <div class="footer-widget footer-widget-red footer-black-color mb-40">
                        <div class="footer-title mb-25">
                            <h4>Contact Us</h4>
                        </div>
                        <div class="footer-about">
                            <p>Your current address goes to here,120 Colombo, Sri Lanka </p>
                            <div class="footer-contact mt-20">
                                <ul>
                                    <li>(+94) 123 456 758 </li>
                                    <li>(+94) 456 123 657</li>
                                </ul>
                            </div>
                            <div class="footer-contact mt-20">
                                <ul>
                                    <li>sabucha@gmail.com</li>
                                    <li>example@admin.com</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="footer-bottom pb-25 pt-25 gray-bg-2">
        <div class="container">
            <div class="row">
                <div class="col-md-6">
                    <div class="copyright">
                        <p><a target="_blank" href="https://www.templateshub.net">Templates Hub</a></p>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="payment-img f-right">
                        <a href="#">
                            <img alt="" src="assets/img/icon-img/payment.png">
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>`;
        const footerElement = document.querySelector("footer");
        if (footerElement) {
footerElement.innerHTML = footerData;
        }
}

// Initialize all functions when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
loadHeader();
        loadFooter();
        viewCart();
});
// Alternative: Call functions immediately (use this if you don't want to wait for DOM)
// loadHeader();
// loadFooter();

        async function viewCart() {
        const response = await fetch("LoadcartItems");
                if (response.ok) {
        console.log("ok");
                const responseJson = await response.json();
                if (responseJson.status) {

//        popup.success({
//        message: responseJson.message
//        });
        console.log(responseJson);
                const cart_table_body = document.getElementById("home-cart-item-list");
                cart_table_body.innerHTML = "";
                let total = 0;
                let totalQty = 0;
                responseJson.cartList.forEach(cart => {
                let subtotal = cart.stock.price * cart.qty;
                        total += subtotal;
                        totalQty += cart.qty;
                        let tableContent = `<li class="single-shopping-cart">
                                                <div class="shopping-cart-img">
                                                    <a href="#"><img alt="" style="width:50px;" src="product-images\\${cart.stock.product.id}\\image1.png"></a>
                                                </div>
                                                <div class="shopping-cart-title">
                                                    <h4><a href="#">${cart.stock.product.name}  -  ${cart.stock.size.name} </a></h4>
                                                    <h6>Qty: ${cart.qty}</h6>
                                                    <span>Rs. ${new Intl.NumberFormat("en-US",
                        {minimumFractionDigits: 2})
                        .format(cart.stock.price)}</span>
                                                </div>
                                                <div class="shopping-cart-delete">
                                                    <a href="#" onclick = "deleteFromCart(${cart.id},${cart.stock.id});"><i class="ion ion-close"></i></a>
                                                </div>
                                            </li>`;
                        cart_table_body.innerHTML += tableContent;
                });
                document.getElementById("cart-total1").innerHTML = "Rs. " + new Intl.NumberFormat("en-US",
        {minimumFractionDigits: 2})
                .format(total);
        } else {
//        popup.error({
//        message: responseJson.message
//        });
        }

        } else {
        popup.error({
        message: "Somthing Went Wrong"
        });
        }
        }

async function deleteFromCart(cid, sid) {


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
                body: JSON.stringify(cartData)
        }
        );
        if (response.ok) {

console.log("ok");
        const responseJson = await response.json();
        if (responseJson.status) {
viewCart();
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
