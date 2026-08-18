package com.library.model;

public class Book {
	private int id;
	private String title;
	private String author;
	private int availableCopies;

	public Book() {
	}

	public Book(int id, String title, String author, int availableCopies) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.availableCopies = availableCopies;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getAvailableCopies() {
		return availableCopies;
	}

	public void setAvailableCopies(int availableCopies) {
		this.availableCopies = availableCopies;
	}

	@Override
	public String toString() {
		return String.format("Book [ID=%d, Title='%s', Author='%s', AvailableCopies=%d]", id, title, author,
				availableCopies);
	}
}