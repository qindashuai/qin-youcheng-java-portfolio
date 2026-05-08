USE `enterprise_rag`;

INSERT INTO `knowledge_base` (`id`, `name`, `description`, `category`, `document_count`, `status`) VALUES
(1, '内部制度库', '公司内部规章制度、管理办法等文档', 'POLICY', 0, 1),
(2, '故障排查库', '系统故障排查指南、运维手册', 'FAULT', 0, 1),
(3, '业务流程库', '业务操作流程、审批流程文档', 'PROCESS', 0, 1);
