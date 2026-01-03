## 🏦 **FinTrust – Banking Management System**

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![ZK Framework](https://img.shields.io/badge/ZK_Framework-1572B6?style=for-the-badge&logo=zk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![Apache Tomcat](https://img.shields.io/badge/Tomcat-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

---

### 💡 **Overview**
FinTrust is a **web-based banking management system** built using **Java, ZK Framework, and MySQL**.  
It provides a secure and responsive platform for both users and administrators to handle banking operations efficiently.

---

### 🚀 **Key Features**
- 👤 User registration and login with validation  
- 🔒 Secure authentication and session handling  
- 💰 View and manage account transactions  
- 🧾 Admin module for user and account management  
- 🌐 Responsive banking UI using ZK 10 + external CSS  
- 🏗️ Follows **MVC (Model–View–Controller)** architecture for scalability and maintainability  

---

### 🧩 **Tech Stack & Architecture**
| Category | Technology |
|-----------|-------------|
| **Frontend (View)** | ZK Framework 10, HTML, CSS |
| **Backend (Controller)** | Java Servlets / Spring Boot Controllers |
| **Database (Model)** | MySQL |
| **Tools** | Apache Tomcat, Maven |
| **Version Control** | Git, GitHub |

---

### 🏗️ **Architecture**
The project follows the **MVC (Model–View–Controller)** design pattern:

- 🧠 **Model:** Represents database entities, DAO layer, and MySQL data logic.  
- 🎨 **View:** ZUL pages designed using ZK Framework for interactive and responsive UI.  
- ⚙️ **Controller:** Java controller classes (e.g., `UserSignupController.java`, `UserLoginController.java`) handle user input, perform validations, and connect Model ↔ View.

This structure ensures **clean separation of concerns**, **better maintainability**, and **easy scalability**.

---

### 📂 **Project Structure**
FinTrust/
│
├── src/
│ ├── main/java/com/fintrust/controller/
│ │ ├── UserSignupController.java
│ │ ├── UserLoginController.java
│ │ └── AdminController.java
│ │
│ ├── main/webapp/
│ │ ├── index.zul
│ │ ├── /user/
│ │ │ ├── userSignup.zul
│ │ │ ├── userLogin.zul
│ │ │ └── userInfo.zul
│ │ ├── /admin/
│ │ │ ├── adminLogin.zul
│ │ │ ├── adminAccounts.zul
│ │ │ └── adminTransactions.zul
│ │ └── /resources/
│ │ ├── /css/style.css
│ │ └── /images/
│ │ ├── signup-rightSec.png
│ │ └── logo.png
│
├── pom.xml
└── README.md

---

### ⚙️ **How to Run**

#### **1️⃣ Clone the Repository**
```bash
git clone https://github.com/<your-username>/FinTrust-Banking-System.git
cd FinTrust-Banking-System

### ⚙️ **Configure Database**
spring.datasource.url=jdbc:mysql://localhost:3306/fintrustdb
spring.datasource.username=root
spring.datasource.password=yourpassword

#### **Open in Browser**
http://localhost:8080/


