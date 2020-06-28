CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `tel` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '电话号码',
  `avatar` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '头像地址',
  `password` varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '密码',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_tel` (`tel`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;