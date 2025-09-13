package com.wipro.supermarket.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.wipro.supermarket.bean.ItemBean;
import com.wipro.supermarket.util.DBConnection;

public class InventoryDAO {

    // Check stock
    public boolean isInStock(int itemId, int qty) {  // Changed parameter type to int
        boolean inStock = false;
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT stock FROM Inventory WHERE itemId=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, itemId);  // Use setInt
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int stock = rs.getInt("stock");
                if (stock >= qty) inStock = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inStock;
    }

    // Update stock
    public String updateStock(int itemId, int qty) {  // Parameter type changed
        String result = "Failed to update stock.";
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Inventory SET stock = stock - ? WHERE itemId=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, qty);
            pst.setInt(2, itemId);  // Use setInt
            int rows = pst.executeUpdate();
            if (rows > 0) result = "Stock updated successfully.";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // Get item
    public ItemBean getItemById(int itemId) {  // Parameter type changed
        ItemBean item = null;
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Inventory WHERE itemId=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, itemId);  // Use setInt
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                item = new ItemBean();
                item.setItemId(String.valueOf(rs.getInt("itemId")));  // Keep as String for frontend
                item.setItemName(rs.getString("itemName"));
                item.setUnitPrice(rs.getDouble("unitPrice"));
                item.setStock(rs.getInt("stock"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    // Log sale
    public void logSale(int itemId, int quantity, double subtotal, double gst, double total) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO SalesHistory(itemId, quantity, subtotal, gst, total) VALUES(?,?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, itemId);  // Use setInt
            pst.setInt(2, quantity);
            pst.setDouble(3, subtotal);
            pst.setDouble(4, gst);
            pst.setDouble(5, total);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
