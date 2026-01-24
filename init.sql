CREATE DATABASE  IF NOT EXISTS `db_alimtrack_prod` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `db_alimtrack_prod`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: db_alimtrack_prod
-- ------------------------------------------------------
-- Server version	9.5.0

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
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

-- Table structure for table `autosave_produccion`
--

DROP TABLE IF EXISTS `autosave_produccion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `autosave_produccion` (
                                       `id_autosave` bigint NOT NULL AUTO_INCREMENT,
                                       `id_produccion` bigint NOT NULL,
                                       `datos` json NOT NULL,
                                       `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`id_autosave`),
                                       KEY `idx_autosave_produccion` (`id_produccion`),
                                       CONSTRAINT `autosave_produccion_ibfk_1` FOREIGN KEY (`id_produccion`) REFERENCES `produccion` (`id_produccion`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `autosave_produccion`
--

LOCK TABLES `autosave_produccion` WRITE;
/*!40000 ALTER TABLE `autosave_produccion` DISABLE KEYS */;
INSERT INTO `autosave_produccion` VALUES (1,2,'{\"lote\": \"LOTE-QUE-24001\", \"estado\": \"EN_PROCESO\", \"encargado\": \"Operador Línea 1\", \"observaciones\": \"Producción en curso\", \"codigo_produccion\": \"PROD-QUE-2024-001\", \"respuestas_campos\": {\"campo_10\": \"LECEHE\", \"campo_11\": \"0.15\", \"campo_12\": \"1.8\", \"campo_13\": \"Mesófilos y termófilos\", \"campo_14\": \"35.5\", \"campo_15\": \"45\"}, \"respuestas_tablas\": {\"celda_5_4\": \"ASDSAD\"}, \"timestamp_autosave\": \"2025-12-04T22:37:34.062104400\"}','2025-12-04 22:37:34');
/*!40000 ALTER TABLE `autosave_produccion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `campo_simple`
--

DROP TABLE IF EXISTS `campo_simple`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `campo_simple` (
                                `id_campo` bigint NOT NULL AUTO_INCREMENT,
                                `id_seccion` bigint NOT NULL,
                                `id_grupo` bigint DEFAULT NULL,
                                `nombre` varchar(100) NOT NULL,
                                `tipo_dato` enum('ENTERO','DECIMAL','TEXTO','FECHA','HORA','BOOLEANO') NOT NULL,
                                `orden` int NOT NULL DEFAULT '0',
                                PRIMARY KEY (`id_campo`),
                                KEY `id_grupo` (`id_grupo`),
                                KEY `idx_campo_seccion` (`id_seccion`),
                                CONSTRAINT `campo_simple_ibfk_1` FOREIGN KEY (`id_seccion`) REFERENCES `seccion` (`id_seccion`),
                                CONSTRAINT `campo_simple_ibfk_2` FOREIGN KEY (`id_grupo`) REFERENCES `grupo_campos` (`id_grupo`)
) ENGINE=InnoDB AUTO_INCREMENT=7025 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campo_simple`
--

LOCK TABLES `campo_simple` WRITE;
/*!40000 ALTER TABLE `campo_simple` DISABLE KEYS */;
INSERT INTO `campo_simple` VALUES (1,1,NULL,'Leche entera','TEXTO',1),(2,1,NULL,'Cultivos lácticos','TEXTO',2),(3,1,NULL,'Proteína de leche','DECIMAL',3),(4,2,1,'Temperatura pasteurización','DECIMAL',1),(5,2,1,'Tiempo pasteurización','ENTERO',2),(6,2,2,'Temperatura fermentación','DECIMAL',1),(7,2,2,'Tiempo fermentación','ENTERO',2),(8,2,3,'Temperatura separación','DECIMAL',1),(9,2,3,'Porcentaje graso final','DECIMAL',2),(10,4,NULL,'Leche de búfala','TEXTO',1),(11,4,NULL,'Cuajo','DECIMAL',2),(12,4,NULL,'Sal','DECIMAL',3),(13,4,NULL,'Cultivos iniciadores','TEXTO',4),(14,5,4,'Temperatura coagulación','DECIMAL',1),(15,5,4,'Tiempo coagulación','ENTERO',2),(16,5,5,'Tamaño del grano','TEXTO',1),(17,5,5,'Temperatura escaldado','DECIMAL',2),(18,5,6,'Temperatura estirado','DECIMAL',1),(19,5,6,'Tiempo de estirado','ENTERO',2),(1101,1010,1011,'Harina (gr)','ENTERO',1),(1102,1010,1011,'Mantequilla (gr)','ENTERO',2),(1103,1010,1011,'Azúcar (gr)','ENTERO',3),(1104,1010,1011,'Agua (ml)','ENTERO',4),(1105,1010,1011,'Sal (gr)','DECIMAL',5),(1106,1010,1011,'Tipo de Harina','TEXTO',6),(1107,1010,1011,'Tiempo de Reposo (min)','ENTERO',7),(1108,1010,1012,'Manzanas (gr)','ENTERO',1),(1109,1010,1012,'Canela (gr)','DECIMAL',2),(1110,1010,1012,'Jugo de Limón (ml)','DECIMAL',3),(1111,1010,1012,'Almidón (gr)','ENTERO',4),(1112,1010,1012,'Tipo de Manzana','TEXTO',5),(1113,1010,1012,'Tiempo de Cocción Previa (min)','ENTERO',6),(1114,1020,NULL,'Temperatura Horno (°C)','ENTERO',1),(1115,1020,NULL,'Tiempo de Horneado (min)','ENTERO',2),(1116,1020,NULL,'Humedad Relativa (%)','DECIMAL',3),(1117,1020,NULL,'Observaciones de Tostado','TEXTO',4),(1118,1020,NULL,'Altura de la Tarta (cm)','DECIMAL',5),(1119,1020,NULL,'Unidades Producidas','ENTERO',6),(1120,1020,NULL,'Costo por Unidad (€)','DECIMAL',7),(2101,2010,2011,'Harina 000 (gr)','ENTERO',1),(2102,2010,2011,'Mantequilla (gr)','ENTERO',2),(2103,2010,2011,'Huevos (unidades)','ENTERO',3),(2104,2010,2011,'Azúcar (gr)','ENTERO',4),(2105,2010,2011,'Leche (ml)','ENTERO',5),(2106,2010,2011,'Sal (gr)','DECIMAL',6),(2107,2010,2012,'Levadura Fresca (gr)','DECIMAL',1),(2108,2010,2012,'Temperatura Leche (°C)','DECIMAL',2),(2109,2010,2012,'Tiempo de Espera (min)','ENTERO',3),(2110,2010,2012,'Volumen Inicial','DECIMAL',4),(2111,2020,2021,'Pasas (gr)','ENTERO',1),(2112,2020,2021,'Fruta Abrillantada (gr)','ENTERO',2),(2113,2020,2021,'Nueces (gr)','ENTERO',3),(2114,2020,2021,'Almendras (gr)','ENTERO',4),(2115,2020,2022,'Ralladura de Cítricos (gr)','DECIMAL',1),(2116,2020,2022,'Extracto de Vainilla (ml)','DECIMAL',2),(2117,2020,2022,'Licor (ml)','DECIMAL',3),(2118,2040,NULL,'Temperatura Horno (°C)','ENTERO',1),(2119,2040,NULL,'Tiempo de Horneado (min)','ENTERO',2),(2120,2040,NULL,'Unidades Finales','ENTERO',3),(3101,3010,3011,'Mantequilla (gr)','ENTERO',1),(3102,3010,3011,'Huevos (unidades)','ENTERO',2),(3103,3010,3011,'Leche (ml)','ENTERO',3),(3104,3010,3011,'Ralladura Naranja','DECIMAL',4),(3105,3010,3012,'Harina (gr)','ENTERO',1),(3106,3010,3012,'Azúcar (gr)','ENTERO',2),(3107,3010,3012,'Polvo Hornear','DECIMAL',3),(3108,3010,3012,'Pasas (gr)','ENTERO',4),(3109,3010,3012,'Cerezas (gr)','ENTERO',5),(3110,3010,3012,'Nueces (gr)','ENTERO',6),(3111,3020,NULL,'Temp Horneado (°C)','ENTERO',1),(3112,3020,NULL,'Tiempo Total (min)','ENTERO',2),(3113,3020,NULL,'Observación Tostado','TEXTO',3),(3114,3020,NULL,'Azúcar Impalpable (gr)','ENTERO',4),(3115,3020,NULL,'Jugo Limón Glaseado','DECIMAL',5),(3116,3020,NULL,'Tiempo Secado Glaseado','ENTERO',6),(3117,3020,NULL,'Peso Final (gr)','ENTERO',7),(3118,3020,NULL,'Volumen Final (cm³)','DECIMAL',8),(3119,3020,NULL,'Densidad Final (g/cm³)','DECIMAL',9),(3120,3020,NULL,'Saboración Final','TEXTO',10),(5101,510,NULL,'Litros de Leche','ENTERO',1),(5102,510,NULL,'Temperatura de Cuajado (°C)','DECIMAL',2),(5103,510,NULL,'Tipo de Cuajo Usado','TEXTO',3),(5211,520,521,'Presión Máxima (bar)','DECIMAL',1),(5212,520,521,'Tiempo Total de Prensado (horas)','ENTERO',2),(5221,520,522,'Concentración de Salmuera (%)','DECIMAL',1),(5222,520,522,'Observaciones de Salado','TEXTO',2),(6101,610,611,'Litros de Leche','DECIMAL',1),(6102,610,611,'Cultivo Termófilo (gr)','DECIMAL',2),(6103,610,611,'Concentración de Cloruro de Calcio (%)','DECIMAL',3),(6201,620,621,'Temperatura de Cuajado (°C)','DECIMAL',1),(6202,620,621,'Tiempo de Cuajado (min)','ENTERO',2),(6203,620,622,'Tamaño del Grano (mm)','DECIMAL',1),(6204,620,622,'Tiempo de Batido (min)','ENTERO',2),(6205,620,622,'Temperatura de Batido (°C)','DECIMAL',3),(6301,630,631,'Días de Maduración (total)','ENTERO',1),(6302,630,631,'Observaciones de Corteza (ej. ceniza)','TEXTO',2),(7020,630,631,'Cantidad Producida (Unidades)','ENTERO',3),(7021,610,611,'Peso Total Fruta (kg)','DECIMAL',4),(7022,630,631,'Fecha de Caducidad','FECHA',4),(7023,630,631,'Hora de Finalización del Horneado','HORA',5),(7024,630,631,'Control de Calidad Aprobado','BOOLEANO',6);
/*!40000 ALTER TABLE `campo_simple` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `columna_tabla`
--

DROP TABLE IF EXISTS `columna_tabla`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `columna_tabla` (
                                 `id_columna` bigint NOT NULL AUTO_INCREMENT,
                                 `id_tabla` bigint NOT NULL,
                                 `nombre` varchar(100) NOT NULL,
                                 `tipo_dato` enum('DECIMAL','ENTERO','TEXTO') NOT NULL,
                                 `orden` int NOT NULL DEFAULT '0',
                                 PRIMARY KEY (`id_columna`,`id_tabla`),
                                 UNIQUE KEY `uk_columna_orden` (`id_tabla`,`orden`),
                                 CONSTRAINT `fk_columna_tabla` FOREIGN KEY (`id_tabla`) REFERENCES `tabla` (`id_tabla`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `columna_tabla`
--

LOCK TABLES `columna_tabla` WRITE;
/*!40000 ALTER TABLE `columna_tabla` DISABLE KEYS */;
INSERT INTO `columna_tabla` VALUES (1,1,'Parámetro','TEXTO',1),(1,1031,'Objetivo','DECIMAL',1),(1,1032,'Criterio','TEXTO',1),(1,2031,'Tiempo (min)','ENTERO',1),(1,2041,'Punto de Medición','TEXTO',1),(1,3031,'Esperado (cm)','DECIMAL',1),(1,3032,'Criterio','TEXTO',1),(1,5300,'Valor Mínimo Requerido','DECIMAL',1),(2,1,'Valor máximo','DECIMAL',2),(2,1031,'Medido','DECIMAL',2),(2,1032,'Puntuación (1-5)','ENTERO',2),(2,2031,'Altura (cm)','DECIMAL',2),(2,2041,'Temperatura (°C)','DECIMAL',2),(2,3031,'Medido (cm)','DECIMAL',2),(2,3032,'Resultado','TEXTO',2),(2,5300,'Valor Medido','DECIMAL',2),(3,1,'Unidad','TEXTO',3),(3,1031,'Comentarios','TEXTO',3),(3,1032,'Aprobado','TEXTO',3),(3,2031,'Estado','TEXTO',3),(3,2041,'Conclusión','TEXTO',3),(3,5300,'Aprobación (S/N)','TEXTO',3),(4,2,'Parámetro','TEXTO',1),(5,2,'Valor mínimo','DECIMAL',2),(6,2,'Valor máximo','DECIMAL',3),(7,2,'Unidad','TEXTO',4);
/*!40000 ALTER TABLE `columna_tabla` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fila_tabla`
--

DROP TABLE IF EXISTS `fila_tabla`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fila_tabla` (
                              `id_fila` bigint NOT NULL AUTO_INCREMENT,
                              `id_tabla` bigint NOT NULL,
                              `nombre` varchar(100) NOT NULL,
                              `orden` int NOT NULL DEFAULT '0',
                              PRIMARY KEY (`id_fila`,`id_tabla`),
                              UNIQUE KEY `uk_fila_orden` (`id_tabla`,`orden`),
                              KEY `id_tabla` (`id_tabla`),
                              CONSTRAINT `fk_fila_tabla` FOREIGN KEY (`id_tabla`) REFERENCES `tabla` (`id_tabla`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fila_tabla`
--

LOCK TABLES `fila_tabla` WRITE;
/*!40000 ALTER TABLE `fila_tabla` DISABLE KEYS */;
INSERT INTO `fila_tabla` VALUES (1,1,'Recuento bacteriano',1),(1,1031,'Espesor (mm)',1),(1,1032,'Sabor',1),(1,2031,'Muestra A',1),(1,2041,'Centro',1),(1,3031,'Largo',1),(1,3032,'Grosor Mínimo (mm)',1),(1,5300,'Peso de la Rueda (kg)',1),(2,1,'E. coli',2),(2,1031,'Densidad (g/cm³)',2),(2,1032,'Textura',2),(2,2031,'Muestra B',2),(2,2041,'Lateral',2),(2,3031,'Ancho',2),(2,3032,'Cobertura Total (%)',2),(2,5300,'pH Final',2),(3,1,'Salmonella',3),(3,5300,'Humedad (%)',3),(4,1,'Listeria',4),(5,2,'Grasa butírica',1),(6,2,'Humedad',2),(7,2,'Proteína',3),(8,2,'Sal',4),(9,2,'pH',5);
/*!40000 ALTER TABLE `fila_tabla` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grupo_campos`
--

DROP TABLE IF EXISTS `grupo_campos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grupo_campos` (
                                `id_grupo` bigint NOT NULL AUTO_INCREMENT,
                                `id_seccion` bigint NOT NULL,
                                `subtitulo` varchar(255) NOT NULL,
                                `orden` int NOT NULL,
                                PRIMARY KEY (`id_grupo`),
                                UNIQUE KEY `uk_grupo_subtitulo_seccion` (`subtitulo`,`id_seccion`),
                                KEY `id_seccion` (`id_seccion`),
                                CONSTRAINT `grupo_campos_ibfk_1` FOREIGN KEY (`id_seccion`) REFERENCES `seccion` (`id_seccion`)
) ENGINE=InnoDB AUTO_INCREMENT=3013 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grupo_campos`
--

LOCK TABLES `grupo_campos` WRITE;
/*!40000 ALTER TABLE `grupo_campos` DISABLE KEYS */;
INSERT INTO `grupo_campos` VALUES (1,2,'Pasteurización',1),(2,2,'Fermentación',2),(3,2,'Separación',3),(4,5,'Coagulación',1),(5,5,'Corte y Escaldado',2),(6,5,'Estirado y Formado',3),(521,520,'Fase de Prensado',1),(522,520,'Fase de Salado',2),(1011,1010,'Ingredientes de la Masa',1),(1012,1010,'Preparación del Relleno',2),(2011,2010,'Base de la Masa',1),(2012,2010,'Activación de Levadura',2),(2021,2020,'Frutas Secas y Nueces',1),(2022,2020,'Especias y Aromatizantes',2),(3011,3010,'Líquidos y Grasas',1),(3012,3010,'Sólidos y Frutas',2);
/*!40000 ALTER TABLE `grupo_campos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produccion`
--

DROP TABLE IF EXISTS `produccion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produccion` (
                              `id_produccion` bigint NOT NULL AUTO_INCREMENT,
                              `id_version` bigint NOT NULL,
                              `creado_por` bigint NOT NULL,
                              `codigo_produccion` varchar(100) NOT NULL,
                              `encargado` varchar(100) DEFAULT NULL,
                              `lote` varchar(100) DEFAULT NULL,
                              `estado` enum('CANCELADA','EN_PROCESO','FINALIZADA') NOT NULL DEFAULT 'EN_PROCESO',
                              `fecha_inicio` datetime NOT NULL,
                              `fecha_fin` datetime DEFAULT NULL,
                              `observaciones` varchar(255) DEFAULT NULL,
                              `fecha_modificacion` datetime DEFAULT NULL,
                              PRIMARY KEY (`id_produccion`),
                              UNIQUE KEY `codigo_produccion_UNIQUE` (`codigo_produccion`),
                              KEY `id_version` (`id_version`),
                              KEY `idx_produccion_fecha` (`fecha_inicio`,`fecha_fin`),
                              KEY `fk_produccion_usuario` (`creado_por`),
                              CONSTRAINT `fk_produccion_usuario` FOREIGN KEY (`creado_por`) REFERENCES `usuario` (`id_usuario`),
                              CONSTRAINT `produccion_ibfk_1` FOREIGN KEY (`id_version`) REFERENCES `version_receta` (`id_version`)
) ENGINE=InnoDB AUTO_INCREMENT=70019 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produccion`
--

LOCK TABLES `produccion` WRITE;
/*!40000 ALTER TABLE `produccion` DISABLE KEYS */;
INSERT INTO `produccion` VALUES (1,1,3,'PROD-YOG-2024-001','Operador Línea 1','LOTE-YOG-24001','FINALIZADA','2024-01-15 08:00:00','2024-01-15 16:30:00','Producción exitosa','2024-01-15 08:00:00'),(2,2,3,'PROD-QUE-2024-001','Operador Línea 1','LOTE-QUE-24001','CANCELADA','2024-01-16 08:00:00','2025-12-04 22:37:34','Producción en curso','2025-12-04 22:37:34'),(700,500,1,'PARM-2025-045','Admin Quesero','LOTE-QPR-25A','FINALIZADA','2025-11-19 08:00:00','2025-11-19 23:35:13','Lote de alta calidad. El pH final fue óptimo.','2024-01-15 08:00:00'),(10000,1000,2,'TARTA-25-10-A','María Gómez','A-20251015','FINALIZADA','2025-10-15 08:00:00','2025-11-20 00:09:18','Lote de prueba. Masa más crujiente lograda.','2024-01-15 08:00:00'),(20000,2000,2,'PANDUL-25-11-C','María Gómez','C-20251101','CANCELADA','2025-11-20 06:00:00',NULL,'Iniciada fase de amasado. Pendiente incorporar frutas.','2024-01-15 08:00:00'),(30000,3000,2,'BUDIN-25-11-F','Juan Pérez','F-20251105','FINALIZADA','2025-11-05 10:00:00','2025-11-05 15:00:00','Excelente humedad y glaseado perfecto.','2024-01-15 08:00:00'),(70015,3000,8,'PRODTEST1234','encargado1123','LOTE TEST1231232','FINALIZADA','2026-01-09 11:56:05','2026-01-20 21:35:19','TEST1231232','2026-01-20 21:35:19'),(70016,1000,8,'190190',NULL,'101','FINALIZADA','2026-01-09 15:38:31','2026-01-09 15:39:10',NULL,'2026-01-09 15:39:10'),(70017,3000,8,'TEST-PRODUCCION-109','encargado121212','QASDAS','FINALIZADA','2026-01-20 23:40:53','2026-01-21 21:25:30','SASSDD','2026-01-21 21:25:30'),(70018,2000,8,'1123123','123123','123123','EN_PROCESO','2026-01-21 21:25:46',NULL,NULL,'2026-01-22 06:36:56');
/*!40000 ALTER TABLE `produccion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `receta`
--

DROP TABLE IF EXISTS `receta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receta` (
                          `id_receta` bigint NOT NULL AUTO_INCREMENT,
                          `codigo_receta` varchar(255) NOT NULL,
                          `descripcion` varchar(255) DEFAULT NULL,
                          `creado_por` bigint NOT NULL,
                          `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `nombre` varchar(255) NOT NULL,
                          PRIMARY KEY (`id_receta`),
                          UNIQUE KEY `codigo_receta` (`codigo_receta`),
                          KEY `creado_por` (`creado_por`),
                          CONSTRAINT `receta_ibfk_1` FOREIGN KEY (`creado_por`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=706 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receta`
--

LOCK TABLES `receta` WRITE;
/*!40000 ALTER TABLE `receta` DISABLE KEYS */;
INSERT INTO `receta` VALUES (1,'RC-YOG-001','Yogurt griego natural premium',1,'2025-08-20 22:47:45','Yogurt Natural 200ml'),(2,'RC-QUE-001','Queso mozzarella de bufala',1,'2025-08-20 22:47:45','Muzza bufala 1kg'),(50,'REC_PARM','Receta para Queso Parmesano Añejo',1,'2025-11-19 23:35:13','Queso Parmesano Añejo'),(100,'REC_TARTA','Receta base para tarta de manzana',1,'2025-11-20 00:09:18','Tarta de Manzana Clásica'),(200,'REC_PANDUL','Receta de Pan Dulce tradicional con frutas secas',1,'2025-11-20 00:09:31','Pan Dulce Artesanal'),(300,'REC_BUDIN','Budín Navideño Tradicional',1,'2025-11-20 00:09:38','Budín Navideño Clásico'),(600,'RC-QCA-001','Queso de Cabra Curado de 6 meses con ceniza',1,'2025-12-05 18:06:32','Queso de Cabra Curado (6M)'),(700,'RC-QPA-001','Queso duro de maduración larga (12 meses).',1,'2025-12-05 18:06:32','Queso Parmesano (12M)'),(701,'RC-QPS-001','Queso de pasta blanda, maduración corta.',1,'2025-12-05 18:06:32','Queso Port Salud'),(702,'RC-BDL-001','Budín húmedo con glaseado de limón.',2,'2025-12-05 18:06:32','Budín de Limón'),(703,'RC-BDC-001','Budín de chocolate con chips y cobertura.',2,'2025-12-05 18:06:32','Budín de Chocolate'),(704,'RC-MDZ-001','Mermelada de durazno artesanal, bajo en azúcar.',3,'2025-12-05 18:06:32','Mermelada de Durazno'),(705,'RC-MFR-001','Mermelada de frutilla extra con trozos.',3,'2025-12-05 18:06:32','Mermelada de Frutilla');
/*!40000 ALTER TABLE `receta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `respuesta_campo`
--

DROP TABLE IF EXISTS `respuesta_campo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `respuesta_campo` (
                                   `id_respuesta` bigint NOT NULL AUTO_INCREMENT,
                                   `id_produccion` bigint NOT NULL,
                                   `id_campo` bigint NOT NULL,
                                   `valor_numerico` decimal(18,5) DEFAULT NULL,
                                   `valor_fecha` datetime DEFAULT NULL,
                                   `valor_texto` text,
                                   `timestamp` datetime DEFAULT CURRENT_TIMESTAMP,
                                   `creado_por` bigint NOT NULL,
                                   PRIMARY KEY (`id_respuesta`),
                                   KEY `id_campo` (`id_campo`) /*!80000 INVISIBLE */,
                                   KEY `ix_resp_campo_creado_por` (`creado_por`),
                                   KEY `idx_campo_produccion` (`id_produccion`,`id_campo`),
                                   KEY `idx_prod_campo_compuesto` (`id_produccion`,`id_campo`),
                                   CONSTRAINT `fk_resp_campo_usuario` FOREIGN KEY (`creado_por`) REFERENCES `usuario` (`id_usuario`),
                                   CONSTRAINT `respuesta_campo_ibfk_1` FOREIGN KEY (`id_produccion`) REFERENCES `produccion` (`id_produccion`) ON DELETE CASCADE,
                                   CONSTRAINT `respuesta_campo_ibfk_2` FOREIGN KEY (`id_campo`) REFERENCES `campo_simple` (`id_campo`)
) ENGINE=InnoDB AUTO_INCREMENT=30150 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `respuesta_campo`
--

LOCK TABLES `respuesta_campo` WRITE;
/*!40000 ALTER TABLE `respuesta_campo` DISABLE KEYS */;
INSERT INTO `respuesta_campo` VALUES (1,1,1,NULL,NULL,'Leche entera fresca','2025-08-20 22:47:45',3),(2,1,2,NULL,NULL,'Streptococcus thermophilus','2025-08-20 22:47:45',3),(3,1,3,NULL,NULL,'3.5','2025-08-20 22:47:45',3),(4,1,4,NULL,NULL,'85.5','2025-08-20 22:47:45',3),(5,1,5,NULL,NULL,'30','2025-08-20 22:47:45',3),(6,1,6,NULL,NULL,'42.0','2025-08-20 22:47:45',3),(7,1,7,NULL,NULL,'360','2025-08-20 22:47:45',3),(8,1,8,NULL,NULL,'12.5','2025-08-20 22:47:45',3),(9,1,9,NULL,NULL,'2.8','2025-08-20 22:47:45',3),(10,2,10,NULL,NULL,'LECEHE','2025-12-04 21:42:27',3),(11,2,11,NULL,NULL,'0.15','2025-08-20 22:47:45',3),(12,2,12,NULL,NULL,'1.8','2025-08-20 22:47:45',3),(13,2,13,NULL,NULL,'Mesófilos y termófilos','2025-08-20 22:47:45',3),(14,2,14,NULL,NULL,'35.5','2025-08-20 22:47:45',3),(15,2,15,NULL,NULL,'45','2025-08-20 22:47:45',3),(7001,700,5101,NULL,NULL,'400','2025-11-19 23:35:13',1),(7002,700,5102,NULL,NULL,'32.5','2025-11-19 23:35:13',1),(7003,700,5103,NULL,NULL,'Cuajo Vegetal C18','2025-11-19 23:35:13',1),(7004,700,5211,NULL,NULL,'3.8','2025-11-19 23:35:13',1),(7005,700,5212,NULL,NULL,'24','2025-11-19 23:35:13',1),(7006,700,5221,NULL,NULL,'22.0','2025-11-19 23:35:13',1),(7007,700,5222,NULL,NULL,'Sin anomalías. Sal penetró uniformemente.','2025-11-19 23:35:13',1),(10001,10000,1101,NULL,NULL,'500','2025-11-20 00:09:18',2),(10002,10000,1102,NULL,NULL,'250','2025-11-20 00:09:18',2),(10003,10000,1103,NULL,NULL,'100','2025-11-20 00:09:18',2),(10004,10000,1104,NULL,NULL,'50','2025-11-20 00:09:18',2),(10005,10000,1105,NULL,NULL,'5.5','2025-11-20 00:09:18',2),(10006,10000,1106,NULL,NULL,'000 Integral','2025-11-20 00:09:18',2),(10007,10000,1107,NULL,NULL,'30','2025-11-20 00:09:18',2),(10008,10000,1108,NULL,NULL,'1000','2025-11-20 00:09:18',2),(10009,10000,1109,NULL,NULL,'10.5','2025-11-20 00:09:18',2),(10010,10000,1110,NULL,NULL,'20.0','2025-11-20 00:09:18',2),(10011,10000,1111,NULL,NULL,'20','2025-11-20 00:09:18',2),(10012,10000,1112,NULL,NULL,'Granny Smith','2025-11-20 00:09:18',2),(10013,10000,1113,NULL,NULL,'15','2025-11-20 00:09:18',2),(10014,10000,1114,NULL,NULL,'180','2025-11-20 00:09:18',2),(10015,10000,1115,NULL,NULL,'45','2025-11-20 00:09:18',2),(10016,10000,1116,NULL,NULL,'65.0','2025-11-20 00:09:18',2),(10017,10000,1117,NULL,NULL,'Tostado uniforme, sin quemaduras.','2025-11-20 00:09:18',2),(10018,10000,1118,NULL,NULL,'4.5','2025-11-20 00:09:18',2),(10019,10000,1119,NULL,NULL,'12','2025-11-20 00:09:18',2),(10020,10000,1120,NULL,NULL,'4.25','2025-11-20 00:09:18',2),(20001,20000,2101,NULL,NULL,'1000','2025-11-20 00:09:31',2),(20002,20000,2102,NULL,NULL,'200','2025-11-20 00:09:31',2),(20003,20000,2103,NULL,NULL,'4','2025-11-20 00:09:31',2),(20004,20000,2104,NULL,NULL,'150','2025-11-20 00:09:31',2),(20005,20000,2105,NULL,NULL,'100','2025-11-20 00:09:31',2),(20006,20000,2106,NULL,NULL,'12.5','2025-11-20 00:09:31',2),(20007,20000,2107,NULL,NULL,'40.0','2025-11-20 00:09:31',2),(20008,20000,2108,NULL,NULL,'35.0','2025-11-20 00:09:31',2),(20009,20000,2109,NULL,NULL,'10','2025-11-20 00:09:31',2),(20010,20000,2110,NULL,NULL,'2.5','2025-11-20 00:09:31',2),(20011,20000,2111,NULL,NULL,'150','2025-11-20 00:09:31',2),(20012,20000,2112,NULL,NULL,'100','2025-11-20 00:09:31',2),(20013,20000,2113,NULL,NULL,'50','2025-11-20 00:09:31',2),(20014,20000,2114,NULL,NULL,'50','2025-11-20 00:09:31',2),(20015,20000,2115,NULL,NULL,'5.0','2025-11-20 00:09:31',2),(20016,20000,2116,NULL,NULL,'10.0','2025-11-20 00:09:31',2),(20017,20000,2117,NULL,NULL,'5.0','2025-11-20 00:09:31',2),(30001,30000,3101,NULL,NULL,'300','2025-11-20 00:09:38',2),(30005,30000,3105,NULL,NULL,'500','2025-11-20 00:09:38',2),(30010,30000,3110,NULL,NULL,'100','2025-11-20 00:09:38',2),(30015,30000,3115,NULL,NULL,'10.0','2025-11-20 00:09:38',2),(30020,30000,3120,NULL,NULL,'Intenso y especiado','2025-11-20 00:09:38',2),(30104,70013,7020,450.00000,NULL,NULL,'2025-12-05 18:30:00',9),(30105,70013,7023,NULL,'2025-12-14 11:00:00',NULL,'2025-12-05 18:35:00',9),(30108,70015,3101,123.00000,NULL,NULL,'2026-01-20 16:52:11',8),(30109,70015,3102,123123.00000,NULL,NULL,'2026-01-20 21:23:35',8),(30110,70015,3103,123.00000,NULL,NULL,'2026-01-20 21:24:33',8),(30111,70015,3104,0.00000,NULL,'1232132','2026-01-09 13:46:51',8),(30112,70015,3105,123.00000,NULL,NULL,'2026-01-20 21:24:36',8),(30113,70015,3106,123.00000,NULL,NULL,'2026-01-20 21:24:38',8),(30114,70015,3107,123123.00000,NULL,NULL,'2026-01-20 21:24:39',8),(30115,70015,3108,123213.00000,NULL,NULL,'2026-01-20 21:24:40',8),(30116,70015,3109,123.00000,NULL,NULL,'2026-01-20 21:25:47',8),(30117,70015,3110,123.00000,NULL,NULL,'2026-01-20 21:25:48',8),(30118,70015,3111,0.00000,NULL,'2222','2026-01-09 13:49:12',8),(30119,70015,3112,12332.00000,NULL,NULL,'2026-01-20 21:24:59',8),(30120,70015,3113,0.00000,NULL,'Observacion test','2026-01-20 21:23:56',8),(30121,70015,3114,0.00000,NULL,'9999','2026-01-09 13:50:05',8),(30122,70015,3116,3123123.00000,NULL,NULL,'2026-01-20 21:24:58',8),(30123,70015,3115,123.00000,NULL,NULL,'2026-01-20 21:24:59',8),(30124,70015,3117,123123.00000,NULL,NULL,'2026-01-20 21:24:58',8),(30125,70015,3118,12323.00000,NULL,NULL,'2026-01-20 21:24:58',8),(30126,70015,3119,12323.00000,NULL,NULL,'2026-01-20 21:24:57',8),(30127,70015,3120,0.00000,NULL,'asdsa','2026-01-09 12:28:33',8),(30128,70016,1101,0.00000,NULL,'2000','2026-01-09 15:38:51',8),(30129,70016,1102,0.00000,NULL,'1800','2026-01-09 15:38:56',8),(30130,70017,3101,NULL,NULL,NULL,'2026-01-21 21:24:38',8),(30131,70017,3102,NULL,NULL,NULL,'2026-01-21 04:19:56',8),(30132,70017,3103,NULL,NULL,NULL,'2026-01-21 04:19:58',8),(30133,70017,3104,123.20000,NULL,NULL,'2026-01-21 04:22:39',8),(30134,70017,3105,1233.00000,NULL,NULL,'2026-01-21 04:31:58',8),(30135,70017,3106,123.00000,NULL,NULL,'2026-01-21 04:30:01',8),(30136,70017,3107,NULL,NULL,NULL,'2026-01-21 04:04:26',8),(30137,70017,3108,NULL,NULL,NULL,'2026-01-21 04:04:24',8),(30138,70017,3109,NULL,NULL,NULL,'2026-01-21 21:24:44',8),(30139,70017,3110,NULL,NULL,NULL,'2026-01-21 04:04:19',8),(30140,70017,3111,NULL,NULL,NULL,'2026-01-21 04:04:17',8),(30141,70017,3112,123123.00000,NULL,NULL,'2026-01-21 04:10:26',8),(30142,70017,3114,1231321312.00000,NULL,NULL,'2026-01-21 21:25:13',8),(30143,70017,3113,NULL,NULL,'1231232323','2026-01-21 21:25:22',8),(30144,70017,3116,NULL,NULL,NULL,'2026-01-21 04:04:09',8),(30145,70017,3115,123.00000,NULL,NULL,'2026-01-21 04:10:33',8),(30146,70017,3117,NULL,NULL,NULL,'2026-01-21 04:04:07',8),(30147,70017,3118,122.99000,NULL,NULL,'2026-01-21 04:06:59',8),(30148,70017,3119,1112.00123,NULL,NULL,'2026-01-21 04:09:18',8),(30149,70018,2104,131232.00000,NULL,NULL,'2026-01-22 06:36:56',8);
/*!40000 ALTER TABLE `respuesta_campo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `respuesta_tabla`
--

DROP TABLE IF EXISTS `respuesta_tabla`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `respuesta_tabla` (
                                   `id_respuesta` bigint NOT NULL AUTO_INCREMENT,
                                   `id_produccion` bigint NOT NULL,
                                   `id_tabla` bigint NOT NULL,
                                   `id_fila` bigint NOT NULL,
                                   `id_columna` bigint NOT NULL,
                                   `creado_por` bigint NOT NULL,
                                   `valor_texto` longtext,
                                   `valor_numerico` decimal(18,5) DEFAULT NULL,
                                   `valor_fecha` datetime DEFAULT NULL,
                                   `timestamp` datetime DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id_respuesta`),
                                   KEY `ix_resp_tabla_creado_por` (`creado_por`),
                                   KEY `idx_prod_tabla_fila_columna` (`id_produccion`,`id_tabla`,`id_fila`,`id_columna`),
                                   KEY `idx_analisis_celda` (`id_tabla`,`id_fila`,`id_columna`),
                                   CONSTRAINT `fk_resp_tabla_usuario` FOREIGN KEY (`creado_por`) REFERENCES `usuario` (`id_usuario`),
                                   CONSTRAINT `fk_respuesta_tabla` FOREIGN KEY (`id_tabla`) REFERENCES `tabla` (`id_tabla`),
                                   CONSTRAINT `respuesta_tabla_ibfk_1` FOREIGN KEY (`id_produccion`) REFERENCES `produccion` (`id_produccion`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31078 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `respuesta_tabla`
--

LOCK TABLES `respuesta_tabla` WRITE;
/*!40000 ALTER TABLE `respuesta_tabla` DISABLE KEYS */;
INSERT INTO `respuesta_tabla` VALUES (1,1,1,1,1,3,'Recuento bacteriano',NULL,NULL,'2025-08-20 22:47:45'),(2,1,1,1,2,3,'100000',NULL,NULL,'2025-08-20 22:47:45'),(3,1,1,1,3,3,'UFC/g',NULL,NULL,'2025-08-20 22:47:45'),(4,1,1,2,1,3,'E. coli',NULL,NULL,'2025-08-20 22:47:45'),(5,1,1,2,2,3,'0',NULL,NULL,'2025-08-20 22:47:45'),(6,1,1,2,3,3,'UFC/g',NULL,NULL,'2025-08-20 22:47:45'),(7,1,1,3,1,3,'Salmonella',NULL,NULL,'2025-08-20 22:47:45'),(8,1,1,3,2,3,'0',NULL,NULL,'2025-08-20 22:47:45'),(9,1,1,3,3,3,'UFC/g',NULL,NULL,'2025-08-20 22:47:45'),(7101,700,5300,1,1,1,'35.0',NULL,NULL,'2025-11-19 23:35:13'),(7102,700,5300,1,2,1,'35.8',NULL,NULL,'2025-11-19 23:35:13'),(7103,700,5300,1,3,1,'S',NULL,NULL,'2025-11-19 23:35:13'),(7201,700,5300,2,1,1,'5.1',NULL,NULL,'2025-11-19 23:35:13'),(7202,700,5300,2,2,1,'5.25',NULL,NULL,'2025-11-19 23:35:13'),(7203,700,5300,2,3,1,'S',NULL,NULL,'2025-11-19 23:35:13'),(7301,700,5300,3,1,1,'28.0',NULL,NULL,'2025-11-19 23:35:13'),(7302,700,5300,3,2,1,'27.5',NULL,NULL,'2025-11-19 23:35:13'),(7303,700,5300,3,3,1,'N',NULL,NULL,'2025-11-19 23:35:13'),(11001,10000,1031,1,1,2,'4.0',NULL,NULL,'2025-11-20 00:09:18'),(11002,10000,1031,1,2,2,'4.1',NULL,NULL,'2025-11-20 00:09:18'),(11003,10000,1031,1,3,2,'OK',NULL,NULL,'2025-11-20 00:09:18'),(11004,10000,1031,2,1,2,'1.1',NULL,NULL,'2025-11-20 00:09:18'),(11005,10000,1031,2,2,2,'1.05',NULL,NULL,'2025-11-20 00:09:18'),(11006,10000,1031,2,3,2,'Densidad baja por exceso de aire',NULL,NULL,'2025-11-20 00:09:18'),(11007,10000,1032,1,1,2,'Dulce/Especiado',NULL,NULL,'2025-11-20 00:09:18'),(11008,10000,1032,1,2,2,'5',NULL,NULL,'2025-11-20 00:09:18'),(11009,10000,1032,1,3,2,'S',NULL,NULL,'2025-11-20 00:09:18'),(11010,10000,1032,2,1,2,'Crujiente/Suave',NULL,NULL,'2025-11-20 00:09:18'),(11011,10000,1032,2,2,2,'4',NULL,NULL,'2025-11-20 00:09:18'),(11012,10000,1032,2,3,2,'S',NULL,NULL,'2025-11-20 00:09:18'),(21001,20000,2031,1,1,2,'45',NULL,NULL,'2025-11-20 00:09:31'),(21002,20000,2031,1,2,2,'5.2',NULL,NULL,'2025-11-20 00:09:31'),(21003,20000,2031,1,3,2,'Activo',NULL,NULL,'2025-11-20 00:09:31'),(21004,20000,2031,2,1,2,'45',NULL,NULL,'2025-11-20 00:09:31'),(21005,20000,2031,2,2,2,'5.1',NULL,NULL,'2025-11-20 00:09:31'),(21006,20000,2031,2,3,2,'Activo',NULL,NULL,'2025-11-20 00:09:31'),(31001,30000,3031,1,2,2,'25.5',NULL,NULL,'2025-11-20 00:09:38'),(31002,30000,3032,2,2,2,'100',NULL,NULL,'2025-11-20 00:09:38'),(31003,30000,3032,2,2,2,'999',NULL,NULL,'2025-11-20 00:15:00'),(31004,30000,3032,2,2,2,'99999999',NULL,NULL,'2025-11-20 00:15:10'),(31011,2,2,5,4,8,'ASDSAD',NULL,NULL,'2025-12-04 21:42:38'),(31066,70015,3031,1,1,8,'1111.001',NULL,NULL,'2026-01-20 21:34:33'),(31067,70015,3031,2,1,8,'11.0012',NULL,NULL,'2026-01-20 21:34:38'),(31068,70015,3031,1,2,8,'11.001',NULL,NULL,'2026-01-20 21:34:37'),(31069,70015,3031,2,2,8,'1111.0012',NULL,NULL,'2026-01-20 21:34:41'),(31070,70015,3032,1,1,8,'1111.00111',NULL,NULL,'2026-01-20 21:34:42'),(31071,70015,3032,1,2,8,'11.0011',NULL,NULL,'2026-01-20 21:34:45'),(31072,70015,3032,2,1,8,'11.001211',NULL,NULL,'2026-01-20 21:34:46'),(31073,70015,3032,2,2,8,'1111.001211',NULL,NULL,'2026-01-20 21:34:48'),(31074,70017,3031,1,1,8,NULL,123.00000,NULL,'2026-01-21 04:05:20'),(31075,70017,3031,1,2,8,'1212',NULL,NULL,'2026-01-21 00:04:06'),(31076,70017,3031,2,1,8,NULL,123.33330,NULL,'2026-01-21 21:24:58'),(31077,70017,3031,2,2,8,'1231',NULL,NULL,'2026-01-21 00:07:02');
/*!40000 ALTER TABLE `respuesta_tabla` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seccion`
--

DROP TABLE IF EXISTS `seccion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seccion` (
                           `id_seccion` bigint NOT NULL AUTO_INCREMENT,
                           `id_version_receta_padre` bigint NOT NULL,
                           `creado_por` bigint NOT NULL,
                           `titulo` varchar(255) NOT NULL,
                           `tipo` enum('simple','agrupada','tabla') NOT NULL,
                           `orden` int NOT NULL DEFAULT '0',
                           PRIMARY KEY (`id_seccion`),
                           UNIQUE KEY `uk_seccion_titulo_version` (`id_version_receta_padre`,`titulo`),
                           UNIQUE KEY `uk_seccion_orden_version` (`id_version_receta_padre`,`orden`),
                           KEY `idx_seccion_version` (`id_version_receta_padre`),
                           KEY `seccion_usuario_fk` (`creado_por`),
                           CONSTRAINT `seccion_usuario_fk` FOREIGN KEY (`creado_por`) REFERENCES `usuario` (`id_usuario`),
                           CONSTRAINT `seccion_version_fk` FOREIGN KEY (`id_version_receta_padre`) REFERENCES `version_receta` (`id_version`)
) ENGINE=InnoDB AUTO_INCREMENT=3031 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seccion`
--

LOCK TABLES `seccion` WRITE;
/*!40000 ALTER TABLE `seccion` DISABLE KEYS */;
INSERT INTO `seccion` VALUES (1,1,1,'Materias Primas','simple',1),(2,1,1,'Parámetros de Proceso','agrupada',2),(3,1,1,'Control de Calidad','tabla',3),(4,2,1,'Ingredientes','simple',1),(5,2,1,'Proceso de Elaboración','agrupada',2),(6,2,1,'Análisis Físico-Químico','tabla',3),(10,10,1,'SEC 1','simple',1),(11,10,1,'SEC 2','tabla',2),(510,500,1,'Materia Prima y Cuajado','simple',1),(520,500,1,'Prensado y Salado','agrupada',2),(530,500,1,'Control de Calidad Final','tabla',3),(1010,1000,1,'Masa y Relleno','agrupada',1),(1020,1000,1,'Procesos de Cocción','simple',2),(1030,1000,1,'Control de Calidad (Tablas)','tabla',3),(2010,2000,1,'Ingredientes Base y Levadura','agrupada',1),(2020,2000,1,'Aditivos y Frutas','agrupada',2),(2030,2000,1,'Control de Fermentación','tabla',3),(2040,2000,1,'Control de Cocción','tabla',4),(3010,3000,1,'Masa e Ingredientes','agrupada',1),(3020,3000,1,'Horneado y Glaseado','simple',2),(3030,3000,1,'Control de Dimensiones','tabla',3);
/*!40000 ALTER TABLE `seccion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tabla`
--

DROP TABLE IF EXISTS `tabla`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tabla` (
                         `id_tabla` bigint NOT NULL AUTO_INCREMENT,
                         `id_seccion` bigint NOT NULL,
                         `nombre` varchar(255) NOT NULL,
                         `descripcion` varchar(255) DEFAULT NULL,
                         `orden` int NOT NULL,
                         PRIMARY KEY (`id_tabla`),
                         UNIQUE KEY `uk_tabla_nombre_seccion` (`id_seccion`,`nombre`),
                         KEY `id_seccion` (`id_seccion`),
                         CONSTRAINT `tabla_ibfk_1` FOREIGN KEY (`id_seccion`) REFERENCES `seccion` (`id_seccion`)
) ENGINE=InnoDB AUTO_INCREMENT=5301 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tabla`
--

LOCK TABLES `tabla` WRITE;
/*!40000 ALTER TABLE `tabla` DISABLE KEYS */;
INSERT INTO `tabla` VALUES (1,3,'Análisis Microbiológico',NULL,1),(2,6,'Especificaciones Finales',NULL,1),(1031,1030,'Mediciones Masa','Análisis de la masa cruda.',1),(1032,1030,'Análisis Final','Mediciones organolépticas.',2),(2031,2030,'Fermentación Primaria','Control de leudado inicial.',1),(2041,2040,'Control de Temperatura Interna','Medición al final de la cocción.',1),(3031,3030,'Medición Budín','Largo y Ancho',1),(3032,3030,'Medición Glaseado','Grosor y Cobertura',2),(5300,530,'Análisis de Lote','Mediciones obligatorias del queso finalizado.',1);
/*!40000 ALTER TABLE `tabla` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
                           `id_usuario` bigint NOT NULL AUTO_INCREMENT,
                           `email` varchar(100) NOT NULL,
                           `nombre` varchar(100) NOT NULL,
                           `rol` enum('ADMIN','OPERADOR') NOT NULL DEFAULT 'OPERADOR',
                           `esta_activo` tinyint(1) DEFAULT '1',
                           `password` varchar(60) NOT NULL,
                           PRIMARY KEY (`id_usuario`),
                           UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'email1@mail.com','Nombre','ADMIN',1,'pass1'),(2,'supervisor@alimtrack.com','Supervisor Producción','OPERADOR',1,'$2a$10$rOzJZ/'),(3,'operador1@alimtrack.com','Operador Línea 1','OPERADOR',1,'$2a$10$rOzJZ/'),(8,'admin55@alimtrack.com','nombre admin 55','OPERADOR',1,'$2a$10$B/xyltXLabmEvd1GU5MbKe9o4paoNYX2OtlH/gTEgIlwevpU957Iq'),(10,'admin22@alimtrack.com','nombre admin 22','OPERADOR',1,'$2a$10$AC6F1b0/64iS./UagYzDdeyZ2Fn.8Opg0xME1bFD5sTJVnY8NVGH2');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `version_receta`
--

DROP TABLE IF EXISTS `version_receta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `version_receta` (
                                  `id_version` bigint NOT NULL AUTO_INCREMENT,
                                  `id_receta_padre` bigint NOT NULL,
                                  `codigo_version_receta` varchar(255) NOT NULL,
                                  `creado_por` bigint NOT NULL,
                                  `nombre` varchar(255) NOT NULL,
                                  `descripcion` varchar(255) DEFAULT NULL,
                                  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id_version`),
                                  UNIQUE KEY `codigo_version_receta` (`codigo_version_receta`),
                                  KEY `id_receta` (`id_receta_padre`),
                                  KEY `fk_versionreceta_creado_por` (`creado_por`),
                                  CONSTRAINT `fk_versionreceta_creado_por` FOREIGN KEY (`creado_por`) REFERENCES `usuario` (`id_usuario`),
                                  CONSTRAINT `version_receta_ibfk_1` FOREIGN KEY (`id_receta_padre`) REFERENCES `receta` (`id_receta`)
) ENGINE=InnoDB AUTO_INCREMENT=3001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `version_receta`
--

LOCK TABLES `version_receta` WRITE;
/*!40000 ALTER TABLE `version_receta` DISABLE KEYS */;
INSERT INTO `version_receta` VALUES (1,1,'VV-YOG-001-1.0',1,'Versión 1.0','Versión inicial yogurt griego','2025-08-20 22:47:45'),(2,2,'VV-QUE-001-1.0',1,'Versión 1.0','Versión inicial mozzarella','2025-08-20 22:47:45'),(10,1,'TEST',1,'TEST','TEST DESC','2025-11-20 00:09:38'),(500,50,'PARM_V1.1',1,'Versión 1.1 - Estándar Añejo','Ajustes en el tiempo de prensado y salado.','2025-11-19 23:35:13'),(1000,100,'TARTA_V2.0',1,'Versión 2.0 - Masa Crujiente','Ajuste de grasas y temperatura de horneado.','2025-11-20 00:09:18'),(2000,200,'PANDUL_V1.0',1,'Versión 1.0 - Frutas y Nueces','Receta de alto rendimiento con levadura natural.','2025-11-20 00:09:31'),(3000,300,'BUDIN_V1.5',1,'Versión 1.5 - Glaseado y Frutas','Ajustes de humedad.','2025-11-20 00:09:38');
/*!40000 ALTER TABLE `version_receta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'db_alimtrack_prod'
--

--
-- Dumping routines for database 'db_alimtrack_prod'
--
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-23 14:39:16
