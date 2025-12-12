-- Add conversation_id to whatsapp_orders to link orders created from AI conversations
ALTER TABLE whatsapp_orders ADD COLUMN IF NOT EXISTS conversation_id UUID;

-- Add index for faster lookup
CREATE INDEX IF NOT EXISTS idx_whatsapp_orders_conversation_id ON whatsapp_orders(conversation_id);

-- Add foreign key reference (optional, if you want referential integrity)
-- ALTER TABLE whatsapp_orders ADD CONSTRAINT fk_whatsapp_orders_conversation
--     FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE SET NULL;
