-- V6__fix_product_array_columns.sql
-- Fix tags and ai_keywords columns from TEXT[] to JSONB for Hibernate compatibility

ALTER TABLE products
ALTER COLUMN tags TYPE JSONB
USING COALESCE(to_jsonb(tags), '[]'::jsonb);

ALTER TABLE products
ALTER COLUMN ai_keywords TYPE JSONB
USING COALESCE(to_jsonb(ai_keywords), '[]'::jsonb);
