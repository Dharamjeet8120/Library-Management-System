package com.library.service;

import com.library.dao.AuditDAO;
import com.library.exception.LibraryException;
import com.library.model.AuditLog;

import java.sql.SQLException;
import java.util.List;

public class AuditService {
	private final AuditDAO auditDAO = new AuditDAO();

	public void log(String action) {
		try {
			auditDAO.logAction(action);
		} catch (SQLException e) {
			System.err.println("Warning: Failed to create audit log: " + e.getMessage());
		}
	}

	public List<AuditLog> fetchRecentLogs(int limit) {
		try {
			return auditDAO.getRecentLogs(limit <= 0 ? 20 : limit);
		} catch (SQLException e) {
			throw new LibraryException("Failed to retrieve audit logs: " + e.getMessage(), e);
		}
	}
}