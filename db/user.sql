/*
 Navicat Premium Data Transfer

 Source Server         : fs_1
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : universe

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 05/12/2023 10:31:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow`  (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                           `userId` bigint UNSIGNED NOT NULL COMMENT '用户id',
                           `followUserId` bigint UNSIGNED NOT NULL COMMENT '关联的用户id',
                           `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for im
-- ----------------------------
DROP TABLE IF EXISTS `im`;
CREATE TABLE `im`  (
                       `id` bigint NOT NULL AUTO_INCREMENT,
                       `toId` bigint NULL DEFAULT NULL COMMENT '对象id',
                       `uid` bigint NULL DEFAULT NULL COMMENT '用户id',
                       `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
                       `avatarUrl` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户头像',
                       `profile` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人简介',
                       `text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息内容',
                       `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                       `img` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片',
                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
                         `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
                         `userId` bigint UNSIGNED NOT NULL COMMENT '用户id',
                         `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
                         `avatarUrl` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '探店的照片，最多9张，多张以\",\"隔开',
                         `content` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '探店的文字描述',
                         `liked` int UNSIGNED NULL DEFAULT 0 COMMENT '点赞数量',
                         `comments` int UNSIGNED NULL DEFAULT NULL COMMENT '评论数量',
                         `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for post_comment
-- ----------------------------
DROP TABLE IF EXISTS `post_comment`;
CREATE TABLE `post_comment`  (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论id',
                                 `userId` bigint NOT NULL COMMENT '评论用户id',
                                 `postId` bigint NOT NULL COMMENT '评论帖子id',
                                 `content` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容(最大200字)',
                                 `pid` bigint NOT NULL COMMENT '父id',
                                 `commentState` int NOT NULL DEFAULT 0 COMMENT '状态 0 正常',
                                 `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for team
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team`  (
                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '队伍名称',
                         `place` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组队地点',
                         `announce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '队伍公告',
                         `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
                         `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '队伍标签',
                         `avatarUrl` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '队伍头像',
                         `maxNum` int NOT NULL DEFAULT 1 COMMENT '最大人数',
                         `expireTime` datetime NULL DEFAULT NULL COMMENT '过期时间',
                         `userId` bigint NULL DEFAULT NULL COMMENT '用户id（队长 id）',
                         `status` int NOT NULL DEFAULT 0 COMMENT '0 - 公开，1 - 私有，2 - 加密',
                         `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
                         `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '队伍' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                         `userAccount` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '账号',
                         `avatarUrl` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
                         `gender` tinyint NULL DEFAULT 2 COMMENT '0-男 1-女 2-未知',
                         `profile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人简介',
                         `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签属性',
                         `userPassword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                         `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                         `x` double NULL DEFAULT NULL COMMENT '经度',
                         `y` double NULL DEFAULT NULL COMMENT '纬度',
                         `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                         `userStatus` int(10) UNSIGNED ZEROFILL NOT NULL DEFAULT 0000000000,
                         `createTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                         `updateTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                         `isDelete` tinyint NULL DEFAULT 0,
                         `userRole` int NULL DEFAULT 0 COMMENT '0-普通用户，1-管理员',
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1636040720355910629 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_team
-- ----------------------------
DROP TABLE IF EXISTS `user_team`;
CREATE TABLE `user_team`  (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                              `userId` bigint NULL DEFAULT NULL COMMENT '用户id',
                              `teamId` bigint NULL DEFAULT NULL COMMENT '队伍id',
                              `joinTime` datetime NULL DEFAULT NULL COMMENT '加入时间',
                              `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户队伍关系' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
