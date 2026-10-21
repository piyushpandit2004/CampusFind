
"use strict";

document.addEventListener("DOMContentLoaded", function () {

    const container = document.getElementById("item-details-container");
    const token = localStorage.getItem("campusfind_token");

    // Read the item ID from the page URL
    const params = new URLSearchParams(window.location.search);
    const itemId = params.get("id");

    async function loadItemDetails() {

        if (!token) {
            window.location.replace("login.html");
            return;
        }

        if (!itemId || !/^\d+$/.test(itemId) || Number(itemId) < 1) {
            container.textContent = "Please select a valid item.";
            return;
        }

        container.textContent = "Loading item details...";

        try {

            const response = await fetch(
                "http://localhost:8080/api/lost-items/" +
                encodeURIComponent(itemId),
                {
                    method: "GET",
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

            if (response.status === 404) {
                container.textContent = "Item not found.";
                return;
            }

            if (!response.ok) {
                throw new Error("Request failed: " + response.status);
            }

            const item = await response.json();

            // Clear the loading message
            container.replaceChildren();

            // Item name
            const title = document.createElement("h2");
            title.className = "detail-item-title";
            title.textContent = item.itemName || "Unnamed item";

            // Description
            const description = document.createElement("p");
            description.className = "detail-description";
            description.textContent =
                item.description || "No description available.";

            // Status
            const status = document.createElement("span");
            status.className = "status-badge";
            status.textContent = item.status || "Unknown";

            // Details list
            const details = document.createElement("div");
            details.className = "detail-information";

            function addDetail(label, value) {

                const row = document.createElement("div");
                row.className = "detail-row";

                const heading = document.createElement("span");
                heading.className = "detail-label";
                heading.textContent = label;

                const text = document.createElement("span");
                text.className = "detail-value";
                text.textContent = value || "Not provided";

                row.append(heading, text);
                details.appendChild(row);
            }

            addDetail("Location", item.location);
            addDetail("Status", item.status);
            addDetail("Lost Date", item.lostDate);
            addDetail("Lost Time", item.lostTime);

            // Main item card
            const card = document.createElement("article");
            card.className = "item-detail-card";

            card.append(title, status, description, details);
            container.appendChild(card);

        } catch (error) {

            console.error("Item details error:", error);

            container.textContent =
                "Could not load item details. Please try again later.";
        }
    }

    loadItemDetails();

});
