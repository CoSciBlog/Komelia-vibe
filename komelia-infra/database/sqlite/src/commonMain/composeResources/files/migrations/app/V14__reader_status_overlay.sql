ALTER TABLE ImageReaderSettings ADD COLUMN reader_overlay_show_clock BOOLEAN DEFAULT 1 NOT NULL;
ALTER TABLE ImageReaderSettings ADD COLUMN reader_overlay_show_battery BOOLEAN DEFAULT 1 NOT NULL;
ALTER TABLE ImageReaderSettings ADD COLUMN reader_overlay_show_page_number BOOLEAN DEFAULT 1 NOT NULL;
ALTER TABLE ImageReaderSettings ADD COLUMN reader_overlay_position TEXT DEFAULT 'TOP_RIGHT' NOT NULL;
ALTER TABLE ImageReaderSettings ADD COLUMN reader_overlay_text_color TEXT DEFAULT 'AUTO' NOT NULL;
ALTER TABLE ImageReaderSettings ADD COLUMN reader_overlay_font_size INTEGER DEFAULT 14 NOT NULL;
