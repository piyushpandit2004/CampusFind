
"use strict";

document.addEventListener("DOMContentLoaded", function () {

    const container = document.getElementById("my-reports-container");
    const token = localStorage.getItem("campusfind_token");

    // Load the logged-in user's reports
    async function loadMyReports() {

        if (!token) {

            container.replaceChildren();

            const message = document.createElement("p");
            message.textContent = "Please log in to view your reports.";

            const loginLink = document.createElement("a");
            loginLink.href = "login.html";
            loginLink.className = "primary-btn";
            loginLink.textContent = "Login";

            container.append(message, loginLink);
            return;
        }

        container.textContent = "Loading your reports...";

        try {

            const response = await fetch(
                "http://localhost:8080/api/lost-items/my-reports",
                {
                    headers: {
                        "Authorization": "Bearer " + token
                    }
                }
            );

            if (response.status === 401) {

                localStorage.removeItem("campusfind_token");
                window.location.replace("login.html");
                return;
            }

            if (!response.ok) {
                throw new Error(
                    "Could not load your reports. Please try again later."
                );
            }

            const items = await response.json();

            if (!Array.isArray(items)) {
                throw new Error("The server returned an unexpected response.");
            }

            container.replaceChildren();

            if (items.length === 0) {
                container.textContent =
                    "You have not reported any items yet.";
                return;
            }

            // Show the newest items first
            items.sort((a, b) =>
                (Number(b.id) || 0) - (Number(a.id) || 0)
            );

            items.forEach(function (item) {

                const card = document.createElement("article");
                card.className = "item-card";

                const title = document.createElement("h3");
                title.textContent = item.itemName || "Unnamed item";

                const description = document.createElement("p");
                description.textContent =
                    item.description || "No description available.";

                const location = document.createElement("p");
                location.textContent =
                    "Location: " + (item.location || "Not specified");

                const status = document.createElement("span");
                status.className = "status-badge";
                status.textContent = item.status || "Unknown";

                card.append(title, description, location, status);

                if (item.lostDate) {
                    const date = document.createElement("p");
                    date.textContent = "Lost date: " + item.lostDate;
                    card.appendChild(date);
                }

                if (item.lostTime) {
                    const time = document.createElement("p");
                    time.textContent = "Lost time: " + item.lostTime;
                    card.appendChild(time);
                }

                // Edit and Delete buttons
                const actions = document.createElement("div");
                actions.className = "report-actions";

                const editButton = document.createElement("button");
                editButton.type = "button";
                editButton.className = "edit-report-btn";
                editButton.textContent = "Edit";

                editButton.addEventListener("click", function () {
                    editReport(item);
                });

                const deleteButton = document.createElement("button");
                deleteButton.type = "button";
                deleteButton.className = "delete-report-btn";
                deleteButton.textContent = "Delete";

                deleteButton.addEventListener("click", function () {
                    deleteReport(item);
                });

                actions.append(editButton, deleteButton);
                card.appendChild(actions);
                container.appendChild(card);
            });

        } catch (error) {

            console.error("My Reports error:", error);

            container.textContent =
                "Could not load your reports. Please try again later.";
        }
    }

    // Edit an item
    async function editReport(item) {

        const itemName = prompt(
            "Item name:",
            item.itemName || ""
        );
        if (itemName === null) return;

        const description = prompt(
            "Description:",
            item.description || ""
        );
        if (description === null) return;

        const location = prompt(
            "Lost location:",
            item.location || ""
        );
        if (location === null) return;

        const status = prompt(
            "Enter status: LOST or FOUND",
            (item.status || "LOST").toUpperCase()
        );
        if (status === null) return;

        const cleanName = itemName.trim();
        const cleanDescription = description.trim();
        const cleanLocation = location.trim();
        const cleanStatus = status.trim().toUpperCase();

        if (!cleanName || !cleanDescription || !cleanLocation) {

            alert("Item name, description, and location are required.");
            return;
        }

        if (!["LOST", "FOUND"].includes(cleanStatus)) {

            alert("Status must be LOST or FOUND.");
            return;
        }

        const requestBody = {
            itemName: cleanName,
            description: cleanDescription,
            location: cleanLocation,
            status: cleanStatus,
            lostDate: item.lostDate || null,
            lostTime: item.lostTime || null
        };

        try {

            const response = await fetch(
                "http://localhost:8080/api/lost-items/" + item.id,
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": "Bearer " + token
                    },
                    body: JSON.stringify(requestBody)
                }
            );

            if (response.status === 401) {

                localStorage.removeItem("campusfind_token");
                window.location.replace("login.html");
                return;
            }

            if (response.status === 403) {

                alert("You do not have permission to edit this item.");
                return;
            }

            if (!response.ok) {

                throw new Error(
                    "Could not update the report. Please try again."
                );
            }

            alert("Report updated successfully!");

            await loadMyReports();

        } catch (error) {

            console.error("Edit report error:", error);

            alert(
                "Could not update the report. Please try again later."
            );
        }
    }

    // Delete an item
    async function deleteReport(item) {

        const confirmed = confirm(
            'Are you sure you want to delete "' +
            item.itemName +
            '"?'
        );

        if (!confirmed) return;

        try {

            const response = await fetch(
                "http://localhost:8080/api/lost-items/" + item.id,
                {
                    method: "DELETE",
                    headers: {
                        "Authorization": "Bearer " + token
                    }
                }
            );

            if (response.status === 401) {

                localStorage.removeItem("campusfind_token");
                window.location.replace("login.html");
                return;
            }

            if (response.status === 403) {

                alert("You do not have permission to delete this item.");
                return;
            }

            if (!response.ok) {

                throw new Error(
                    "Could not delete the report. Please try again."
                );
            }

            alert("Report deleted successfully!");

            await loadMyReports();

        } catch (error) {

            console.error("Delete report error:", error);

            alert(
                "Could not delete the report. Please try again later."
            );
        }
    }

    loadMyReports();

});
