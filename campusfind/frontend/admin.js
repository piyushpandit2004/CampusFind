
const API_URL = "http://localhost:8080/api";
const token = localStorage.getItem("campusfind_token");

const tableBody = document.getElementById("itemsTableBody");
const message = document.getElementById("message");

// Login check
if (!token) {
    window.location.href = "login.html";
}

// Button styling
function styleButton(button, background) {
    button.style.background = background;
    button.style.color = "white";
    button.style.border = "none";
    button.style.padding = "8px 12px";
    button.style.borderRadius = "5px";
    button.style.cursor = "pointer";
    button.style.marginRight = "5px";
}

// Load all items from ADMIN API
async function loadItems() {
    message.textContent = "Loading items...";
    tableBody.innerHTML = "";

    try {
        const response = await fetch(`${API_URL}/admin/lost-items`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.status === 401) {
            message.textContent = "Session expired. Please login again.";
            return;
        }

        if (response.status === 403) {
            message.textContent =
                "Access denied. Your account must have ADMIN role.";
            return;
        }

        if (!response.ok) {
            throw new Error(`Failed to load items. HTTP ${response.status}`);
        }

        const items = await response.json();

        // Update dashboard counters
        document.getElementById("totalItems").textContent = items.length;

        document.getElementById("lostItems").textContent =
            items.filter(item =>
                (item.status || "").toUpperCase() === "LOST"
            ).length;

        document.getElementById("foundItems").textContent =
            items.filter(item =>
                (item.status || "").toUpperCase() === "FOUND"
            ).length;

        if (items.length === 0) {
            message.textContent = "No items reported yet.";
            return;
        }

        message.textContent = "";

        // Create table rows
        items.forEach(item => {
            const row = document.createElement("tr");

            const values = [
                item.id,
                item.itemName,
                item.location,
                item.status,
                item.lostDate
            ];

            values.forEach(value => {
                const cell = document.createElement("td");
                cell.textContent = value ?? "-";
                row.appendChild(cell);
            });

            // Actions cell
            const actionCell = document.createElement("td");

            // EDIT button
            const editBtn = document.createElement("button");
            editBtn.textContent = "Edit";
            styleButton(editBtn, "#2563eb");

            editBtn.addEventListener("click", async () => {
                const itemName = prompt("Item name:", item.itemName ?? "");
                if (itemName === null) return;

                const description = prompt(
                    "Description:", item.description ?? ""
                );
                if (description === null) return;

                const location = prompt("Location:", item.location ?? "");
                if (location === null) return;

                const status = prompt(
                    "Status (LOST or FOUND):", item.status ?? "LOST"
                );
                if (status === null) return;

                if (
                    !itemName.trim() ||
                    !description.trim() ||
                    !location.trim() ||
                    !["LOST", "FOUND"].includes(status.trim().toUpperCase())
                ) {
                    alert(
                        "Please enter valid details. Status must be LOST or FOUND."
                    );
                    return;
                }

                const updatedItem = {
                    itemName: itemName.trim(),
                    description: description.trim(),
                    location: location.trim(),
                    status: status.trim().toUpperCase(),
                    lostDate: item.lostDate,
                    lostTime: item.lostTime
                };

                try {
                    const updateResponse = await fetch(
                        `${API_URL}/admin/lost-items/${item.id}`,
                        {
                            method: "PUT",
                            headers: {
                                "Content-Type": "application/json",
                                "Authorization": `Bearer ${token}`
                            },
                            body: JSON.stringify(updatedItem)
                        }
                    );

                    if (!updateResponse.ok) {
                        const errorText = await updateResponse.text();
                        console.error("Update failed:", errorText);
                        alert(
                            `Update failed. HTTP ${updateResponse.status}. Check console.`
                        );
                        return;
                    }

                    alert("Item updated successfully!");
                    await loadItems();

                } catch (error) {
                    console.error("Update error:", error);
                    alert("Could not connect to backend.");
                }
            });

            // DELETE button
            const deleteBtn = document.createElement("button");
            deleteBtn.textContent = "Delete";
            styleButton(deleteBtn, "#dc2626");

            deleteBtn.addEventListener("click", async () => {
                const confirmed = confirm(
                    `Permanently delete "${item.itemName}" (ID: ${item.id})?`
                );

                if (!confirmed) return;

                try {
                    const deleteResponse = await fetch(
                        `${API_URL}/admin/lost-items/${item.id}`,
                        {
                            method: "DELETE",
                            headers: {
                                "Authorization": `Bearer ${token}`
                            }
                        }
                    );

                    if (!deleteResponse.ok) {
                        const errorText = await deleteResponse.text();
                        console.error("Delete failed:", errorText);
                        alert(
                            `Delete failed. HTTP ${deleteResponse.status}. Check console.`
                        );
                        return;
                    }

                    alert("Item deleted successfully!");
                    await loadItems();

                } catch (error) {
                    console.error("Delete error:", error);
                    alert("Could not connect to backend.");
                }
            });

            actionCell.appendChild(editBtn);
            actionCell.appendChild(deleteBtn);
            row.appendChild(actionCell);
            tableBody.appendChild(row);
        });

    } catch (error) {
        console.error("Loading items error:", error);
        message.textContent =
            "Could not load items. Check backend and browser console.";
    }
}

// Logout
document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.removeItem("campusfind_token");
    window.location.href = "login.html";
});

// Start dashboard
if (token) {
    loadItems();
}
