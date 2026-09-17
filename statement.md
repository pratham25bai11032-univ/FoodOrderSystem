# 📄 Problem Statement & Project Scope
**Project:** Food Order Management System  
**Course:** CSE2006 – Programming in Java  
**Student:** Pratham | VIT Bhopal University

---

## 🎯 Problem Statement

In fast-paced environments like campus cafeterias, food courts, and pop-up kiosks, managing food orders manually leads to serious operational breakdowns — missed orders, billing errors, and no transaction history. While enterprise-grade web applications with SQL databases exist to solve this, they require complex server setups, external database drivers, network configurations, and significant deployment overhead that is impractical for lightweight or academic deployment scenarios.

There is a clear need for a **portable, dependency-free, terminal-based application** that can:
- Manage user accounts securely with strict input validation
- Display restaurant menus dynamically based on real-time availability
- Handle a full order lifecycle — from item selection to invoice generation
- Maintain session-persistent order history using efficient in-memory data structures
- Enforce proper data encapsulation so no module can corrupt another's data directly

This project addresses that need by building a production-quality Food Order Management System entirely within the Java Virtual Machine (JVM), using core Java OOP principles, the Collections Framework, custom exception handling, the Singleton design pattern, and interface-driven architecture — all concepts covered under CSE2006.

---

## 🔭 Scope of the Project

This project is a **complete backend simulation** of a food ordering platform, restricted entirely to Java standard libraries (no external dependencies). The scope is divided into three core functional domains:

### 1. User Authentication Module
Handles secure user registration and login with:
- Regex-based validation to enforce proper email format (`^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[a-z]{2,}$`) and name constraints (letters only, 3+ characters)
- Base64 **encoding** to simulate password storage security for this academic project (field correctly named `passwordEncoded`, not `passwordHash` — Base64 is reversible encoding, not one-way hashing)
- Session state management — all ordering features are locked until successful authentication

### 2. Restaurant & Menu Catalogue Module
Provides:
- A pre-seeded centralised database of restaurants and menu items via `InMemoryDB` (Singleton)
- Dynamic menu filtering by restaurant ID and boolean `isAvailable` flag — unavailable items are never shown
- Clean, tabular terminal display using `printf` formatting and ANSI colour codes

### 3. Transaction & Order Processing Module
Implements the full cart-to-invoice pipeline:
- Add items to a session-based cart by item ID — throws `ItemNotAvailableException` if unavailable
- Automatic duplicate grouping via overridden `equals()` and `hashCode()` in `MenuItem`, enabling `Collectors.groupingBy()` to display correct quantities (e.g., "2x Garlic Naan Rs.80")
- `getGroupedCart()` helper method in `OrderService` — reused by both cart display and invoice generation, avoiding code duplication
- Calculate running cart total using Java Streams (`mapToDouble`)
- Confirm and place orders with `Y/N` user confirmation — cart is preserved on cancellation
- Generate time-stamped invoices using `LocalDateTime`
- Archive completed orders to the in-memory order ledger via controlled `addOrder()` mutator
- Retrieve personal order history per user ID

**Out of scope:** Persistent file storage, network communication, GUI, multi-user concurrency, and real payment processing.

---

## 👥 Target Users

| User Type | Description |
|---|---|
| **CS Students & Evaluators** | An academic benchmark demonstrating OOP design, Singleton pattern, interface-driven architecture, proper encapsulation, and the Java Collections Framework |
| **Small-Scale Kiosk Operators** | Staff requiring a zero-setup, instant-deploy digital order ledger with no internet or database dependency |
| **Java Learners** | A practical reference implementation showing how layered architecture, design patterns, and encapsulation work in a real project |

---

## ⚙️ High-Level Features

- **Secure Authentication** — Register/login with Regex-validated inputs and Base64-encoded password storage (`passwordEncoded` field)
- **Multi-Restaurant Browsing** — View catalogues and availability-filtered menus across multiple restaurants
- **Smart Cart Engine** — Session-based cart with automatic quantity tracking via overridden `equals()` and `hashCode()`, enabling `Collectors.groupingBy(Function.identity(), Collectors.counting())` to group identical items at display time
- **Checkout & Invoicing** — Aggregate cost calculation via Java Streams, `Y/N` confirmation prompt, formatted digital invoice with `LocalDateTime` timestamp
- **Order History** — Per-user transaction ledger queried in real-time from the in-memory database
- **Proper Encapsulation** — `InMemoryDB` fields are `private`; all getters return `Collections.unmodifiableList()` (read-only); writes only permitted through `addUser()` and `addOrder()` mutators
- **Fault-Tolerant Input** — `UserNotFoundException`, `ItemNotAvailableException`, and recursive Scanner buffer clearing prevent any runtime crash
- **Professional CLI** — ANSI colour codes, box-drawing characters, `printf` tabular formatting, and `LocalDateTime` timestamps
