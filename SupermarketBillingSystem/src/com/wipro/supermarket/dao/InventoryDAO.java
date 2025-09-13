package com.wipro.supermarket.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.wipro.supermarket.bean.ItemBean;
import com.wipro.supermarket.util.DBConnection;

public class InventoryDAO {

    // Check if requested quantity is in stock
    public boolean isInStock(String itemId, int qty) {
        boolean inStock = false;
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT stock FROM Inventory WHERE itemId=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, itemId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int stock = rs.getInt("stock");
                if (stock >= qty) {
                    inStock = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inStock;
    }

    // Update stock after sale
    public String updateStock(String itemId, int qty) {
        String result = "Failed to update stock.";
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Inventory SET stock = stock - ? WHERE itemId=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, qty);
            pst.setString(2, itemId);
            int rows = pst.executeUpdate();
            if (rows > 0) result = "Stock updated successfully.";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // Get item details
    public ItemBean getItemById(String itemId) {
        ItemBean item = null;
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Inventory WHERE itemId=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, itemId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                item = new ItemBean();
                item.setItemId(rs.getString("itemId"));
                item.setItemName(rs.getString("itemName"));
                item.setUnitPrice(rs.getDouble("unitPrice"));
                item.setStock(rs.getInt("stock"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }
}
