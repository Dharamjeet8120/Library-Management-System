package com.library.dao;

import com.library.config.DatabaseConfig;
import com.library.model.Book;
import com.library.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryAndFineDAO {

	public boolean addCategory(String categoryName) throws SQLException {
		String sql = "INSERT INTO categories (category_name) VALUES (?)";
		try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, categoryName);
			return pstmt.executeUpdate() > 0;
		}
	}

	public List<Category> getAllCategories() throws SQLException {
		List<Category> categories = new ArrayList<>();
		String sql = "SELECT * FROM categories";
		try (Connection conn = DatabaseConfig.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				categories.add(new Category(rs.getInt("category_id"), rs.getString("category_name")));
			}
		}
		return categories;
	}

	public List<Book> getBooksByCategory(int categoryId) throws SQLException {
		List<Book> books = new ArrayList<>();
		String sql = "SELECT * FROM books WHERE category_id = ?";
		try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, categoryId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					books.add(new Book(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
							rs.getInt("available_copies")));
				}
			}
		}
		return books;
	}

	public boolean payFine(int transactionId) throws SQLException {
		String sql = "UPDATE transactions SET fine_status = 'PAID' WHERE transaction_id = ? AND fine_status = 'PENDING'";
		try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, transactionId);
			return pstmt.executeUpdate() > 0;
		}
	}

	public double getPendingFinesByMember(int memberId) throws SQLException {
		String sql = "SELECT SUM(fine_amount) FROM transactions WHERE member_id = ? AND fine_status = 'PENDING'";
		try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getDouble(1);
				}
			}
		}
		return 0.0;
	}
}