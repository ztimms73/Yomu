CREATE TABLE favourites (
	id INTEGER PRIMARY KEY,
	name TEXT NOT NULL,
	summary TEXT,
	genres TEXT,
	url TEXT NOT NULL,
	thumbnail TEXT,
	provider TEXT NOT NULL,
	status INTEGER,
	rating INTEGER,
	created_at INTEGER,
	category_id INTEGER,
	total_chapters INTEGER,
	new_chapters INTEGER,
	removed INTEGER DEFAULT 0
);