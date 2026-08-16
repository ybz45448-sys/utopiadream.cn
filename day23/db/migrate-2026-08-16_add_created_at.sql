-- ============================================================
-- 迁移脚本：topics/comments 用真实时间戳替换硬编码 time 列
-- 背景：time 列原来是 VARCHAR，创建时被写死成"刚刚"，
--       永远不更新。改成 created_at 时间戳，前端再算相对时间。
-- 幂等：可以重复执行；已有 created_at 时是 no-op，
--       首次执行会给老行回填"迁移执行时刻"。
-- 用法：在 postgres 容器里执行：
--   docker exec -i utopia-postgres psql -U postgres -d utopia < 本文件
-- ============================================================

-- 1. topics 增加 created_at，老数据回填为当前时间
ALTER TABLE topics
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- 2. 删掉 topics 的 time 列
--    （先加后删，顺序不能反：保证中途任何时刻字段都不缺失）
ALTER TABLE topics DROP COLUMN IF EXISTS time;

-- 3. comments 同样处理
ALTER TABLE comments
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE comments DROP COLUMN IF EXISTS time;
