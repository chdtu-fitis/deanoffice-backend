ALTER TABLE grade ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

CREATE FUNCTION update_grade_timestamp()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_timestamp
    BEFORE UPDATE ON grade
    FOR EACH ROW
    EXECUTE FUNCTION update_grade_timestamp();
