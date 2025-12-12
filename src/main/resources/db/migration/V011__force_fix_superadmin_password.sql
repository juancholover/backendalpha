-- V011: Force update super admin password hash
-- This migration ensures the correct BCrypt hash is set regardless of current value
-- Reason: V010 had a condition that prevented update in some cases

-- Password: SuperAdmin2025!
-- BCrypt hash: $2a$10$4CXnmWn5Z9bquEI5qW3k8O/mOHIyutLauNACSfiuPv2pBJbHRqWVG

UPDATE auth_usuario 
SET password_hash = '$2a$10$4CXnmWn5Z9bquEI5qW3k8O/mOHIyutLauNACSfiuPv2pBJbHRqWVG',
    updated_at = NOW(),
    updated_by = 'V011_migration'
WHERE id = 1;
