/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50610
Source Host           : localhost:3306
Source Database       : hollowrpg

Target Server Type    : MYSQL
Target Server Version : 50610
File Encoding         : 65001

Date: 2013-04-15 01:20:50
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `active_quests`
-- ----------------------------
DROP TABLE IF EXISTS `active_quests`;
CREATE TABLE `active_quests` (
  `player_name` varchar(255) DEFAULT NULL,
  `quest_id` int(11) DEFAULT NULL,
  `counter` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of active_quests
-- ----------------------------
INSERT INTO `active_quests` VALUES ('cherbert', '2', '2');
INSERT INTO `active_quests` VALUES ('cherbert', '1', '3');

-- ----------------------------
-- Table structure for `npcs`
-- ----------------------------
DROP TABLE IF EXISTS `npcs`;
CREATE TABLE `npcs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `npc_name` varchar(255) DEFAULT NULL,
  `greeting_text` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of npcs
-- ----------------------------
INSERT INTO `npcs` VALUES ('1', 'Cubeydoom', 'Well Met!');
INSERT INTO `npcs` VALUES ('2', 'sherbzz', 'Greetings traveller!');

-- ----------------------------
-- Table structure for `quests`
-- ----------------------------
DROP TABLE IF EXISTS `quests`;
CREATE TABLE `quests` (
  `quest_id` int(11) NOT NULL AUTO_INCREMENT,
  `npc_id` int(11) DEFAULT NULL,
  `quest_name` varchar(255) DEFAULT NULL,
  `quest_detail` text,
  `confirm_text` varchar(255) DEFAULT NULL,
  `x` int(11) DEFAULT NULL,
  `y` int(11) DEFAULT NULL,
  `z` int(11) DEFAULT NULL,
  `objective_type` int(11) DEFAULT NULL,
  `objective_target_npc` varchar(255) DEFAULT NULL,
  `objective_entity` varchar(255) DEFAULT NULL,
  `objective_count` int(11) DEFAULT NULL,
  `objective_item_reward` varchar(255) DEFAULT NULL,
  `objective_money_reward` int(11) DEFAULT NULL,
  `objective_reward_achievement` int(11) DEFAULT NULL,
  `objective_text_reward` text,
  `is_repeatable` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`quest_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of quests
-- ----------------------------
INSERT INTO `quests` VALUES ('1', '2', 'In search of Herion', 'Hello friend. Let me take you across the ocean to the grand city of Daggerfall! Type the word *YELLOWtravel *WHITEto accept this voyage.', 'Off we go!', '134', '64', '139', '2', null, 'cow', '3', null, null, null, null, null);
INSERT INTO `quests` VALUES ('2', '1', 'Kill 4 Chickens', 'Kill me 4 chickens! Type *YELLOWaccept *WHITEto accept this challenge!', 'Quest Accepted. Use *YELLOW/my quests*WHITE to see your active quests.', null, null, null, '2', null, 'chicken', '2', null, null, null, null, null);
