-- Insert roles with created_at and updated_at timestamps
INSERT INTO roles (id, name, created_at, updated_at) VALUES
('11111111-1111-1111-1111-111111111111', 'ADMIN', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('22222222-2222-2222-2222-222222222222', 'USER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- Insert users with the correct columns
INSERT INTO users (id, username, email, oauth_id, oauth_provider, permission, profile_description, profile_picture_url, created_at, updated_at) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'admin', 'admin@example.com', NULL, NULL, 'ADMIN', 'Administrator account', 'https://example.com/admin.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'user', 'user@example.com', NULL, NULL, 'USER', 'Regular user account', 'https://example.com/user.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- Insert some skills
INSERT INTO skills (id, name, description, created_at, updated_at) VALUES
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Java', 'Java programming language', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Spring Boot', 'Spring Boot framework', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- Assign roles to users - use the correct column names
INSERT INTO roles_users (roles_id, users_id) VALUES
('11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb');

-- Assign skills to users - use the correct column names
INSERT INTO user_skills (skill_id, user_id) VALUES
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb');