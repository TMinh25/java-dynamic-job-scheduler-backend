SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for task_types
-- ----------------------------
DROP TABLE IF EXISTS `task_types`;
CREATE TABLE `task_types`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `class_name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `type` enum('SYSTEM','CUSTOM','INTEGRATION','MANUAL') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of task_types
-- ----------------------------
INSERT INTO `task_types` VALUES (1, 'BatchTicketCreation', 'Ticket Creation', 'MANUAL');
INSERT INTO `task_types` VALUES (2, 'DataSynchronization', 'Data Synchronization', 'MANUAL');
INSERT INTO `task_types` VALUES (3, 'BatchTicketCreation', 'Batch Ticket Creation', 'SYSTEM');

SET FOREIGN_KEY_CHECKS = 1;
