drop all objects;
CREATE TABLE "Film"
(
    "film_id"      long GENERATED ALWAYS AS IDENTITY NOT NULL,
    "name"         varchar                           NOT NULL,
    "description"  varchar(200)                      NOT NULL,
    "rating_id"    integer                           NULL,
    "release_date" date                              NOT NULL,
    "duration"     integer                           NULL,
    CONSTRAINT "pk_Film" PRIMARY KEY ("film_id")
);

CREATE TABLE "User"
(
    "user_id"   long GENERATED ALWAYS AS IDENTITY NOT NULL,
    "name"      varchar                           NULL,
    "email"     varchar                           NOT NULL,
    "login"     varchar                           NOT NULL,
    "birthdate" date                              NULL,
    CONSTRAINT "pk_User" PRIMARY KEY ("user_id"),
    CONSTRAINT "uk_User_email" UNIQUE ("email")
);

CREATE TABLE "Friendship"
(
    "user_id"   long NOT NULL,
    "friend_id" long NOT NULL,
    CONSTRAINT "pk_Friendship" PRIMARY KEY ("user_id", "friend_id")
);

CREATE TABLE "Genre"
(
    "genre_id" long GENERATED ALWAYS AS IDENTITY NOT NULL,
    "name"     varchar                           NOT NULL,
    CONSTRAINT "pk_Genre" PRIMARY KEY ("genre_id")
);

CREATE TABLE "Films_Genre"
(
    "film_id"  long NOT NULL,
    "genre_id" long NOT NULL,
    CONSTRAINT "pk_Films_Genre" PRIMARY KEY ("film_id", "genre_id")
);

CREATE TABLE "Rating_MPA"
(
    "rating_id"   long GENERATED ALWAYS AS IDENTITY NOT NULL,
    "name"        varchar                           NOT NULL,
    "description" varchar                           NOT NULL,
    CONSTRAINT "pk_Rating_MPA" PRIMARY KEY ("rating_id")
);

CREATE TABLE "User_Likes"
(
    "film_id" long NOT NULL,
    "user_id" long NOT NULL
);

ALTER TABLE "Film"
    ADD CONSTRAINT "fk_Film_rating_id" FOREIGN KEY ("rating_id")
        REFERENCES "Rating_MPA" ("rating_id");
ALTER TABLE "Friendship"
    ADD CONSTRAINT "fk_Friendship_user_id" FOREIGN KEY ("user_id")
        REFERENCES "User" ("user_id");
ALTER TABLE "Friendship"
    ADD CONSTRAINT "fk_Friendship_friend_id" FOREIGN KEY ("friend_id")
        REFERENCES "User" ("user_id");
ALTER TABLE "Films_Genre"
    ADD CONSTRAINT "fk_Films_Genre_film_id" FOREIGN KEY ("film_id")
        REFERENCES "Film" ("film_id");
ALTER TABLE "Films_Genre"
    ADD CONSTRAINT "fk_Films_Genre_genre_id" FOREIGN KEY ("genre_id")
        REFERENCES "Genre" ("genre_id");
ALTER TABLE "User_Likes"
    ADD CONSTRAINT "fk_User_Likes_film_id" FOREIGN KEY ("film_id")
        REFERENCES "Film" ("film_id");
ALTER TABLE "User_Likes"
    ADD CONSTRAINT "fk_User_Likes_user_id" FOREIGN KEY ("user_id")
        REFERENCES "User" ("user_id");