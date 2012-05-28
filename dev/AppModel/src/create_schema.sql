-- MySQL dump 10.13  Distrib 5.1.59, for apple-darwin10.3.0 (i386)
--
-- Host: localhost    Database: races
-- ------------------------------------------------------
-- Server version	5.1.59

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


DROP DATABASE IF EXISTS `races`;
CREATE DATABASE `races`;
CONNECT `races`;


--
-- grant tables (:
--

grant all privileges on races.* to 'dbadmin' identified by 'dba-racer';


--
-- Table structure for table `auto`
--
DROP TABLE IF EXISTS `auto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auto` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `patente` varchar(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `carrera`
--

DROP TABLE IF EXISTS `carrera`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `carrera` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `carril_carrera`
--

DROP TABLE IF EXISTS `carril_carrera`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `carril_carrera` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `numero_carril` int(10) unsigned NOT NULL,
  `id_carrera` int(10) unsigned NOT NULL,
  `id_inscripto_competencia` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_carrera` (`id_carrera`),
  CONSTRAINT `FK_carrera` FOREIGN KEY (`id_carrera`) REFERENCES `carrera` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categoria` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(40) NOT NULL,
  `tiempo_maximo` decimal(10,3) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `competencia`
--

DROP TABLE IF EXISTS `competencia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `competencia` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tipo_competicion` int(10) unsigned NOT NULL,
  `numero_ronda` int(11) NOT NULL,
  `id_torneo` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `corredor`
--

DROP TABLE IF EXISTS `corredor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `corredor` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `dni` int(8) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `corredor_auto`
--

DROP TABLE IF EXISTS `corredor_auto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `corredor_auto` (
  `id_corredor` int(10) unsigned NOT NULL,
  `id_auto` int(10) unsigned NOT NULL,
  `numero_designado` int(10) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id_corredor`,`id_auto`),
  KEY `numero_designado` (`numero_designado`),
  KEY `FK_auto` (`id_auto`),
  CONSTRAINT `FK_auto` FOREIGN KEY (`id_auto`) REFERENCES `auto` (`id`),
  CONSTRAINT `FK_corredor` FOREIGN KEY (`id_corredor`) REFERENCES `corredor` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inscripto`
--

DROP TABLE IF EXISTS `inscripto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `inscripto` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_corredor` int(10) unsigned NOT NULL,
  `id_auto` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inscripto_competencia`
--

DROP TABLE IF EXISTS `inscripto_competencia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `inscripto_competencia` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_competencia` int(10) unsigned NOT NULL,
  `id_categoria` int(10) unsigned NOT NULL,
  `numero_generado` int(10) unsigned NOT NULL,
  `id_inscripto` int(10) unsigned NOT NULL,
  `estado` int(10) unsigned NOT NULL,
  `estado_competencia` int(10) unsigned NOT NULL,
  `rondas_restantes` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tiempo`
--

DROP TABLE IF EXISTS `tiempo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tiempo` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tipo_tiempo` int(10) unsigned NOT NULL,
  `tiempo` decimal(7,3) unsigned NOT NULL,
  `id_carril_carrera` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tipo_tiempo`
--

DROP TABLE IF EXISTS `tipo_tiempo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tipo_tiempo` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  `posicion` int(11) NOT NULL,
  `habilitado` tinyint(1) NOT NULL,
  `decisorio` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `torneo`
--

DROP TABLE IF EXISTS `torneo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `torneo` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `fecha_hora` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-03-15 20:59:56


-- data necesaria para usar el sistema
INSERT INTO categoria VALUES
(null,'13.5','13.5'),
(null,'13.0','13.0'),
(null,'12.5','12.5'),
(null,'12.0','12.0'),
(null,'11.5','11.5'),
(null,'11.0','11.0'),
(null,'10.5','10.5'),
(null,'10.0','10.0'),
(null,'9.5','9.5'),
(null,'9.0','9.0'),
(null,'Libre','8.5'),
(12,'Sin Categoria Asignada','-1');


insert into tipo_tiempo values 
(1,'Tiempo Reaccion', 1,1,0),
(2,'Tiempo 100MTS', 2,1,0),
(3,'Tiempo Fin', 3,1,1);
