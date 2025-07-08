-- Baseline migration for existing database
-- This migration handles the case where tables already exist from Hibernate DDL auto-generation

-- This is a baseline migration that does nothing but establishes the starting point
-- for Flyway migrations when tables already exist

SELECT 1; -- No-op statement to make this a valid SQL file
