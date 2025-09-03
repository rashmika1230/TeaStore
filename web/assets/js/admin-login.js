// Toggle password visibility
function togglePassword() {
    const passwordInput = document.getElementById('password');
    const toggleIcon = document.querySelector('.toggle-password');

    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        toggleIcon.classList.remove('fa-eye');
        toggleIcon.classList.add('fa-eye-slash');
    } else {
        passwordInput.type = 'password';
        toggleIcon.classList.remove('fa-eye-slash');
        toggleIcon.classList.add('fa-eye');
    }
}
// Show alert message
function showAlert(message, type = 'danger') {
    const alertDiv = document.getElementById('loginAlert');
    const alertMessage = document.getElementById('alertMessage');

    alertMessage.textContent = message;
    alertDiv.className = `alert alert-${type} alert-custom`;
    alertDiv.classList.remove('d-none');

    setTimeout(() => {
        alertDiv.classList.add('d-none');
    }, 5000);

}

const popup = new Notification;
async function adminLogin() {

//    alert();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

//    console.log(email);
//    console.log(password);

    const adminData = {
        email: email,
        password: password
    };

    const adminLoginJson = JSON.stringify(adminData);

    const response = await fetch(
            "AdminLogin",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: adminLoginJson
            }
    );
    
    if (response.ok) {
        
        const resposnseJson = await response.json();
        
        if (resposnseJson.status) {
            
            console.log(resposnseJson);
            
            window.location = "admin.html";
        } else {
            popup.error({
                message:resposnseJson.message
            });
        }
        
    } else {
    }

}