-- MySQL dump 10.13  Distrib 8.0.43, for Linux (x86_64)
--
-- Host: localhost    Database: fintrustdb
-- ------------------------------------------------------
-- Server version	8.0.43-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `account_no` bigint NOT NULL,
  `balance` double NOT NULL,
  `account_type` enum('SAVING','CURRENT','SALARY') NOT NULL,
  `account_status` enum('ACTIVE','INACTIVE','CLOSED') DEFAULT 'ACTIVE',
  `branch_name` varchar(100) NOT NULL,
  `mode_of_operation` enum('SELF','JOINT','EITHER_OR_SURVIVOR') NOT NULL,
  `nominee_id` bigint unsigned DEFAULT NULL,
  `customer_id` bigint unsigned DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`account_no`),
  KEY `nominee_id` (`nominee_id`),
  KEY `customer_id` (`customer_id`),
  CONSTRAINT `account_ibfk_1` FOREIGN KEY (`nominee_id`) REFERENCES `nominee` (`nominee_id`),
  CONSTRAINT `account_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  CONSTRAINT `account_chk_1` CHECK ((`balance` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (10001001,1111,'SAVING','ACTIVE','Mathura','SELF',123412341234,1,'2025-11-14 12:53:12'),(10001002,1111,'CURRENT','ACTIVE','Mathura','SELF',123456789012,1,'2025-11-14 12:57:04'),(10001003,1155,'SALARY','ACTIVE','Mathura','SELF',123456789011,1,'2025-11-14 14:59:42'),(10001004,12000,'SAVING','ACTIVE','Mathura','SELF',123456789012,2149,'2025-11-15 10:26:51'),(10001005,13450,'SAVING','ACTIVE','Mathura','SELF',123456789012,4471,'2025-11-15 10:30:02'),(10001006,20000,'SALARY','ACTIVE','Aligarch','SELF',123456789012,2149,'2025-11-15 13:02:49');
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_update_request`
--

DROP TABLE IF EXISTS `account_update_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_update_request` (
  `request_id` bigint NOT NULL AUTO_INCREMENT,
  `account_no` bigint NOT NULL,
  `new_account_type` enum('SAVINGS','CURRENT','SALARY') DEFAULT NULL,
  `new_branch_name` varchar(100) DEFAULT NULL,
  `new_mode_of_operation` enum('SELF','JOINT') DEFAULT NULL,
  `status` enum('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
  `requested_by` bigint DEFAULT NULL,
  `reviewed_by` bigint DEFAULT NULL,
  `request_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `review_date` datetime DEFAULT NULL,
  PRIMARY KEY (`request_id`),
  KEY `account_no` (`account_no`),
  CONSTRAINT `account_update_request_ibfk_1` FOREIGN KEY (`account_no`) REFERENCES `account` (`account_no`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_update_request`
--

LOCK TABLES `account_update_request` WRITE;
/*!40000 ALTER TABLE `account_update_request` DISABLE KEYS */;
INSERT INTO `account_update_request` VALUES (1,10001001,'SALARY','New Delhi','SELF','REJECTED',1001,201,'2025-11-14 13:11:01','2025-11-14 13:11:32');
/*!40000 ALTER TABLE `account_update_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `beneficiary`
--

DROP TABLE IF EXISTS `beneficiary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `beneficiary` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `name` varchar(100) NOT NULL,
  `account_number` varchar(20) NOT NULL,
  `bank_name` varchar(100) NOT NULL,
  `ifsc_code` varchar(20) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `beneficiary_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `beneficiary`
--

LOCK TABLES `beneficiary` WRITE;
/*!40000 ALTER TABLE `beneficiary` DISABLE KEYS */;
/*!40000 ALTER TABLE `beneficiary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `card`
--

DROP TABLE IF EXISTS `card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card` (
  `card_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `card_number` char(16) NOT NULL,
  `customer_id` bigint unsigned NOT NULL,
  `account_no` bigint NOT NULL,
  `cvv` char(3) NOT NULL,
  `pin` char(4) NOT NULL,
  `issued_date` date DEFAULT (curdate()),
  `expiry_date` date GENERATED ALWAYS AS ((`issued_date` + interval 3 year)) STORED,
  `card_status` varchar(20) DEFAULT 'Active',
  `maximum_limit` int DEFAULT '50000',
  PRIMARY KEY (`card_id`),
  UNIQUE KEY `card_number` (`card_number`),
  KEY `customer_id` (`customer_id`),
  KEY `account_no` (`account_no`),
  CONSTRAINT `card_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  CONSTRAINT `card_ibfk_2` FOREIGN KEY (`account_no`) REFERENCES `account` (`account_no`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card`
--

LOCK TABLES `card` WRITE;
/*!40000 ALTER TABLE `card` DISABLE KEYS */;
INSERT INTO `card` (`card_id`, `card_number`, `customer_id`, `account_no`, `cvv`, `pin`, `issued_date`, `card_status`, `maximum_limit`) VALUES (1,'4567891234567890',1,10001001,'314','4321','2025-11-14','Blocked',50000),(2,'4567899876543210',1,10001001,'852','1299','2025-11-14','Blocked',50000),(3,'4567891122334455',1,10001001,'123','1111','2020-11-14','Expired',50000);
/*!40000 ALTER TABLE `card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `card_request`
--

DROP TABLE IF EXISTS `card_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card_request` (
  `request_no` bigint unsigned NOT NULL AUTO_INCREMENT,
  `card_type` varchar(30) NOT NULL,
  `card_category` varchar(30) NOT NULL,
  `address` varchar(255) NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `card_request_status` varchar(20) DEFAULT 'PENDING',
  `customer_id` bigint unsigned NOT NULL,
  `account_no` bigint NOT NULL,
  `requested_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`request_no`),
  KEY `account_no` (`account_no`),
  KEY `customer_id` (`customer_id`),
  CONSTRAINT `card_request_ibfk_1` FOREIGN KEY (`account_no`) REFERENCES `account` (`account_no`),
  CONSTRAINT `card_request_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_request`
--

LOCK TABLES `card_request` WRITE;
/*!40000 ALTER TABLE `card_request` DISABLE KEYS */;
INSERT INTO `card_request` VALUES (1,'VISA','Debit Card','qqqqqqq','qqqqqq','PENDING',1,10001001,'2025-11-14 16:58:52'),(2,'MasterCard','Credit Card','Delhi','Yes','PENDING',1,10001004,'2025-11-15 11:56:48');
/*!40000 ALTER TABLE `card_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `customer_id` bigint unsigned NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `state` varchar(50) DEFAULT NULL,
  `dist` varchar(50) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `pincode` varchar(10) DEFAULT NULL,
  `dob` varchar(20) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `registered_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) DEFAULT 'Active',
  `image` varchar(50) DEFAULT NULL,
  `twoFactor` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'Nayan','n@g.com','6212345678','Male','India','Delhi','Jamtara','Delhi','1212','2025-11-05','1','2025-11-11 10:13:55','Active','default.jpg',NULL),(2149,'Nayan Mandal','nayan@gmail.com','6205358154','Male','India','Jharkhand','Delhi','Delhi','11061','2002-11-07','123456','2025-11-15 04:55:47','Active',NULL,NULL),(4471,'Vikash Kumar','vikash@gmail.com','6212345678','Male','India','Jharkhand','Jamtara','Jamtara','815351','2001-02-01','123456','2025-11-15 04:59:25','Active',NULL,NULL);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nominee`
--

DROP TABLE IF EXISTS `nominee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nominee` (
  `nominee_id` bigint unsigned NOT NULL,
  `nominee_name` varchar(100) NOT NULL,
  `nominee_relation` varchar(50) NOT NULL,
  PRIMARY KEY (`nominee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nominee`
--

LOCK TABLES `nominee` WRITE;
/*!40000 ALTER TABLE `nominee` DISABLE KEYS */;
INSERT INTO `nominee` VALUES (123412341234,'sdsd','Mother'),(123456789011,'Nayan','Father'),(123456789012,'Nayan','Father');
/*!40000 ALTER TABLE `nominee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `support_tickets`
--

DROP TABLE IF EXISTS `support_tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `support_tickets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_name` varchar(100) DEFAULT NULL,
  `mobile` varchar(15) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `customer_id` varchar(50) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  `subcategory` varchar(100) DEFAULT NULL,
  `priority` varchar(20) DEFAULT NULL,
  `subject` varchar(200) DEFAULT NULL,
  `description` text,
  `preferred_contact_method` varchar(50) DEFAULT NULL,
  `preferred_time` varchar(50) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'Open',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `support_tickets`
--

LOCK TABLES `support_tickets` WRITE;
/*!40000 ALTER TABLE `support_tickets` DISABLE KEYS */;
INSERT INTO `support_tickets` VALUES (1,'Nayan Mandal','234567891','nayan@gmail.com','123','Account Issues','Password Reset','Medium','test','','','','Open','2025-11-15 11:07:58','2025-11-15 11:07:58');
/*!40000 ALTER TABLE `support_tickets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket_attachments`
--

DROP TABLE IF EXISTS `ticket_attachments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket_attachments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ticket_id` bigint DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `uploaded_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `ticket_id` (`ticket_id`),
  CONSTRAINT `ticket_attachments_ibfk_1` FOREIGN KEY (`ticket_id`) REFERENCES `support_tickets` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket_attachments`
--

LOCK TABLES `ticket_attachments` WRITE;
/*!40000 ALTER TABLE `ticket_attachments` DISABLE KEYS */;
/*!40000 ALTER TABLE `ticket_attachments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `from_account` bigint DEFAULT NULL,
  `to_account` bigint DEFAULT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `from_account` (`from_account`),
  KEY `to_account` (`to_account`),
  CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`from_account`) REFERENCES `account` (`account_no`),
  CONSTRAINT `transactions_ibfk_2` FOREIGN KEY (`to_account`) REFERENCES `account` (`account_no`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (1,10001004,10001004,500.00,'SUCCESS','2025-11-15 11:41:17'),(7,10001004,10001004,150.75,'pending','2025-11-15 11:44:13'),(8,10001004,10001005,500.00,'completed','2025-11-15 11:44:13'),(9,10001004,10001004,75.20,'failed','2025-11-15 11:44:13'),(10,10001004,10001005,999.99,'completed','2025-11-15 11:44:13'),(11,10001004,10001004,20.00,'pending','2025-11-15 11:44:13'),(12,10001004,10001004,150.75,'pending','2025-11-04 10:15:00'),(13,10001004,10001004,500.00,'completed','2025-11-06 14:30:00'),(14,10001004,10001004,75.20,'failed','2025-11-08 09:45:00'),(15,10001004,10001004,999.99,'completed','2025-11-10 16:20:00'),(16,10001004,10001004,20.00,'pending','2025-11-13 12:00:00'),(17,10001004,10001004,500.00,'SUCCESS','2025-11-15 13:04:38');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-15 14:54:36
