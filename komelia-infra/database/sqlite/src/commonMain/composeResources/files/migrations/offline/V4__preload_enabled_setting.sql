ALTER TABLE SETTINGS ADD COLUMN preload_next_book_enabled BOOLEAN NOT NULL DEFAULT 0;
UPDATE SETTINGS SET preload_next_book_enabled = 1 WHERE preload_next_book_pages > 0;
UPDATE SETTINGS SET preload_next_book_pages = 3 WHERE preload_next_book_pages = 0;
