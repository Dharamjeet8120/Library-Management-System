package com.library.dao;

import com.library.config.DatabaseConfig;
import com.library.model.AuditLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditDAO {

	public void logAction(String actionDescription) throws SQLException {
		String sql = "INSERT INTO audit_logs (action) VALUES (?)";
		try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, actionDescription);
			pstmt.executeUpdate();
		}
	}

	public List<AuditLog> getRecentLogs(int limit) throws SQLException {
		List<AuditLog> logs = new ArrayList<>();
		String sql = "SELECT * FROM audit_logs ORDER BY performed_at DESC LIMIT ?";
		try (Connection conn = DatabaseConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, limit);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					logs.add(
							new AuditLog(rs.getInt("log_id"), rs.getString("action"), rs.getTimestamp("performed_at")));
				}
			}
		}
		return logs;
	}
}