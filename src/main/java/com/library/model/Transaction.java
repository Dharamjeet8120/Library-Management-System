package com.library.model;

import java.time.LocalDate;

public class Transaction {
	private int id;
	private int bookId;
	private int memberId;
	private LocalDate issueDate;
	private LocalDate returnDate;
	private double fineAmount;
	private String fineStatus; // "PENDING", "PAID", "N/A"

	public Transaction(int id, int bookId, int memberId, LocalDate issueDate, LocalDate returnDate, double fineAmount,
			String fineStatus) {
		this.id = id;
		this.bookId = bookId;
		this.memberId = memberId;
		this.issueDate = issueDate;
		this.returnDate = returnDate;
		this.fineAmount = fineAmount;
		this.fineStatus = fineStatus;
	}

	public int getId() {
		return id;
	}

	public int getBookId() {
		return bookId;
	}

	public int getMemberId() {
		return memberId;
	}

	public LocalDate getIssueDate() {
		return issueDate;
	}

	public LocalDate getReturnDate() {
		return returnDate;
	}

	public double getFineAmount() {
		return fineAmount;
	}

	public String getFineStatus() {
		return fineStatus;
	}
}