async function addReviews() {
//    alert();

//    const userName = document.getElementById("userName").value;
//    const userEmail = document.getElementById("userEmail").value;
    const userMessage = document.getElementById("userMessage").value;
    const rateValue = document.getElementById("select-rate").value;

    const searchParams = new URLSearchParams(window.location.search);
    const productSid = searchParams.get("sid");

    const data = {
//        userName: userName,
//        userEmail: userEmail,
        userMessage: userMessage,
        stockId: productSid,
        rateValue: rateValue
    };

    const dataJson = JSON.stringify(data);

    const response = await fetch(
            "AddReviews",
            {
                method: "POST",
                body: dataJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {

        const json = await response.json();

        if (json.status) {

            loadReviews();
            document.getElementById("userMessage").value = "";
            document.getElementById("select-rate").value = 0;
            popup.success({
                message: json.message
            });


        } else {

            if (json.message === "1") {
                window.location = "login-register.html";
            } else {
                popup.error({
                    message: json.message
                });
            }

        }

    } else {
        popup.error({
            message: "somthing went wrong"
        });
    }

}

async function loadReviews() {
    const searchParams = new URLSearchParams(window.location.search);
    const productSid = searchParams.get("sid");
    const response = await fetch("AddReviews?sid=" + productSid);
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const rv_container = document.getElementById("product-reviews");
            rv_container.innerHTML = "";
            json.reviewList.forEach(rv => {
                // Generate stars based on rating
                const stars = generateStars(rv.ratings);
                
                let rv_data = `<div class="sin-rattings">
                                    <div class="star-author-all">
                                        <div class="ratting-star f-left">
                                            ${stars}
                                            <span>${rv.ratings}</span>
                                        </div>
                                        <div class="ratting-author f-right">
                                            <h3>${rv.user.first_name + " " + rv.user.last_name}</h3>
                                            <span>${rv.review_date}</span>
                                        </div>
                                    </div>
                                    <p>${rv.message}</p>
                                </div>`;
                rv_container.innerHTML += rv_data;
            });
        } else {
            if (json.message === "1") {
//                popup.error({
//                    message: "Cant'found any reviews"
//                });
            } else {
                popup.error({
                    message: json.message
                });
            }
        }
    } else {
        popup.error({
            message: "something went wrong"
        });
    }
}

// Function to generate stars based on rating
function generateStars(rating) {
    let stars = "";
    const totalStars = 5;
    const filledStars = Math.floor(rating); // Full stars
    const hasHalfStar = rating % 1 !== 0; // Check if there's a decimal part for half star
    
    // Add filled stars
    for (let i = 0; i < filledStars; i++) {
        stars += '<i class="ion-star star-filled"></i>';
    }
    
    // Add half star if needed
    if (hasHalfStar) {
        stars += '<i class="ion-star-half star-half"></i>';
    }
    
    // Add empty stars for the remaining
    const emptyStars = totalStars - filledStars - (hasHalfStar ? 1 : 0);
    for (let i = 0; i < emptyStars; i++) {
        stars += '<i class="ion-star star-empty"></i>';
    }
    
    return stars;
}

// Alternative function if you want simpler implementation without half stars
function generateStarsSimple(rating) {
    let stars = "";
    const totalStars = 5;
    const filledStars = Math.round(rating); // Round to nearest whole number
    
    // Add filled stars
    for (let i = 0; i < filledStars; i++) {
        stars += '<i class="ion-star star-filled"></i>';
    }
    
    // Add empty stars
    for (let i = filledStars; i < totalStars; i++) {
        stars += '<i class="ion-star star-empty"></i>';
    }
    
    return stars;
}