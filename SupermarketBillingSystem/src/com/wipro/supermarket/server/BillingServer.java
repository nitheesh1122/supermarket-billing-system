package com.wipro.supermarket.server;

import com.wipro.supermarket.bean.ItemBean;
import com.wipro.supermarket.dao.InventoryDAO;
import com.google.gson.Gson;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.*;
import java.util.List;

public class BillingServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/generateBill", new BillingHandler());
        server.setExecutor(null);
        System.out.println("Server started at http://localhost:8080");
        server.start();
    }

    static class BillingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // CORS headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            // Handle preflight request
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            // Read JSON body
            BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            String body = sb.toString();

            Gson gson = new Gson();
            BillRequest request = gson.fromJson(body, BillRequest.class);

            InventoryDAO dao = new InventoryDAO();
            String response;

            try {
                double grandTotal = 0;

                for (BillItem billItem : request.items) {
                    ItemBean item = dao.getItemById(billItem.itemId);
                    if (item == null) {
                        response = "{\"error\":\"Item " + billItem.itemId + " not found\"}";
                        sendResponse(exchange, response);
                        return;
                    }
                    if (billItem.quantity > item.getStock()) {
                        response = "{\"error\":\"Requested quantity not available for item " + billItem.itemId + "\"}";
                        sendResponse(exchange, response);
                        return;
                    }

                    double subtotal = item.getUnitPrice() * billItem.quantity;
                    double gst = subtotal * 0.18;
                    double total = subtotal + gst;

                    // Update stock and log sale
                    dao.updateStock(item.getItemId(), billItem.quantity);
                    dao.logSale(item.getItemId(), billItem.quantity, subtotal, gst, total);

                    // Fill bill details
                    billItem.itemName = item.getItemName();
                    billItem.subtotal = subtotal;
                    billItem.gst = gst;
                    billItem.total = total;
                    grandTotal += total;
                }

                response = gson.toJson(new BillResponseMulti(request.items, grandTotal));

            } catch (Exception e) {
                response = "{\"error\":\"Server error: " + e.toString() + "\"}";
            }

            sendResponse(exchange, response);
        }

        private void sendResponse(HttpExchange exchange, String response) throws IOException {
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            byte[] respBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, respBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(respBytes);
            os.close();
        }
    }

    // Request containing multiple items
    static class BillRequest {
        public List<BillItem> items;
    }

    static class BillItem {
        public String itemId;
        public int quantity;

        // Will be filled by server
        public String itemName;
        public double subtotal;
        public double gst;
        public double total;
    }

    // Response containing all items and grand total
    static class BillResponseMulti {
        public List<BillItem> items;
        public double grandTotal;

        BillResponseMulti(List<BillItem> items, double grandTotal) {
            this.items = items;
            this.grandTotal = grandTotal;
        }
    }
}
