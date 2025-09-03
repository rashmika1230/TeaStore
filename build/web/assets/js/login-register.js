const popup = new Notification();
async function signUp() {
//    alert();

    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const password = document.getElementById("password").value;
    const mobile = document.getElementById("mobile").value;
    const email = document.getElementById("email").value;

    const user = {
        firstName: firstName,
        lastName: lastName,
        mobile: mobile,
        password: password,
        email: email
    };

//    console.log(user);

    const userJson = JSON.stringify(user);

    const response = await fetch(
            "SignUp",
            {
                method: "POST",
                body: userJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {

        const json = await response.json();

        if (json.status) {
            changeView();
        } else {
//            document.getElementById("message").innerHTML = json.message;
            popup.error({
                message: json.message
            });

        }
    } else {
//        document.getElementById("message").innerHTML = "try again later";
        popup.error({
            message: "Try Again.."
        });

    }
}

function changeView() {
    var mainbox = document.getElementById("mainBox");
    var vbox = document.getElementById("vBox");

    mainbox.classList.toggle("d-none");
    vbox.classList.toggle("d-none");
}

async function verificationProcess() {

    const verficationCode = document.getElementById("vcode").value;

//    console.log(verficationCode);

    const verification = {
        verificationCode: verficationCode
    };

    const verificationJSON = JSON.stringify(verification);

    const response = await fetch(
            "VerificationProcess",
            {
                method: "POST",
                body: verificationJSON,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            window.location = "index.html";
        } else {
            if (json.message === "1") {
                window.location = "login-register.html";
            } else {

//                document.getElementById("message").innerHTML = json.message;
                popup.error({
                    message: json.message
                });
            }

        }
    } else {
//        document.getElementById("message").innerHTML = "please try again";
        popup.error({
            message: "Try Again..."
        });

    }

}

async function signIn() {

//    console.log("ok");

    const email = document.getElementById("sEmail").value;
    const password = document.getElementById("sPassword").value;

//    console.log(email);
//    console.log(password);

    const user = {
        email: email,
        password: password
    };

    const userJson = JSON.stringify(user);

    const response = await fetch(
            "SignIn",
            {
                method: "POST",
                body: userJson,
                "Content-Type": "application/json"
            }
    );

    if (response.ok) {

        const responseJson = await response.json();

        if (responseJson.status) {

            if (responseJson.message === "1") {
                changeView();
            }else {
                window.location = "index.html";

            }

        } else {
            //document.getElementById("message").innerHTML = responseJson.message;
            popup.error({
                message: responseJson.message
            });
        }

    } else {

//        document.getElementById("message").innerHTML = "Please Try Again later";
        popup.error({
            message: "Try Again..."
        });

    }
}

async function sendLink() {

//    alert();

    const email = document.getElementById("sEmail").value;

    const response = await fetch("ForgetPassword?email=" + email);

    if (response.ok) {

        const responseJson = await response.json();

        if (responseJson.status) {

            popup.success({
                message: responseJson.message
            });

            setTimeout(function () {
                $('#forgotPasswordModal').modal('show');
            }, 3000);




        } else {
            //document.getElementById("message").innerHTML = responseJson.message;
            popup.error({
                message: responseJson.message
            });
        }

    } else {

        popup.error({
            message: "Try Again..."
        });

    }
}

async function restPassword() {

    const email = document.getElementById("sEmail").value;
    const vcode = document.getElementById("fp-vcode").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

//    console.log(newPassword);
//    console.log(confirmPassword);

    const password = {
        email: email,
        vcode: vcode,
        newPassword: newPassword,
        confirmPassword: confirmPassword
    };

    const passwordJson = JSON.stringify(password);

    const response = await fetch(
            "ForgetPassword",
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

            setTimeout(function () {
                window.location = "login-register.html";
            }, 1000);


        } else {
            document.getElementById("fp-message").innerHTML = responseJson.message;

        }

    } else {

        document.getElementById("message").innerHTML = "Can't Change the Password";

    }
}


