Feature: Library Management

  Background:
    Given The database is clean
    
  Scenario: Insert books and members into the database
    Given The database contains the following books:
      | ID | Serial Number | Name        | Author   | Genre      |
      | 1  | SN123         | Test Book 1 | Author 1 | Fiction    |
      | 2  | SN124         | Test Book 2 | Author 2 | Non-Fiction|
    And The database contains the following members:
      | ID | Name     | Email             |
      | 1  | Owais    | owais@gmail.com    |
      | 2  | Zeeshan  | zeeshan@gmail.com  |
    
  Scenario: Add a new book to MongoDB
    Given The database is clean
    And The application is launched
    When The user enters book details with id "3", serial number "SN125", name "Test Book 3", author "Author 3", and genre "Fiction 3"
    And The user clicks the "addBookButton" button
    Then The book status should show "Book added successfully"
    And The book table should contain a book with id "3", serial number "SN125", name "Test Book 3", author "Author 3", and genre "Fiction 3"
    
  Scenario: Add a new member to MongoDB
    Given The database is clean
    Given The database contains the following books:
      | ID | Serial Number | Name        | Author   | Genre  |
      | 1  | SN123         | Test Book 1 | Author 1 | Fiction|
    Given The application is launched
    When The user enters member details with id "3", name "New Member", email "new@gmail.com"
    And The user selects book "Test Book 1" from dropdown
    And The user clicks the "addMemberButton" button
    Then The member status should show "Member added successfully"
    And The member table should contain a member with id "3", name "New Member", email "new@gmail.com", and book "Test Book 1"
    