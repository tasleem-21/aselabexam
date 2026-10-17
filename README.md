# Library Management System

A small Spring Boot 3 REST API for managing books in a library. It demonstrates a controller, service, Spring Data JPA repository, validation, exception handling, automated tests, GitHub Actions, and Docker.

## Architecture

`BookController` exposes HTTP endpoints, `BookService` contains business rules, `BookRepository` uses Spring Data JPA, and `Book` is the JPA entity. H2 stores data in memory, so all data is lost when the application stops.

## Prerequisites

- Java 21
- Maven 3.9+ (or use the Maven wrapper if you add one)
- Docker Desktop, for container commands

## Run locally

```powershell
mvn spring-boot:run
```

The API listens on `http://localhost:8080`. The H2 console is available at `http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:librarydb`, user `sa`, and an empty password.

## API

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/books` | Add a book |
| GET | `/api/books` | List all books |
| GET | `/api/books?search=java` | Search title or author |
| POST | `/api/books/{id}/borrow` | Borrow an available book |
| POST | `/api/books/{id}/return` | Return a book |

Add and manage a book:

```powershell
curl.exe -X POST http://localhost:8080/api/books `
  -H "Content-Type: application/json" `
  -d '{\"id\":1,\"title\":\"Effective Java\",\"author\":\"Joshua Bloch\"}'
curl.exe http://localhost:8080/api/books
curl.exe "http://localhost:8080/api/books?search=bloch"
curl.exe -X POST http://localhost:8080/api/books/1/borrow
curl.exe -X POST http://localhost:8080/api/books/1/return
```

Validation errors return `400`, an unknown book returns `404`, and borrowing an unavailable book or duplicating an ID returns `409`.

## Tests and build

```powershell
mvn test
mvn clean package
```

## Docker

The multi-stage image builds with Maven and runs on a Java 21 JRE:

```powershell
docker build -t library-management-system .
docker run --rm -p 8080:8080 library-management-system
```

Or use Compose:

```powershell
docker compose up --build
```

H2 is intentionally in-memory for this learning project; container restarts discard books. Use a persistent database and externalized credentials before production use.

## Git workflow

Development changes belong on `feature/lms-book-management`. Review the changes, commit them, and merge only after approval:

```powershell
git status
git diff --stat
git add .
git commit -m "Add LMS book management"
git switch main
git pull --ff-only origin main
git merge --no-ff feature/lms-book-management
git push origin main
```

If `origin` is not configured, create an empty GitHub repository and then run:

```powershell
git remote add origin https://github.com/<your-user>/<your-repository>.git
git push -u origin main
git push -u origin feature/lms-book-management
```

Do not force-push. Open a pull request from `feature/lms-book-management` to `main` if that is your team's preferred review process.

## GitHub Actions

`.github/workflows/ci.yml` runs `mvn test` on pushes to `main` and this feature branch, and on pull requests targeting `main`. Push the branch to GitHub, open the **Actions** tab, and inspect the CI workflow run. No repository secrets are needed for these tests.

## Troubleshooting

- **Java version error:** run `java -version` and select a Java 21 JDK in VS Code.
- **Port 8080 is busy:** stop the process using it or run with `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`.
- **Docker daemon error:** start Docker Desktop before `docker build` or `docker compose up`.
- **No books after restart:** this is expected because H2 uses an in-memory database.
