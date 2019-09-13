/*
 Navicat Premium Data Transfer

 Source Server         : 本地mysql
 Source Server Type    : MySQL
 Source Server Version : 80016
 Source Host           : localhost:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 80016
 File Encoding         : 65001

 Date: 12/09/2019 17:20:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `MSG_ID` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `USER_NAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `MSG` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `LRRQ` date NULL DEFAULT NULL,
  `YXBZ` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`MSG_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES ('1000000001', 'dada', '新婚快乐！！！！', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000002', 'ddd', '永结同心！！！', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000003', 'fff', '欢声，笑声，声声都喜悦；新郎，新娘，新人真幸福！', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000004', 'ddd', '新婚同祝愿，百年好合同与共。', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000005', 'ss', '一对神仙眷侣，两颗白首同心', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000006', 'aa', '花好月圆，喜事连连', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000007', 'dd', '新郎，新娘，新人真幸福！', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000008', 'ggg', '祝你们山盟永在、海誓长存！', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000009', 'dd', '哈哈,恭喜恭喜,新婚大吉!', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000010', 'ss', '衷心的祝福你们:心心相印,相亲相爱!', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000011', 'x', '今朝已定百年好，愿祝新人共白首！', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000012', 'd', '恩爱新人盟白首，缠绵情意缔良缘', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000013', 'ff', '愿你们两情长长又久久，朝朝暮暮爱都有！', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000014', 'ddd', '愿君幸福美满长长久，如似一江春水永不竭！', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000015', 'xxx', '痴心人，天不负，今日新婚，祝爱情永固百年好！', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000016', 'ssss', '此后人生影成双，有甘有苦同共济。新婚快乐!', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000017', 'eeee', '心心相印,相亲相爱!', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000018', 'ccc', '一生情、一世爱、一对鸳鸯浴爱河！', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000019', 'eee', '一对神仙眷侣，两颗白首同心', '2019-08-21', 'Y');
INSERT INTO `message` VALUES ('1000000020', 'aaa', '祝福你们永远互敬互爱，甜美幸福！', '2019-08-21', 'Y');

SET FOREIGN_KEY_CHECKS = 1;
