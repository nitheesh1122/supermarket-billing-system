package com.wipro.supermarket.service;

import com.wipro.supermarket.bean.ItemBean;
import com.wipro.supermarket.dao.InventoryDAO;
import com.wipro.supermarket.util.OutOfStockException;

import java.util.Scanner;

public class BillingService {

    public String generateBill(ItemBean item) throws OutOfStockException {
        InventoryDAO dao = new InventoryDAO();
        if (!dao.isInStock(item.getItemId(), item.getQuantity())) {
            throw new OutOfStockException();
        }
        double total = item.getQuantity() * item.getUnitPrice();
        double gst = total * 0.18; // 18% GST
        total += gst;
        dao.updateStock(item.getItemId(), item.getQuantity());

        return "Final Bill for " + item.getItemName() + ": ₹" + total;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        InventoryDAO dao = new InventoryDAO();
        BillingService service = new BillingService();

        System.out.println("Enter Item ID:");
        String id = sc.nextLine();

        ItemBean item = dao.getItemById(id);
        if (item == null) {
            System.out.println("Item not found!");
            return;
        }

        System.out.println("Enter Quantity:");
        int qty = sc.nextInt();
        item.setQuantity(qty);

        try {
            String bill = service.generateBill(item);
            System.out.println(bill);
        } catch (OutOfStockException e) {
            System.out.println(e);
        }

        sc.close();
    }
}
