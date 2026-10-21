
"use strict";

// Get HTML elements
const loginForm = document.getElementById("login-form");
const message = document.getElementById("message");

// =========================
// 1. SHOW / HIDE PASSWORD
// =========================

document.querySelectorAll(".eye-button").forEach(function (button) {

    button.addEventListener("click", function () {

        const targetId = button.getAttribute("data-target");
        const passwordInput = document.getElementById(targetId);

        if (!passwordInput) {
            console.error("Password input not found:", targetId);
            return;
        }

        const shouldShow = passwordInput.type === "password";

        passwordInput.type = shouldShow ? "text" : "password";

        button.setAttribute(
            "aria-label",
            shouldShow ? "Hide password" : "Show password"
        );

        button.setAttribute("aria-pressed", String(shouldShow));
    });

});

// =========================
// 2. DISPLAY MESSAGE
// =========================

function showMessage(text, color) {
    message.textContent = text;
    message.style.color = color;
}

// =========================
// 3. LOGIN FORM
// =========================

loginForm.addEventListener("submit", async function (event) {

    event.preventDefault();

    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");

    if (!emailInput || !passwordInput) {
        console.error("Email or password input not found.");
        return;
    }

    const email = emailInput.value.trim();
    const password = passwordInput.value;

    // Validate empty fields
    if (!email || !password) {
        showMessage("Please enter your email and password.", "red");
        return;
    }

    // Basic email format check
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailPattern.test(email)) {
        showMessage("Please enter a valid email address.", "red");
        return;
    }

    showMessage("Logging in...", "#4169e1");

    const submitButton = loginForm.querySelector(
        'button[type="submit"], input[type="submit"]'
    );

    if (submitButton) {
        submitButton.disabled = true;
    }

    try {

        // Send login request to Spring Boot
        const response = await fetch(
            "http://localhost:8080/api/auth/login",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email: email,
                    password: password
                })
            }
        );

        // Read response safely
        const data = await response.json().catch(function () {
            return {};
        });

        // =========================
        // 4. HANDLE LOGIN FAILURE
        // =========================

        if (!response.ok) {

            if (response.status === 401) {
                showMessage("Invalid email or password.", "red");
            } else if (response.status === 403) {
                showMessage(
                    "Login request was denied. Please check backend security.",
                    "red"
                );
            } else {
                showMessage(
                    data.message || "Login failed. Please try again.",
                    "red"
                );
            }

            return;
        }

        // =========================
        // 5. GET JWT TOKEN
        // =========================

        const token = data.token || data.accessToken;

        if (!token || typeof token !== "string") {

            console.error("Login API response:", data);

            showMessage(
                "Login response did not contain an access token.",
                "red"
            );

            return;
        }

        // =========================
        // 6. SAVE JWT TOKEN
        // =========================

        localStorage.setItem("campusfind_token", token);

        showMessage("Login successful! Redirecting...", "#008060");

        // =========================
        // 7. REDIRECT TO HOMEPAGE
        // =========================

        window.setTimeout(function () {
            window.location.href = "index.html";
        }, 500);

    } catch (error) {

        console.error("Login error:", error);

        showMessage(
            "Could not connect to the server. Please check that the backend is running.",
            "red"
        );

    } finally {

        if (submitButton) {
            submitButton.disabled = false;
        }

    }

});
