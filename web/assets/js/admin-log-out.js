async function adminlogOut() {
    const response = await fetch("AdminLogOut");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            window.location = "admin-login.html";
        } else {
            window.location.reload();
        }
    } else {
        console.log("Logout Failed!");
    }
}