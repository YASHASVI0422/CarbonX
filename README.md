# 🌍 CarbonX – Smart Carbon Footprint Intelligence System

> 🚀 An Advanced Java Project aligned with **SDG 13: Climate Action**

---

## 🏷️ Project Badges

![Java](https://img.shields.io/badge/Java-17-orange)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-green)
![SDG13](https://img.shields.io/badge/SDG-13%20Climate%20Action-brightgreen)

---

## 📌 Overview

CarbonX is an intelligent carbon footprint tracking system that helps users calculate, monitor, and actively reduce their daily carbon emissions. It combines advanced Java technologies with real-world environmental impact to promote sustainable living and climate awareness.

---

## ❗ Problem Statement

Climate change is driven by everyday human activities like transportation, electricity use, and food consumption. However, individuals often lack awareness of their personal carbon footprint, and existing tools fail to provide real-time insights or actionable recommendations.

CarbonX addresses this gap by providing a smart, interactive system that enables users to track emissions and make informed, eco-friendly decisions.

---

## 🚀 Key Features

* 🧮 Carbon Footprint Calculator (Travel, Electricity, Food)
* 📊 Emission Analytics (Daily & Monthly Trends)
* 🔔 Real-time Alerts using Socket Programming
* 🌐 Distributed Processing using Java RMI
* 🏆 Leaderboard & Eco Score System
* 💡 Smart Recommendations for Sustainable Living
* 🎨 Interactive UI with JavaFX

---

## 🛠️ Tech Stack

* **Language:** Java (JDK 17+)
* **IDE:** Eclipse
* **Frontend:** JavaFX
* **Backend:** Core Java
* **Database:** MySQL
* **Connectivity:** JDBC
* **Networking:** Socket Programming
* **Distributed System:** Java RMI
* **Concepts:** Streams, Lambda Expressions, JavaBeans

---

## 🏗️ System Architecture

CarbonX follows a multi-layer architecture:

User Interface (JavaFX)
↓
Service Layer (Business Logic)
↓
DAO Layer (JDBC)
↓
MySQL Database

Additional Components:

* Socket Server → Real-time Alerts
* RMI Server → Distributed Carbon Calculation

---

## 📁 Project Structure

```bash
com.carbonx.main
com.carbonx.ui
com.carbonx.model
com.carbonx.dao
com.carbonx.service
com.carbonx.network
com.carbonx.rmi
com.carbonx.util
```

---

## 🗄️ Database Setup

```sql
CREATE DATABASE carbonx;
USE carbonx;
```

### Tables:

* `users`
* `carbon_data`
* `leaderboard`

---

## ⚙️ How to Run

### 1️⃣ Clone Repository

```bash
git clone https://github.com/YASHASVI0422/CarbonX.git
cd CarbonX
```

### 2️⃣ Setup Database

* Start MySQL Server
* Execute SQL scripts to create tables

### 3️⃣ Configure JDBC

Update database credentials in:

```bash
DBConnection.java
```

### 4️⃣ Run Application

* Open project in Eclipse
* Run `MainApp.java`

---

## 📊 Core Formula

```java
carbon = (travel_km * 0.21) + (electricity_units * 0.85) + food_factor;
```

---

## 🧠 Key Concepts Demonstrated

* Object-Oriented Programming (OOP)
* Java Streams & Lambda Expressions
* JDBC for Database Connectivity
* JavaFX for GUI Development
* Socket Programming (Client-Server Communication)
* Remote Method Invocation (RMI)
* MVC Architecture

---

## 👥 Contributors

* 👨‍💻 Yashasvi Pandey
* 👨‍💻 Aman Chhimwal

---

## 🌱 SDG Alignment

This project contributes to:

👉 **SDG 13: Climate Action**
By promoting awareness and enabling individuals to reduce their carbon footprint.

---

## 📌 Future Enhancements

* 🌐 Real-time API integration for emission factors
* 📱 Mobile application version
* 🤖 AI-based carbon prediction
* 🌍 City-wise emission comparison dashboard

---

## 📄 License

This project is licensed under the **MIT License**.

---

## 🎤 Viva Insight

CarbonX is not just a calculator but a distributed, real-time climate intelligence system built using Advanced Java concepts including JDBC, JavaFX, Socket Programming, and RMI.

---

## ⭐ Support

If you like this project, consider giving it a ⭐ on GitHub!
