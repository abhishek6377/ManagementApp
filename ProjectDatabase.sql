-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.42 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.10.0.7000
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for managementapp
DROP DATABASE IF EXISTS `managementapp`;
CREATE DATABASE IF NOT EXISTS `managementapp` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `managementapp`;

-- Dumping structure for table managementapp.documents
DROP TABLE IF EXISTS `documents`;
CREATE TABLE IF NOT EXISTS `documents` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `file_path` varchar(255) NOT NULL,
  `file_size` bigint DEFAULT NULL,
  `filename` varchar(255) NOT NULL,
  `mime_type` varchar(255) DEFAULT NULL,
  `status` enum('PENDING','PROCESSING','INDEXED','ERROR') NOT NULL,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `document_type_id` bigint DEFAULT NULL,
  `uploader_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpgon1w2k2kaujvhrficxuj854` (`document_type_id`),
  KEY `FKpevi92qn6kuotawa7dy2gpu39` (`uploader_id`),
  CONSTRAINT `FKpevi92qn6kuotawa7dy2gpu39` FOREIGN KEY (`uploader_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKpgon1w2k2kaujvhrficxuj854` FOREIGN KEY (`document_type_id`) REFERENCES `document_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table managementapp.documents: ~3 rows (approximately)
INSERT INTO `documents` (`id`, `created_at`, `description`, `file_path`, `file_size`, `filename`, `mime_type`, `status`, `title`, `updated_at`, `document_type_id`, `uploader_id`) VALUES
	(4, '2025-05-13 13:02:08.209241', 'Some description', '5e9a7b87-ef93-41a5-8404-7f2b1518e47a_Screenshot (3).png', 439426, 'Screenshot (3).png', 'image/png', 'PENDING', 'My Document Title', '2025-05-13 13:02:08.209241', 1, 1),
	(5, '2025-05-13 13:03:40.254729', 'Batch uploaded document', '38c8db00-e483-4095-ab9c-b522be7a015d_Screenshot (3).png', 439426, 'Screenshot (3).png', 'image/png', 'PENDING', 'Screenshot (3).png', '2025-05-13 13:03:40.254729', NULL, 1),
	(6, '2025-05-13 13:04:21.616809', 'Batch uploaded document', '6be561fe-18fb-404c-a41d-17804d9e1753_Screenshot 2025-04-07 170405.png', 72391, 'Screenshot 2025-04-07 170405.png', 'image/png', 'PENDING', 'Screenshot 2025-04-07 170405.png', '2025-05-13 13:04:21.616809', NULL, 1);

-- Dumping structure for table managementapp.document_contents
DROP TABLE IF EXISTS `document_contents`;
CREATE TABLE IF NOT EXISTS `document_contents` (
  `id` bigint NOT NULL,
  `content` text,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKajb51cl4s2kjsl0osuqv9b6v1` FOREIGN KEY (`id`) REFERENCES `documents` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table managementapp.document_contents: ~0 rows (approximately)

-- Dumping structure for table managementapp.document_keywords
DROP TABLE IF EXISTS `document_keywords`;
CREATE TABLE IF NOT EXISTS `document_keywords` (
  `document_id` bigint NOT NULL,
  `keyword` varchar(255) DEFAULT NULL,
  KEY `FKcxg06vu0b6mkepui8enx6a7qj` (`document_id`),
  CONSTRAINT `FKcxg06vu0b6mkepui8enx6a7qj` FOREIGN KEY (`document_id`) REFERENCES `documents` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table managementapp.document_keywords: ~2 rows (approximately)
INSERT INTO `document_keywords` (`document_id`, `keyword`) VALUES
	(4, 'finance'),
	(4, 'report');

-- Dumping structure for table managementapp.document_type
DROP TABLE IF EXISTS `document_type`;
CREATE TABLE IF NOT EXISTS `document_type` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_h2sgue44om58dw11wqwja5lec` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table managementapp.document_type: ~1 rows (approximately)
INSERT INTO `document_type` (`id`, `description`, `name`) VALUES
	(1, 'hgfvy', 'adhar');

-- Dumping structure for table managementapp.roles
DROP TABLE IF EXISTS `roles`;
CREATE TABLE IF NOT EXISTS `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` enum('ROLE_ADMIN','ROLE_EDITOR','ROLE_VIEWER') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table managementapp.roles: ~3 rows (approximately)
INSERT INTO `roles` (`id`, `name`) VALUES
	(1, 'ROLE_ADMIN'),
	(2, 'ROLE_EDITOR'),
	(3, 'ROLE_VIEWER');

-- Dumping structure for table managementapp.users
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `last_login` datetime(6) DEFAULT NULL,
  `password` varchar(120) DEFAULT NULL,
  `reset_password_token` varchar(255) DEFAULT NULL,
  `reset_password_token_expiry_date` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `verification_token` varchar(255) DEFAULT NULL,
  `verification_token_expiry_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table managementapp.users: ~1 rows (approximately)
INSERT INTO `users` (`id`, `created_at`, `email`, `enabled`, `last_login`, `password`, `reset_password_token`, `reset_password_token_expiry_date`, `updated_at`, `username`, `verification_token`, `verification_token_expiry_date`) VALUES
	(1, '2025-05-13 07:18:27.492152', 'admin@gmail.com', b'0', NULL, '$2a$10$i5siIWTxAOafZlOmcnLlwO8NNw9U29iGzJsf2EcMTbOkK5PsZWo4u', NULL, NULL, '2025-05-13 07:18:27.492152', 'admin', '98c19ab1-769b-416e-8824-511d70a5e6b6', '2025-05-14 07:18:27.486151'),
	(2, '2025-05-13 09:20:19.989398', 'Abhishekabhi@gmail.com', b'0', NULL, '$2a$10$hB1By/GY1uX48w.Uz4FrXOql392F.OpOQlsJ/d.O7BK/4BnBq9HfG', NULL, NULL, '2025-05-13 09:20:19.989398', 'AbhishekAbhii', '9637e9bc-741a-4668-b3d5-1b5cd81d561d', '2025-05-14 09:20:19.974987');

-- Dumping structure for table managementapp.user_roles
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE IF NOT EXISTS `user_roles` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FKh8ciramu9cc9q3qcqiv4ue8a6` (`role_id`),
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table managementapp.user_roles: ~1 rows (approximately)
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
	(1, 1),
	(2, 1);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
