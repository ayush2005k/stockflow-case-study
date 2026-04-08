# stockflow-case-study
## 👨‍💻 Overview
This repository contains my solution for the StockFlow B2B Inventory Management System case study.  
The goal was to analyze an existing API, design a scalable database schema, and implement a low-stock alert system.

---

## 📁 Repository Structure

- `part1-code/` → Contains the corrected API code for product creation  
- `part1-explanation.docx` → Explanation of issues, impact, and fixes  

- `part2.docx` → Database design including schema, assumptions, and design decisions  

- `part3-code/` → Implementation of low stock alerts API (Spring Boot)  
  - Includes code, assumptions, edge cases, and approach explanation  

---

## ⚙️ Tech Stack

- **Backend:** Java (Spring Boot) / Python (Flask for Part 1)  
- **Database Design:** SQL (DDL)  
- **Version Control:** Git & GitHub  

---

## 🧠 Approach Summary

### 🔹 Part 1: Code Review & Debugging
- Identified issues related to input validation, data modeling, and transaction handling  
- Fixed problems by adding validation, enforcing SKU uniqueness, and improving database consistency  
- Ensured the API is more robust and production-ready  

---

### 🔹 Part 2: Database Design
- Designed a normalized schema to support:
  - Multiple warehouses per company  
  - Products across multiple warehouses  
  - Supplier relationships  
  - Inventory tracking and logging  
- Focused on scalability, flexibility, and data integrity  

---

### 🔹 Part 3: Low Stock Alerts API
- Built an endpoint to identify products running low on stock  
- Considered recent sales data to estimate demand  
- Included supplier details to make alerts actionable  
- Handled multiple warehouses and edge cases  

---

## ⚠️ Assumptions

- Low stock threshold is defined per product  
- Recent sales are considered over the last 30 days  
- A product can have multiple suppliers (one returned for simplicity)  
- Inventory is tracked per warehouse  

---

## 🚀 Notes

- The solution focuses on real-world scalability and clean design  
- Code includes inline comments for clarity  
- Each part includes reasoning and assumptions as required  

---

## 🔗 Submission

This repository link is submitted as the solution to the backend case study.

---
