
"use strict";

document.addEventListener("DOMContentLoaded", function () {

    const registerForm = document.getElementById("register-form");
    const message = document.getElementById("message");

    const nameInput = document.getElementById("name");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput =
        document.getElementById("confirmPassword");

    const submitButton = registerForm.querySelector(
        'button[type="submit"]'
    );

    let submitted = false;

    // Show a message
    function showMessage(text, color = "red") {
        message.textContent = text;
        message.style.color = color;
    }

    function clearMessage() {
        message.textContent = "";
    }

    // Show / Hide Password
    document.querySelectorAll(".eye-button").forEach(function (button) {

        button.addEventListener("click", function () {

            const input = document.getElementById(
                button.getAttribute("data-target")
            );

            if (!input) return;

            const shouldShow = input.type === "password";

            input.type = shouldShow ? "text" : "password";

            button.setAttribute(
                "aria-label",
                shouldShow ? "Hide password" : "Show password"
            );

            button.setAttribute(
                "aria-pressed",
                String(shouldShow)
            );
        });

    });

    // Password validation
    function validatePassword(password) {

        if (password.length === 0) {
            return "Please create a password.";
        }

        if (password.length < 8) {
            return "Password must be at least 8 characters long.";
        }

        if (password.length > 20) {
            return "Password cannot be longer than 20 characters.";
        }

        if (/\s/.test(password)) {
            return "Password must not contain spaces.";
        }

        if (!/[A-Z]/.test(password)) {
            return "Add at least one capital letter, such as A or B.";
        }

        if (!/[a-z]/.test(password)) {
            return "Add at least one small letter, such as a or b.";
        }

        if (!/[0-9]/.test(password)) {
            return "Add at least one number, such as 1 or 2.";
        }

        if (!/[^A-Za-z0-9\s]/.test(password)) {
            return "Add at least one special character, such as @ or #.";
        }

        return "";
    }

    // Email validation
    function validateEmail(email) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }

    // Live password validation while typing
    passwordInput.addEventListener("input", function () {

        submitted = false;

        const error = validatePassword(passwordInput.value);

        if (error) {
            showMessage(error);
            return;
        }

        if (
            confirmPasswordInput.value &&
            passwordInput.value !== confirmPasswordInput.value
        ) {
            showMessage("Both passwords must match.");
            return;
        }

        showMessage("Password looks good!", "#16834a");
    });

    // Live confirm-password validation
    confirmPasswordInput.addEventListener("input", function () {

        submitted = false;

        if (!confirmPasswordInput.value) {
            clearMessage();
            return;
        }

        if (passwordInput.value !== confirmPasswordInput.value) {
            showMessage("Both passwords must match.");
        } else {
            showMessage("Passwords match!", "#16834a");
        }
    });

    // Clear stale messages when other fields change
    nameInput.addEventListener("input", clearMessage);
    emailInput.addEventListener("input", clearMessage);

    // Registration form
    registerForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        if (submitted) return;

        const name = nameInput.value.trim();
        const email = emailInput.value.trim();
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        if (!name) {
            showMessage("Please enter your name.");
            nameInput.focus();
            return;
        }

        if (!email) {
            showMessage("Please enter your email address.");
            emailInput.focus();
            return;
        }

        if (!validateEmail(email)) {
            showMessage(
                "Please enter a valid email address, such as name@gmail.com."
            );
            emailInput.focus();
            return;
        }

        const passwordError = validatePassword(password);

        if (passwordError) {
            showMessage(passwordError);
            passwordInput.focus();
            return;
        }

        if (!confirmPassword) {
            showMessage("Please confirm your password.");
            confirmPasswordInput.focus();
            return;
        }

        if (password !== confirmPassword) {
            showMessage("Both passwords must match.");
            confirmPasswordInput.focus();
            return;
        }

        submitted = true;
        submitButton.disabled = true;

        showMessage("Creating your account...", "#4169e1");

        try {

            const response = await fetch(
                "http://localhost:8080/api/auth/register",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        name: name,
                        email: email,
                        password: password
                    })
                }
            );

            const data = await response.json().catch(function () {
                return {};
            });

            if (!response.ok) {

                showMessage(
                    data.message ||
                    data.error ||
                    (response.status === 409
                        ? "This email is already registered. Please log in."
                        : "Registration failed. Please check your details.")
                );

                submitted = false;
                return;
            }

            showMessage(
                "Account created successfully! Redirecting to login...",
                "#16834a"
            );

            registerForm.reset();

            setTimeout(function () {
                window.location.href = "login.html";
            }, 1200);

        } catch (error) {

            console.error("Registration error:", error);

            showMessage(
                "Could not connect to the server. Please try again."
            );

            submitted = false;

        } finally {

            submitButton.disabled = false;

        }

    });

});
