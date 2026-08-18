package com.library;

import com.library.config.DatabaseConfig;
import com.library.exception.LibraryException;
import com.library.model.AuditLog;
import com.library.model.Book;

import com.library.service.AuditService;
import com.library.service.FineAndCategoryService;
import com.library.service.LibraryService;

import java.util.List;
import java.util.Scanner;

public class Main {

	private static final LibraryService libraryService = new LibraryService();
	private static final FineAndCategoryService fineCategoryService = new FineAndCategoryService();
	private static final AuditService auditService = new AuditService();

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.println("==================================================");
		System.out.println("   WELCOME TO LIBRARY MANAGEMENT SYSTEM (JDBC/HikariCP)");
		System.out.println("==================================================");

		boolean running = true;
		while (running) {
			printMenu();
			System.out.print("Select an option (1-9): ");
			String input = scanner.nextLine().trim();

			System.out.println();
			try {
				switch (input) {
				case "1":
					handleAddBook(scanner);
					break;
				case "2":
					handleRegisterMember(scanner);
					break;
				case "3":
					handleSearchBooks(scanner);
					break;
				case "4":
					handleIssueBook(scanner);
					break;
				case "5":
					handleReturnBook(scanner);
					break;
				case "6":
					handlePayFine(scanner);
					break;
				case "7":
					handleCheckPendingFines(scanner);
					break;
				case "8":
					handleViewAuditLogs();
					break;
				case "9":
					running = false;
					System.out.println("Shutting down application and closing HikariCP connection pool...");
					DatabaseConfig.closePool();
					System.out.println("Goodbye!");
					break;
				default:
					System.out.println("Invalid choice. Please select between 1 and 9.");
				}
			} catch (LibraryException e) {
				System.err.println("Business Rule Error: " + e.getMessage());
			} catch (Exception e) {
				System.err.println("Unexpected Error: " + e.getMessage());
			}
			System.out.println();
		}

		scanner.close();
	}

	private static void printMenu() {
		System.out.println("--------------------------------------------------");
		System.out.println("1. Add New Book");
		System.out.println("2. Register New Member");
		System.out.println("3. Search Books");
		System.out.println("4. Issue Book to Member");
		System.out.println("5. Return Book");
		System.out.println("6. Pay Overdue Fine");
		System.out.println("7. Check Member Outstanding Fines");
		System.out.println("8. View Recent Audit Logs");
		System.out.println("9. Exit");
		System.out.println("--------------------------------------------------");
	}

	private static void handleAddBook(Scanner scanner) {
		System.out.print("Enter Book Title: ");
		String title = scanner.nextLine();
		System.out.print("Enter Author Name: ");
		String author = scanner.nextLine();
		System.out.print("Enter Initial Available Copies: ");
		int copies = Integer.parseInt(scanner.nextLine().trim());

		libraryService.addBook(title, author, copies);
		auditService.log("Added new book: '" + title + "' by " + author + " (Copies: " + copies + ")");
		System.out.println("Book successfully added to catalog!");
	}

	private static void handleRegisterMember(Scanner scanner) {
		System.out.print("Enter Member Full Name: ");
		String name = scanner.nextLine();
		System.out.print("Enter Member Email Address: ");
		String email = scanner.nextLine();

		libraryService.registerMember(name, email);
		auditService.log("Registered new member: " + name + " (" + email + ")");
		System.out.println("Member successfully registered!");
	}

	private static void handleSearchBooks(Scanner scanner) {
		System.out.print("Enter Title or Author Keyword: ");
		String keyword = scanner.nextLine();

		List<Book> books = libraryService.searchBooks(keyword);
		if (books.isEmpty()) {
			System.out.println("No matching books found.");
		} else {
			System.out.println("Found " + books.size() + " book(s):");
			for (Book b : books) {
				System.out.println("  -> " + b);
			}
		}
	}

	private static void handleIssueBook(Scanner scanner) {
		System.out.print("Enter Member ID: ");
		int memberId = Integer.parseInt(scanner.nextLine().trim());

		// Validate fine eligibility before proceeding
		fineCategoryService.validateMemberEligibilityForBorrowing(memberId);

		System.out.print("Enter Book ID to Issue: ");
		int bookId = Integer.parseInt(scanner.nextLine().trim());

		libraryService.issueBook(bookId, memberId);
		auditService.log("Issued Book ID: " + bookId + " to Member ID: " + memberId);
		System.out.println("Book issued successfully!");
	}

	private static void handleReturnBook(Scanner scanner) {
		System.out.print("Enter Active Transaction ID: ");
		int transactionId = Integer.parseInt(scanner.nextLine().trim());
		System.out.print("Enter Book ID being returned: ");
		int bookId = Integer.parseInt(scanner.nextLine().trim());

		double fineAmount = libraryService.returnBook(transactionId, bookId);
		auditService.log(
				"Processed return for Transaction ID: " + transactionId + " (Fine Calculated: $" + fineAmount + ")");

		if (fineAmount > 0) {
			System.out.printf("Book returned successfully. Overdue fine assessed: $%.2f%n", fineAmount);
		} else {
			System.out.println("Book returned on time with $0.00 fine.");
		}
	}

	private static void handlePayFine(Scanner scanner) {
		System.out.print("Enter Transaction ID to Pay Fine: ");
		int transactionId = Integer.parseInt(scanner.nextLine().trim());

		fineCategoryService.processFinePayment(transactionId);
		auditService.log("Paid fine for Transaction ID: " + transactionId);
		System.out.println("Fine status updated to PAID successfully!");
	}

	private static void handleCheckPendingFines(Scanner scanner) {
		System.out.print("Enter Member ID: ");
		int memberId = Integer.parseInt(scanner.nextLine().trim());

		// Exception will be thrown if fine exceeds limit
		fineCategoryService.validateMemberEligibilityForBorrowing(memberId);
		System.out.println("Member ID " + memberId + " is in good standing and eligible to borrow books.");
	}

	private static void handleViewAuditLogs() {
		List<AuditLog> logs = auditService.fetchRecentLogs(10);
		if (logs.isEmpty()) {
			System.out.println("No audit logs found.");
		} else {
			System.out.println("=== Recent System Audit Logs ===");
			for (AuditLog log : logs) {
				System.out.println("  " + log);
			}
		}
	}
}