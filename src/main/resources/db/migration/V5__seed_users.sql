-- Test users for the sales agents already present in the credits seed data.
-- Password for all three is "password123" (bcrypt-hashed), for technical-test purposes only.
INSERT INTO users (username, password_hash, full_name) VALUES
    ('carlos.ramirez', '$2a$10$SsPSviiICiuwy3NPHu1moeZHTNXzJ8Vb9CvWvumoeDmo1DnDGR6rO', 'Carlos Ramirez'),
    ('laura.jimenez', '$2a$10$XfATTBJVjoY7x66hhuuT7u3k5nKl6ALkeZaEuzjk/8C9X.JXkq3O.', 'Laura Jimenez'),
    ('andres.torres', '$2a$10$OkqvGuuh5Y7HSsT5vztN8e8FUXgCt4fqEhR49jN42u9vDWL4cBh12', 'Andres Torres');
