/*
 Navicat Premium Data Transfer

 Source Server         : aliyun
 Source Server Type    : MySQL
 Source Server Version : 50631
 Source Host           : 101.200.147.13
 Source Database       : rooster

 Target Server Type    : MySQL
 Target Server Version : 50631
 File Encoding         : utf-8

 Date: 02/08/2017 11:44:59 AM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `rst_failed`
-- ----------------------------
DROP TABLE IF EXISTS `rst_failed`;
CREATE TABLE `rst_failed` (
  `id` int(9) NOT NULL AUTO_INCREMENT,
  `uniquelyID` varchar(200) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `failedInfo` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `rst_task`
-- ----------------------------
DROP TABLE IF EXISTS `rst_task`;
CREATE TABLE `rst_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uniquelyID` varchar(255) DEFAULT NULL,
  `triggerMode` varchar(255) DEFAULT NULL,
  `triggerTime` varchar(255) DEFAULT NULL,
  `callbackURL` varchar(255) DEFAULT NULL,
  `callbackMethod` varchar(255) DEFAULT NULL,
  `callbackData` varchar(500) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
