USE project_manager:

INSERT INTO employee (position, mail, firstName, lastName) VALUES
('Manager', 'anders.nielsen@gmail.com
', 'Anders', 'Nielsen'),
('Udvikler', 'sofie.jensen@yahoo.com
', 'Sofie', 'Jensen'),
('Designer', 'mads.hansen@gmail.com
', 'Mads', 'Hansen'),
('Analytiker', 'freja.pedersen@gmail.com
', 'Freja', 'Pedersen'),
('Support', 'rasmus.larsen@yahoo.com
', 'Rasmus', 'Larsen'),
('Udvikler', 'ida.sorensen@hotmail.com
', 'Ida', 'Sorensen'),
('Tester', 'jonas.christensen@gmail.com
', 'Jonas', 'Christensen'),
('Udvikler', 'laura.madsen@yahoo.com
', 'Laura', 'Madsen'),
('Manager', 'thomas.kristensen@gmail.com
', 'Thomas', 'Kristensen'),
('Udvikler', 'emma.andersen@gmail.com
', 'Emma', 'Andersen'),
('Analytiker', 'kasper.olsen@yahoo.com
', 'Kasper', 'Olsen'),
('Designer', 'line.moller@gmail.com
', 'Line', 'Moller'),
('Udvikler', 'henrik.jeppesen@yahoo.com
', 'Henrik', 'Jeppesen'),
('Support', 'sara.krogh@gmail.com
', 'Sara', 'Krogh'),
('Udvikler', 'martin.dam@gmail.com
', 'Martin', 'Dam'),
('Analytiker', 'amalie.bonde@yahoo.com
', 'Amalie', 'Bonde'),
('Udvikler', 'lars.holm@gmail.com
', 'Lars', 'Holm'),
('Designer', 'mia.friis@yahoo.com
', 'Mia', 'Friis'),
('Support', 'jens.lauritsen@gmail.com
', 'Jens', 'Lauritsen'),
('Tester', 'katrine.bech@hotmail.com
', 'Katrine', 'Bech'),
('Udvikler', 'simon.birk@gmail.com
', 'Simon', 'Birk'),
('Analytiker', 'maria.jespersen@yahoo.com
', 'Maria', 'Jespersen'),
('Manager', 'peter.rahbek@gmail.com
', 'Peter', 'Rahbek'),
('Support', 'lone.lykke@yahoo.com
', 'Lone', 'Lykke'),
('Udvikler', 'nikolaj.steen@gmail.com
', 'Nikolaj', 'Steen'),
('Designer', 'camilla.hvid@yahoo.com
', 'Camilla', 'Hvid'),
('Udvikler', 'victor.thygesen@gmail.com
', 'Victor', 'Thygesen'),
('Analytiker', 'karoline.bjerg@yahoo.com
', 'Karoline', 'Bjerg'),
('Tester', 'oliver.kure@gmail.com
', 'Oliver', 'Kure'),

('Udvikler', 'li.wei@gmail.com
', 'Li', 'Wei'),
('Designer', 'chen.lina@yahoo.com
', 'Chen', 'Lina'),

('Support', 'samal.nurpeiis@gmail.com
', 'Samal', 'Nurpeiis'),
('Udvikler', 'arkan.kadyrov@yahoo.com
', 'Arkan', 'Kadyrov'),

('Analytiker', 'oleksii.bondarenko@gmail.com
', 'Oleksii', 'Bondarenko'),

('Tester', 'igor.morozov@hotmail.com
', 'Igor', 'Morozov'),

('Udvikler', 'mael.kerdudo@gmail.com
', 'Mael', 'Kerdudo'),

('Designer', 'tobias.bechholm@gmail.com
', 'Tobias', 'Bechholm'),
('Support', 'rebecca.halberg@yahoo.com
', 'Rebecca', 'Halberg'),
('Udvikler', 'magnus.riis@gmail.com
', 'Magnus', 'Riis'),
('Analytiker', 'astrid.lykkeberg@yahoo.com
', 'Astrid', 'Lykkeberg'),
('Tester', 'sebastian.hove@gmail.com
', 'Sebastian', 'Hove'),
('Udvikler', 'lea.hartvig@yahoo.com
', 'Lea', 'Hartvig'),
('Support', 'mathias.bechmann@gmail.com
', 'Mathias', 'Bechmann'),
('Designer', 'nanna.lilholt@yahoo.com
', 'Nanna', 'Lilholt'),
('Udvikler', 'ralle.skovgaard@gmail.com
', 'Ralle', 'Skovgaard'),
('Analytiker', 'stine.haugaard@gmail.com
', 'Stine', 'Haugaard');

INSERT INTO account(role, password, emp_id) VALUES
(1, 'Ostesovs3000', 9),
(1, 'TommyWieseau200' 1),
(2, 'MischeviousFig' 2),
(2, 'SilentHarbor42', 4),
(2, 'NordicPineTrail', 6),
(2, 'AzureLantern88', 8),
(2, 'WinterMothGlass', 10),
(2, 'CrimsonFieldNote', 11),
(2, 'HollowStream94', 15),
(2, 'SilverGroveLine', 46),
(2, 'AutumnRidge17', 42),
(2, 'WillowChord59', 40);

INSERT INTO project(name, start_date, end_date) VALUES
('Shopify.com WebPage Adjustment', 2025-03-23, 2025-07-10),
('CDON.com User-payment fixes', 2024-04-22, 2024-08-29);

INSERT INTO subproject(name, start_date, end_date) VALUES
('Change search options bar to include new product categories', '2025-03-24', '2025-04-15'),
('Optimize checkout workflow for mobile users', '2025-04-16', '2025-05-10'),
('Fix user authentication errors during payment', '2024-04-23', '2024-05-12'),
('Add new payment gateway options and test', '2024-05-13', '2024-06-20'),
('Improve page load speed on product detail pages', '2025-05-11', '2025-06-01'),
('Revise shipping calculation logic for international orders', '2024-06-21', '2024-07-15');

INSERT INTO task(name, start_date, end_date, hours) VALUES
('Update search bar HTML and CSS', '2025-03-24', '2025-03-28', 12),
('Add product category dropdown', '2025-03-29', '2025-04-02', 16),
('Integrate category filtering logic', '2025-04-03', '2025-04-10', 24),
('Unit test search functionality', '2025-04-11', '2025-04-15', 8),
('Redesign mobile checkout layout', '2025-04-16', '2025-04-20', 14),
('Optimize checkout page scripts', '2025-04-21', '2025-04-27', 18),
('Conduct mobile browser testing', '2025-04-28', '2025-05-02', 10),
('Adjust payment form validation', '2025-05-03', '2025-05-10', 12),
('Fix login session errors', '2024-04-23', '2024-04-28', 10),
('Update authentication API calls', '2024-04-29', '2024-05-05', 14),
('End-to-end test payment workflow', '2024-05-06', '2024-05-12', 12),
('Integrate new payment gateway', '2024-05-13', '2024-05-25', 20),
('Test payment gateway transactions', '2024-05-26', '2024-06-10', 16),
('Verify error handling on failed payments', '2024-06-11', '2024-06-20', 8),
('Optimize product page images', '2025-05-11', '2025-05-20', 10),
('Minify CSS and JavaScript for speed', '2025-05-21', '2025-05-25', 12),
('Implement caching for product data', '2025-05-26', '2025-05-30', 14),
('Revise shipping calculation formulas', '2024-06-21', '2024-07-01', 16),
('Test international shipping scenarios', '2024-07-02', '2024-07-10', 12),
('Update shipping error messages', '2024-07-11', '2024-07-15', 8);

