-- public.songs definition

-- Drop table

-- DROP TABLE public.songs;

CREATE TABLE public.songs (
	id int8 NOT NULL,
	album varchar(100) NOT NULL,
	artist varchar(100) NOT NULL,
	duration varchar(255) NOT NULL,
	"name" varchar(100) NOT NULL,
	"year" varchar(255) NOT NULL,
	CONSTRAINT songs_id_check CHECK ((id >= 1)),
	CONSTRAINT songs_pkey PRIMARY KEY (id)
);