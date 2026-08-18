package com.library.model;

import java.sql.Timestamp;

public class AuditLog {
	private int id;
	private String action;
	private Timestamp performedAt;

	public AuditLog(int id, String action, Timestamp performedAt) {
		this.id = id;
		this.action = action;
		this.performedAt = performedAt;
	}

	public int getId() {
		return id;
	}

	public String getAction() {
		return action;
	}

	public Timestamp getPerformedAt() {
		return performedAt;
	}

	@Override
	public String toString() {
		return String.format("[%s] Action: %s", performedAt, action);
	}
}