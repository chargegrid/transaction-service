CREATE TABLE IF NOT EXISTS "transactions" (
  "id"         UUID,
  "tenant_id"  UUID NOT NULL,
  "evse_id"    TEXT NOT NULL,
  "started_at" TIMESTAMP WITH TIME ZONE,
  "ended_at"   TIMESTAMP WITH TIME ZONE,
  "volume"     DOUBLE PRECISION,
  "price"      NUMERIC(12,2),
  "user_id"    TEXT,
  PRIMARY KEY ("id")
);
--;;
