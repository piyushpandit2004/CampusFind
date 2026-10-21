
"use strict";

document.addEventListener("DOMContentLoaded", function () {

    const reportForm = document.getElementById("report-form");
    const message = document.getElementById("report-message");
    const submitButton = document.getElementById("report-submit");

    // 1. Check login token
    const token = localStorage.getItem("campusfind_token");

    if (!token) {
        window.location.replace("login.html");
        return;
    }

    // 2. Handle report form submission
    reportForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        const itemName = document.getElementById("itemName").value.trim();
        const description = document.getElementById("description").value.trim();
        const location = document.getElementById("location").value.trim();
        const lostDate = document.getElementById("lostDate").value;
        const lostTime = document.getElementById("lostTime").value;
        const status = document.getElementById("status").value;

        // Validate required fields
        if (!itemName || !description || !location) {
            message.textContent =
                "Please enter the item name, description, and location.";
            message.style.color = "red";
            return;
        }

        message.textContent = "Submitting report...";
        message.style.color = "#4169e1";
        submitButton.disabled = true;

        const requestBody = {
            itemName: itemName,
            description: description,
            location: location,
            status: status,
            lostDate: lostDate || null,
            lostTime: lostTime || null
        };

        try {

            const response = await fetch(
                "http://localhost:8080/api/lost-items",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": "Bearer " + token
                    },
                    body: JSON.stringify(requestBody)
                }
            );

            const data = await response.json().catch(() => ({}));

            // Handle expired or invalid session
            if (response.status === 401) {

                localStorage.removeItem("campusfind_token");

                message.textContent =
                    "Your session has expired. Please log in again.";

                message.style.color = "red";

                setTimeout(function () {
                    window.location.href = "login.html";
                }, 1200);

                return;
            }

            // Handle access denied
            if (response.status === 403) {

                message.textContent =
                    "You do not have permission to submit this report.";

                message.style.color = "red";
                return;
            }

            // Handle other errors
            if (!response.ok) {

                message.textContent =
                    data.message ||
                    "Could not submit the report. Please try again.";

                message.style.color = "red";

                console.error(
                    "Report API error:",
                    response.status,
                    data
                );

                return;
            }

            // Report submitted successfully
            message.textContent =
                "Item reported successfully!";

            message.style.color = "#008060";

            reportForm.reset();

        } catch (error) {

            console.error("Report error:", error);

            message.textContent =
                "Could not connect to the server. Please try again later.";

            message.style.color = "red";

        } finally {

            submitButton.disabled = false;

        }

    });

});
