-- V5__fix_handoff_triggers_type.sql
-- Fix handoff_triggers column type from TEXT[] to JSONB

ALTER TABLE agent_configs
ALTER COLUMN handoff_triggers TYPE JSONB
USING COALESCE(to_jsonb(handoff_triggers), '[]'::jsonb);
