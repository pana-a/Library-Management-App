# Library Management System

A desktop application for managing a small library, developed in **Java** using **Swing**.
The project follows a layered architecture that separates domain models, repositories, services, and the user interface.

## Features

* User authentication and registration
* Separate interfaces for **students** and **librarians**
* Book management (add, search, update, delete)
* Borrowing and returning books
* Simple statistics about library activity

## Architecture

The application is organized into several layers:

* **models** – domain entities such as `Book`, `User`, `Loan`, and `Statistic`
* **repositories** – data access layer with file-based persistence
* **services** – business logic for authentication, books, and loans
* **ui** – graphical interface implemented with Swing

## Data Storage

Application data is stored in text files:

* `books.txt`
* `users.txt`

Each book entry follows the format:

```
id;title;author;collection;year;totalCopies;availableCopies
```

## Running the Application

1. Open the project in **IntelliJ IDEA** (or any Java IDE)
2. Run the `Main` class

## Technologies

* Java
* Swing
* File-based persistence

## Author

Personal project developed for an Object-Oriented Programming course.
