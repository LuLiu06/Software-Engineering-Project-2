-- Shopping Cart Database Initialization Script
-- Creates database, tables, and inserts localization data

CREATE DATABASE IF NOT EXISTS shopping_cart_localization
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shopping_cart_localization;

-- Create cart_records table
CREATE TABLE IF NOT EXISTS cart_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    total_items INT NOT NULL,
    total_cost DOUBLE NOT NULL,
    language VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create cart_items table with foreign key relationship
CREATE TABLE IF NOT EXISTS cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cart_record_id INT,
    item_number INT NOT NULL,
    price DOUBLE NOT NULL,
    quantity INT NOT NULL,
    subtotal DOUBLE NOT NULL,
    FOREIGN KEY (cart_record_id) REFERENCES cart_records(id) ON DELETE CASCADE
);

-- Create localization_strings table
CREATE TABLE IF NOT EXISTS localization_strings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    `key` VARCHAR(100) NOT NULL,
    value VARCHAR(255) NOT NULL,
    language VARCHAR(10) NOT NULL,
    UNIQUE KEY unique_key_lang (`key`, language)
);

-- Clear existing localization data
DELETE FROM localization_strings;

-- Insert English (en_US) localization strings
INSERT INTO localization_strings (`key`, value, language) VALUES
('welcome', 'Welcome to the Shopping Cart Application!', 'en_US'),
('enter.item.count', 'Enter the number of items to purchase: ', 'en_US'),
('enter.price', 'Enter the price for item: ', 'en_US'),
('enter.quantity', 'Enter the quantity for item: ', 'en_US'),
('item', 'Item', 'en_US'),
('item.cost', 'Item cost:', 'en_US'),
('total.cost', 'Total cost:', 'en_US'),
('thank.you', 'Thank you for shopping with us!', 'en_US'),
('error.positive.number', 'Please enter a positive number: ', 'en_US'),
('error.invalid.number', 'Invalid input. Please enter a valid number: ', 'en_US'),
('cart.saved', 'Shopping cart saved to database!', 'en_US'),
('cart.save.error', 'Error saving cart to database.', 'en_US');

-- Insert Finnish (fi_FI) localization strings
INSERT INTO localization_strings (`key`, value, language) VALUES
('welcome', 'Tervetuloa ostoskorisovellukseen!', 'fi_FI'),
('enter.item.count', 'Syötä ostettavien tuotteiden määrä: ', 'fi_FI'),
('enter.price', 'Syötä tuotteen hinta: ', 'fi_FI'),
('enter.quantity', 'Syötä tuotteen määrä: ', 'fi_FI'),
('item', 'Tuote', 'fi_FI'),
('item.cost', 'Tuotteen hinta:', 'fi_FI'),
('total.cost', 'Kokonaishinta:', 'fi_FI'),
('thank.you', 'Kiitos ostoksistasi!', 'fi_FI'),
('error.positive.number', 'Syötä positiivinen luku: ', 'fi_FI'),
('error.invalid.number', 'Virheellinen syöte. Syötä kelvollinen numero: ', 'fi_FI'),
('cart.saved', 'Ostoskori tallennettu tietokantaan!', 'fi_FI'),
('cart.save.error', 'Virhe tallennettaessa ostoskoria tietokantaan.', 'fi_FI');

-- Insert Swedish (sv_SE) localization strings
INSERT INTO localization_strings (`key`, value, language) VALUES
('welcome', 'Välkommen till kundvagnsapplikationen!', 'sv_SE'),
('enter.item.count', 'Ange antalet varor att köpa: ', 'sv_SE'),
('enter.price', 'Ange priset för varan: ', 'sv_SE'),
('enter.quantity', 'Ange mängden varor: ', 'sv_SE'),
('item', 'Vara', 'sv_SE'),
('item.cost', 'Varans kostnad:', 'sv_SE'),
('total.cost', 'Total kostnad:', 'sv_SE'),
('thank.you', 'Tack för att du handlade hos oss!', 'sv_SE'),
('error.positive.number', 'Ange ett positivt tal: ', 'sv_SE'),
('error.invalid.number', 'Ogiltig inmatning. Ange ett giltigt nummer: ', 'sv_SE'),
('cart.saved', 'Kundvagn sparad i databasen!', 'sv_SE'),
('cart.save.error', 'Fel vid sparande av kundvagn i databasen.', 'sv_SE');

-- Insert Japanese (ja_JP) localization strings
INSERT INTO localization_strings (`key`, value, language) VALUES
('welcome', 'ショッピングカートアプリケーションへようこそ！', 'ja_JP'),
('enter.item.count', '購入する商品の数を入力してください: ', 'ja_JP'),
('enter.price', '商品の価格を入力してください: ', 'ja_JP'),
('enter.quantity', '商品の数量を入力してください: ', 'ja_JP'),
('item', '商品', 'ja_JP'),
('item.cost', '商品の合計:', 'ja_JP'),
('total.cost', '合計金額:', 'ja_JP'),
('thank.you', 'お買い上げありがとうございました！', 'ja_JP'),
('error.positive.number', '正の数を入力してください: ', 'ja_JP'),
('error.invalid.number', '無効な入力です。有効な数値を入力してください: ', 'ja_JP'),
('cart.saved', 'ショッピングカートがデータベースに保存されました！', 'ja_JP'),
('cart.save.error', 'データベースへの保存中にエラーが発生しました。', 'ja_JP');

-- Verify data insertion
SELECT language, COUNT(*) as string_count FROM localization_strings GROUP BY language;
