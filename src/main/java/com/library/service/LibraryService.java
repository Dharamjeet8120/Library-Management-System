package com.library.service;

import com.library.dao.LibraryDAO;
import com.library.exception.LibraryException;
import com.library.model.Book;
import com.library.model.Member;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class LibraryService {
	private final LibraryDAO libraryDAO;
	private static final int ALLOWED_DAYS = 14;
	private static final double FINE_PER_DAY = 2.0;

	public LibraryService() {
		this.libraryDAO = new LibraryDAO();
	}

	public void addBook(String title, String author, int copies) {
		if (title == null || title.trim().isEmpty()) {
			throw new LibraryException("Title cannot be empty.");
		}
		if (author == null || author.trim().isEmpty()) {
			throw new LibraryException("Author cannot be empty.");
		}
		if (copies < 0) {
			throw new LibraryException("Copies cannot be negative.");
		}

		try {
			Book book = new Book(0, title.trim(), author.trim(), copies);
			libraryDAO.insertBook(book);
		} catch (SQLException e) {
			throw new LibraryException("Database error while adding book: " + e.getMessage(), e);
		}
	}

	public void registerMember(String name, String email) {
		if (name == null || name.trim().isEmpty()) {
			throw new LibraryException("Member name required.");
		}
		if (email == null || !email.contains("@")) {
			throw new LibraryException("Valid email required.");
		}

		try {
			Member member = new Member(0, name.trim(), email.trim());
			libraryDAO.insertMember(member);
		} catch (SQLException e) {
			throw new LibraryException("Database error while adding member: " + e.getMessage(), e);
		}
	}

	public List<Book> searchBooks(String keyword) {
		try {
			return libraryDAO.findBooks(keyword == null ? "" : keyword.trim());
		} catch (SQLException e) {
			throw new LibraryException("Error searching books: " + e.getMessage(), e);
		}
	}

	public void issueBook(int bookId, int memberId) {
		try {
			Member member = libraryDAO.findMemberById(memberId);
			if (member == null) {
				throw new LibraryException("Member not found.");
			}

			Book book = libraryDAO.findBookById(bookId);
			if (book == null) {
				throw new LibraryException("Book not found.");
			}
			if (book.getAvailableCopies() <= 0) {
				throw new LibraryException("Book is out of stock.");
			}

			libraryDAO.issueBookTransaction(bookId, memberId);
		} catch (SQLException e) {
			throw new LibraryException("Issue transaction failed: " + e.getMessage(), e);
		}
	}

	public double returnBook(int transactionId, int bookId) {
		try {
			LocalDate issueDate = libraryDAO.getIssueDate(transactionId);
			if (issueDate == null) {
				throw new LibraryException("Invalid or already completed transaction ID.");
			}

			long days = ChronoUnit.DAYS.between(issueDate, LocalDate.now());
			double fine = days > ALLOWED_DAYS ? (days - ALLOWED_DAYS) * FINE_PER_DAY : 0.0;

			libraryDAO.returnBookTransaction(transactionId, bookId, fine);
			return fine;
		} catch (SQLException e) {
			throw new LibraryException("Return transaction failed: " + e.getMessage(), e);
		}
	}
}