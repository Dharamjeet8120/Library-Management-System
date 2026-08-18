package com.library.dao;

import com.library.config.DatabaseConfig;
import com.library.model.Book;
import com.library.model.Member;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibraryDAO {

	// --- BOOK OPERATIONS ---

	public boolean insertBook(Book book) throws SQLException {
		String sql = "INSERT INTO books (title, author, available_copies) VALUES (?, ?, ?)";
		try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, book.getTitle());
			pstmt.setString(2, book.getAuthor());
			pstmt.setInt(3, book.getAvailableCopies());
			return pstmt.executeUpdate() > 0;
		}
	}

	public List<Book> findBooks(String keyword) throws SQLException {
		List<Book> books = new ArrayList<>();
		String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
		try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			String searchPattern = "%" + keyword + "%";
			pstmt.setString(1, searchPattern);
			pstmt.setString(2, searchPattern);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					books.add(new Book(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
							rs.getInt("available_copies")));
				}
			}
		}
		return books;
	}

	public Book findBookById(int bookId) throws SQLException {
		String sql = "SELECT * FROM books WHERE book_id = ?";
		try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, bookId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return new Book(rs.getInt("book_id"), rs.getString("title"), rs.getString("author"),
							rs.getInt("available_copies"));
				}
			}
		}
		return null;
	}

	// --- MEMBER OPERATIONS ---

	public boolean insertMember(Member member) throws SQLException {
		String sql = "INSERT INTO members (name, email) VALUES (?, ?)";
		try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, member.getName());
			pstmt.setString(2, member.getEmail());
			return pstmt.executeUpdate() > 0;
		}
	}

	public Member findMemberById(int memberId) throws SQLException {
		String sql = "SELECT * FROM members WHERE member_id = ?";
		try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return new Member(rs.getInt("member_id"), rs.getString("name"), rs.getString("email"));
				}
			}
		}
		return null;
	}

	// --- TRANSACTION OPERATIONS ---

	public boolean issueBookTransaction(int bookId, int memberId) throws SQLException {
		String updateBookSql = "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ?";
		String insertTxSql = "INSERT INTO transactions (book_id, member_id, issue_date) VALUES (?, ?, ?)";

		Connection conn = null;
		try {
			conn = DatabaseConfig.getConnection();
			conn.setAutoCommit(false); // Enable manual transaction control

			try (PreparedStatement updateStmt = conn.prepareStatement(updateBookSql);
					PreparedStatement txStmt = conn.prepareStatement(insertTxSql)) {

				// 1. Decrement inventory
				updateStmt.setInt(1, bookId);
				updateStmt.executeUpdate();

				// 2. Insert transaction
				txStmt.setInt(1, bookId);
				txStmt.setInt(2, memberId);
				txStmt.setDate(3, Date.valueOf(LocalDate.now()));
				txStmt.executeUpdate();

				conn.commit(); // Commit unit of work
				return true;
			}
		} catch (SQLException e) {
			if (conn != null) {
				conn.rollback(); // Rollback on failure
			}
			throw e;
		} finally {
			if (conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		}
	}

	public LocalDate getIssueDate(int transactionId) throws SQLException {
		String sql = "SELECT issue_date FROM transactions WHERE transaction_id = ? AND return_date IS NULL";
		try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, transactionId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getDate("issue_date").toLocalDate();
				}
			}
		}
		return null;
	}

	public boolean returnBookTransaction(int transactionId, int bookId, double fineAmount) throws SQLException {
		String updateTxSql = "UPDATE transactions SET return_date = ?, fine_amount = ? WHERE transaction_id = ?";
		String updateBookSql = "UPDATE books SET available_copies = available_copies + 1 WHERE book_id = ?";

		Connection conn = null;
		try {
			conn = DatabaseConfig.getConnection();
			conn.setAutoCommit(false);

			try (PreparedStatement txStmt = conn.prepareStatement(updateTxSql);
					PreparedStatement bookStmt = conn.prepareStatement(updateBookSql)) {

				// 1. Update return record
				txStmt.setDate(1, Date.valueOf(LocalDate.now()));
				txStmt.setDouble(2, fineAmount);
				txStmt.setInt(3, transactionId);
				txStmt.executeUpdate();

				// 2. Restock copy count
				bookStmt.setInt(1, bookId);
				bookStmt.executeUpdate();

				conn.commit();
				return true;
			}
		} catch (SQLException e) {
			if (conn != null) {
				conn.rollback();
			}
			throw e;
		} finally {
			if (conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		}
	}
}