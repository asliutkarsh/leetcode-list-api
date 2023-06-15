# Leetcode List Backend Server

The Leetcode List Backend Server is responsible for handling API requests and managing the data for the Leetcode List web application. It is built using Java Spring Boot, providing a robust and scalable server-side solution.

## Features

- Handle API requests for user authentication, problem tracking, and leaderboard functionality
- Manage user data, including points, completed problems, and progress tracking
- Provide secure endpoints for interacting with the Leetcode List frontend application

## Installation and Setup

To run the Leetcode List Backend Server locally, follow these steps:

1. Clone this repository to your local machine.
2. Make sure you have Java Development Kit (JDK) installed. You can download it from the Oracle website or use OpenJDK.
3. Install the required dependencies by running the following command:
    ```mvn clean install```
4. Start the server by running the following command:
    ```mvn spring-boot:run```
5. The server will now be running at `http://localhost:8080`. You can configure the port in the `application.yaml` file if needed.

## Technologies Used

- Java Spring Boot: Backend framework for building robust and scalable applications
- Maven: Build and dependency management tool for Java projects
- Spring Data JPA: Simplify database operations and integration
- Spring Security: Provide authentication and authorization capabilities
- MySQL : Relational database management system for storing user data

## Contributing

Contributions are welcome! If you have any ideas for new features, bug fixes, or improvements, please submit a pull request. Ensure that your code follows the project's coding conventions and is thoroughly tested.

## License

This project is licensed under the [MIT License](LICENSE).
