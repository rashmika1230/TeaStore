const popup =  Notification();
function loadData() {
//    alert();
    loadCity();
    loadUserData();
}

function backButton(id) {

    if (id === 1) {
        document.getElementById("panel-1").click();
    } else if (id === 2) {
        document.getElementById("panel-2").click();
    } else if (id === 3) {
        document.getElementById("panel-3").click();
    } 

}

async function loadCity() {
    const response = await fetch("LoadCity");

    if (response.ok) {

        const responseJson = await response.json();
        const citySelect = document.getElementById("selectCity");

        responseJson.forEach(city => {
            let option = document.createElement("option");
            option.value = city.id;
            option.innerHTML = city.name;
            citySelect.appendChild(option);

        });
    }
}

async function loadUserData() {

    const response = await fetch("MyAccount");

    if (response.ok) {
        const responseJson = await response.json();
//        console.log(responseJson);

        document.getElementById("firstName").value = responseJson.firstName;
        document.getElementById("lastName").value = responseJson.lastName;
        document.getElementById("email").value = responseJson.email;
        document.getElementById("mobile").value = responseJson.mobile;

        if (responseJson.hasOwnProperty("addressList") && responseJson.addressList !== undefined) {

            let line;
            let city;
            let postalCode;
            let cityId;

            const addressUl = document.getElementById("addressUl");

            responseJson.addressList.forEach(address => {
                line = address.line;
                city = address.city.name;
                postalCode = address.postal_code;
                cityId = address.city.id;

                const ad = document.createElement("li");
                ad.innerHTML = line + "<br/>" +
                        city + "<br/>" +
                        postalCode

                addressUl.appendChild(ad);

            });

            document.getElementById("adressLine").value = line;
            document.getElementById("postalCode").value = postalCode;
            document.getElementById("selectCity").value = Number(cityId);


        }
    } else {
        popup.error({
            message: "somthing went wrong"
        });
    }

}

async function  saveUserData() {

    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const mobile = document.getElementById("mobile").value;
    const line = document.getElementById("adressLine").value;
    const postalCode = document.getElementById("postalCode").value;
    const cityId = document.getElementById("selectCity").value;

    const userData = {

        firstName: firstName,
        lastName: lastName,
        mobile: mobile,
        line: line,
        postalCode: postalCode,
        cityId: cityId
    };

    const userDataJson = JSON.stringify(userData);

    const response = await fetch(
            "MyAccount",
            {
                method: "PUT",
                body: userDataJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {

        const responseJson = await response.json();
        if (responseJson.status) {

            popup.success({
                message: "Profile Update Success"
            });
            
            loadUserData();


        } else {
            popup.error({
                message: responseJson.message
            });

        }

    } else {
        popup.error({
            message: "Profile Details update failed"
        });
    }

}

async function changePassword() {

    const oldPassword = document.getElementById("oldPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

//    console.log(newPassword);
//    console.log(confirmPassword);

    const password = {
        oldPassword: oldPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword
    };

    const passwordJson = JSON.stringify(password);

    const response = await fetch(
            "ChangePassword",
            {
                method: "PUT",
                body: passwordJson,
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {

        const responseJson = await response.json();
        if (responseJson.status) {

            popup.success({
                message: "password change successfully"
            });

            document.getElementById("oldPassword").value = "";
            document.getElementById("newPassword").value = "";
            document.getElementById("confirmPassword").value = "";


        } else {
            popup.error({
                message: responseJson.message
            });
        }

    } else {

        popup.error({
            message: "Pasword change Faild"
        });
    }
}

