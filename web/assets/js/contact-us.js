
const popup = new Notification();
async function ContactUs() {

    const fullname = document.getElementById("fullName").value;
    const email = document.getElementById("email").value;
    const subject = document.getElementById("subject").value;
    const message = document.getElementById("message").value;

    const data = {
        fullname: fullname,
        email: email,
        subject: subject,
        message: message
    };
    const jsonData = JSON.stringify(data);

    const response = await fetch(
            "ContactUs",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: jsonData
            }
    );

    if (response.ok) {

        const json = await response.json();

        if (json.status) {
            popup.success({
                message: "Email Sent Success"
            });
            document.getElementById("fullName").value = "";
            document.getElementById("email").value = "";
            document.getElementById("subject").value = "";
            document.getElementById("message").value = "";

        } else {

            popup.error({
                message: json.message
            });
        }
    } else {
        popup.error({
            message: "Somthing went wrong"
        });
    }
}