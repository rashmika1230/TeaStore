async  function laodSingleProductData() {

//    alert();
    const searchParams = new URLSearchParams(window.location.search);

    if (searchParams.has("sid")) {

        const productSid = searchParams.get("sid");
//        console.log(productSid);

        const response = await fetch("SingleProduct?sid=" + productSid);

        if (response.ok) {

            const responseJson = await response.json();

            if (responseJson.status) { //true

//                console.log(responseJson);

                document.getElementById("mainImage").src = "product-images\\" + responseJson.stock.product.id + "\\image1.png";
                document.getElementById("adImage1").src = "product-images\\" + responseJson.stock.product.id + "\\image2.png";
                document.getElementById("adImage2").src = "product-images\\" + responseJson.stock.product.id + "\\image3.png";
                document.getElementById("adImage3").src = "product-images\\" + responseJson.stock.product.id + "\\image4.png";
                document.getElementById("adImage4").src = "product-images\\" + responseJson.stock.product.id + "\\image5.png";
                document.getElementById("adImage5").src = "product-images\\" + responseJson.stock.product.id + "\\image6.png";

                document.getElementById("productName").innerHTML = responseJson.stock.product.name;
                document.getElementById("productPrice").innerHTML = new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2}).format(responseJson.stock.price);
                document.getElementById("productCategory").innerHTML = responseJson.stock.product.category.name;
                document.getElementById("productColor").innerHTML = responseJson.stock.product.color.name;
                document.getElementById("productSize").innerHTML = responseJson.stock.size.name;
                document.getElementById("productDescription").innerHTML = responseJson.stock.product.description;

                const addToCartButton = document.getElementById("add-to-cart-btn");
                addToCartButton.addEventListener(
                        "click", (e) => {
                    addToCart(responseJson.stock.id, document.getElementById("add-to-cart-qty").value);
                    e.preventDefault();
                });

                //related products
//            let relatedProductMain = document.getElementById("relatedProduct-main");
//            let relatedProduct = document.getElementById("relatedProduct");
//            
//            relatedProductMain.innerHTML = "";
//            
//            responseJson.stockList.forEach(item => {
//                let productClone = relatedProduct.cloneNode(true);
//                
//                productClone.querySelector("#relatedImage1").src ="product-images\\" + item.product.id + "\\image1.png";
//            
//                                    relatedProductMain.appendChild(productClone);
//
//                });

//            //related-product-end

// Related products
                const relatedContainer = document.getElementById("relatedProduct-main");
                relatedContainer.innerHTML = ""; // Clear existing content

                responseJson.stockList.forEach((item) => {
                    const productHTML = `
                <div class="product-wrapper">
                    <div class="product-img">
                        <a href="product-details.html?sid=${item.id}">
                            <img alt="${item.product.name}" 
                                 src="product-images/${item.product.id}/image1.png">
                        </a>
                        
                        <div class="product-action">
                            <!-- action buttons -->
                        </div>
                    </div>
                    <div class="product-content text-left">
                        <div class="product-hover-style">
                            <div class="product-title">
                                <h4><a href="product-details.html?sid=${item.id}">${item.product.name}</a></h4>
                            </div>
                            <div class="cart-hover">
                                <h4><a href="#">+ Add to cart</a></h4>
                            </div>
                        </div>
                        <div class="product-price-wrapper">
                            <span>Rs. ${item.price.toFixed(2)}</span>
                        </div>
                    </div>
                </div>`;
                    relatedContainer.insertAdjacentHTML("beforeend", productHTML);
                });

                // Reinitialize Owl Carousel
                $('.featured-product-active').owlCarousel('destroy').owlCarousel({
                    loop: true,
                    nav: false,
                    autoplay: false,
                    autoplayTimeout: 5000,
                    navText: ['<i class="ion-ios-arrow-back"></i>', '<i class="ion-ios-arrow-forward"></i>'],
                    items: 4,
                    margin: 30,
                    responsive: {
                        0: {items: 1},
                        576: {items: 2},
                        768: {items: 3},
                        992: {items: 3},
                        1200: {items: 4}
                    }
                });

                // 4. Refresh thumbnail carousel
                $('.product-dec-slider').slick('unslick').slick({
                    infinite: true,
                    slidesToShow: 4,
                    slidesToScroll: 1,
                    centerPadding: '60px',
                    prevArrow: '<span class="product-dec-icon product-dec-prev"><i class="fa fa-angle-left"></i></span>',
                    nextArrow: '<span class="product-dec-icon product-dec-next"><i class="fa fa-angle-right"></i></span>',
                    responsive: [{
                            breakpoint: 768,
                            settings: {slidesToShow: 3}
                        }, {
                            breakpoint: 480,
                            settings: {slidesToShow: 3}
                        }, {
                            breakpoint: 479,
                            settings: {slidesToShow: 2}
                        }]
                });


            } else {
                window.location = "index.html";
            }
        } else {

            window.location = "index.html";
        }

    }

}

const popup = new Notification();

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

