package com.library.service;

import com.library.dao.CategoryAndFineDAO;
import com.library.exception.LibraryException;

import java.sql.SQLException;

public class FineAndCategoryService {
	private final CategoryAndFineDAO dao = new CategoryAndFineDAO();
	private static final double MAX_ALLOWED_PENDING_FINE = 10.0;

	public void processFinePayment(int transactionId) {
		if (transactionId <= 0) {
			throw new LibraryException("Invalid transaction ID.");
		}
		try {
			boolean paid = dao.payFine(transactionId);
			if (!paid) {
				throw new LibraryException("Payment failed or no pending fine for transaction ID: " + transactionId);
			}
		} catch (SQLException e) {
			throw new LibraryException("Database error during fine payment: " + e.getMessage(), e);
		}
	}

	public void validateMemberEligibilityForBorrowing(int memberId) {
		try {
			double pendingFine = dao.getPendingFinesByMember(memberId);
			if (pendingFine > MAX_ALLOWED_PENDING_FINE) {
				throw new LibraryException("Borrowing blocked. Outstanding fine of $" + pendingFine
						+ " exceeds limit of $" + MAX_ALLOWED_PENDING_FINE);
			}
		} catch (SQLException e) {
			throw new LibraryException("Error verifying member fine status: " + e.getMessage(), e);
		}
	}
}