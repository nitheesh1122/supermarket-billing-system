const form = document.getElementById('billingForm');
const itemSelect = document.getElementById('itemSelect');
const quantityInput = document.getElementById('quantity');
const billTableBody = document.getElementById('billTableBody');
const totalDiv = document.getElementById('totalDiv');

// List of items from your DB
const items = [
    { id: 1, name: "Rice", unitPrice: 50 },
    { id: 2, name: "Oil", unitPrice: 100 },
    { id: 3, name: "Sugar", unitPrice: 40 },
    { id: 4, name: "Wheat", unitPrice: 45 },
    { id: 5, name: "Salt", unitPrice: 20 },
    { id: 6, name: "Milk", unitPrice: 30 },
    { id: 7, name: "Eggs", unitPrice: 5 },
    { id: 8, name: "Butter", unitPrice: 60 },
    { id: 9, name: "Bread", unitPrice: 35 },
    { id: 10, name: "Tea", unitPrice: 150 },
    { id: 11, name: "Coffee", unitPrice: 200 },
    { id: 12, name: "Cheese", unitPrice: 120 },
    { id: 13, name: "Yogurt", unitPrice: 25 },
    { id: 14, name: "Flour", unitPrice: 45 },
    { id: 15, name: "Maida", unitPrice: 40 },
    { id: 16, name: "Oats", unitPrice: 80 },
    { id: 17, name: "Chocolates", unitPrice: 150 },
    { id: 18, name: "Biscuits", unitPrice: 30 },
    { id: 19, name: "Juice", unitPrice: 60 },
    { id: 20, name: "Water Bottle", unitPrice: 20 },
    { id: 21, name: "Soft Drink", unitPrice: 50 },
    { id: 22, name: "Cornflakes", unitPrice: 120 },
    { id: 23, name: "Honey", unitPrice: 180 },
    { id: 24, name: "Jam", unitPrice: 90 },
    { id: 25, name: "Noodles", unitPrice: 40 },
    { id: 26, name: "Pasta", unitPrice: 80 },
    { id: 27, name: "Tomato Sauce", unitPrice: 60 },
    { id: 28, name: "Pickle", unitPrice: 50 },
    { id: 29, name: "Spices", unitPrice: 150 },
    { id: 30, name: "Cereal", unitPrice: 120 },
    { id: 31, name: "Coconut Oil", unitPrice: 200 },
    { id: 32, name: "Mustard Oil", unitPrice: 180 },
    { id: 33, name: "Vinegar", unitPrice: 90 },
    { id: 34, name: "Soy Sauce", unitPrice: 110 },
    { id: 35, name: "Mayonnaise", unitPrice: 120 },
    { id: 36, name: "Soya Chunks", unitPrice: 80 },
    { id: 37, name: "Paneer", unitPrice: 150 },
    { id: 38, name: "Frozen Veg", unitPrice: 120 },
    { id: 39, name: "Ice Cream", unitPrice: 100 },
    { id: 40, name: "Bisleri", unitPrice: 25 },
    { id: 41, name: "Detergent", unitPrice: 150 },
    { id: 42, name: "Soap", unitPrice: 40 },
    { id: 43, name: "Shampoo", unitPrice: 120 },
    { id: 44, name: "Toothpaste", unitPrice: 60 },
    { id: 45, name: "Brush", unitPrice: 50 },
    { id: 46, name: "Face Wash", unitPrice: 90 },
    { id: 47, name: "Hand Wash", unitPrice: 70 },
    { id: 48, name: "Sanitizer", unitPrice: 150 },
    { id: 49, name: "Tissues", unitPrice: 40 },
    { id: 50, name: "Paper Towels", unitPrice: 60 },
    { id: 51, name: "Cooking Spray", unitPrice: 200 },
    { id: 52, name: "Condiments", unitPrice: 100 },
    { id: 53, name: "Energy Drink", unitPrice: 150 },
    { id: 54, name: "Canned Beans", unitPrice: 80 },
    { id: 55, name: "Canned Corn", unitPrice: 90 }
];

// Populate dropdown
items.forEach(item => {
    const option = document.createElement('option');
    option.value = item.id;
    option.textContent = `${item.id} - ${item.name}`;
    itemSelect.appendChild(option);
});

// Cart array
let billItems = [];

// Submit form via Enter key
form.addEventListener('submit', function(e) {
    e.preventDefault();
    addItemToBill();
});

// Add item to bill
function addItemToBill() {
    const itemId = parseInt(itemSelect.value);
    const quantity = parseInt(quantityInput.value);

    if (!itemId || quantity <= 0) {
        alert("Enter valid Item and Quantity");
        return;
    }

    const item = items.find(i => i.id === itemId);
    if (!item) return;

    let existing = billItems.find(b => b.id === itemId);
    if (existing) {
        existing.quantity += quantity;
    } else {
        billItems.push({ ...item, quantity });
    }

    renderBillTable();
}

// Remove item from bill
function removeItem(itemId) {
    billItems = billItems.filter(b => b.id !== itemId);
    renderBillTable();
}

// Render bill table and totals
function renderBillTable() {
    billTableBody.innerHTML = "";
    let total = 0;
    let gstTotal = 0;

    if (billItems.length === 0) {
        billTableBody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-state">
                    <i class="fas fa-shopping-basket"></i>
                    <h4>No items added yet</h4>
                    <p>Start by selecting an item and adding it to your bill</p>
                </td>
            </tr>`;
        totalDiv.textContent = "";
        return;
    }

    billItems.forEach(item => {
        const subtotal = item.unitPrice * item.quantity;
        const gst = subtotal * 0.18;
        const totalItem = subtotal + gst;
        total += subtotal;
        gstTotal += gst;

        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${item.name}</td>
            <td>${item.quantity}</td>
            <td>₹${item.unitPrice.toFixed(2)}</td>
            <td>₹${gst.toFixed(2)}</td>
            <td>₹${subtotal.toFixed(2)}</td>
            <td>₹${totalItem.toFixed(2)}</td>
            <td><button onclick="removeItem(${item.id})">Remove</button></td>
        `;
        billTableBody.appendChild(row);
    });

    totalDiv.textContent = `Items Total: ₹${total.toFixed(2)} | GST Total: ₹${gstTotal.toFixed(2)} | Grand Total: ₹${(total + gstTotal).toFixed(2)}`;
}

// Generate final bill modal and send to backend
function generateBillModal() {
    if (billItems.length === 0) {
        alert("No items in bill");
        return;
    }

    let billText = "Bill Summary:\n\n";
    let total = 0;
    let gstTotal = 0;

    billItems.forEach(item => {
        const subtotal = item.unitPrice * item.quantity;
        const gst = subtotal * 0.18;
        const totalItem = subtotal + gst;
        billText += `${item.name} x ${item.quantity} = ₹${totalItem.toFixed(2)} (GST ₹${gst.toFixed(2)})\n`;
        total += subtotal;
        gstTotal += gst;
    });

    billText += `\nItems Total: ₹${total.toFixed(2)}\nGST Total: ₹${gstTotal.toFixed(2)}\nGrand Total: ₹${(total + gstTotal).toFixed(2)}`;

    if (confirm(billText + "\n\nConfirm and finalize the bill?")) {
        const payload = {
            items: billItems.map(item => ({
                itemId: item.id,
                quantity: item.quantity
            }))
        };

        fetch("http://localhost:8080/generateBill", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })
        .then(res => res.json())
        .then(data => {
            if (data.error) {
                alert("Error: " + data.error);
            } else {
                alert(`Bill Generated Successfully!\nGrand Total: ₹${data.grandTotal.toFixed(2)}`);
                billItems = [];  // Clear cart
                renderBillTable();
            }
        })
        .catch(err => {
            alert("Server error: " + err);
        });
    }
}
