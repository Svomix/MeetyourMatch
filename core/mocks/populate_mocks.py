import psycopg2
import csv
import glob

def location(lat, lon):
    return "latitude=%f;longitude=%f" % (lat, lon)

conn = psycopg2.connect(dbname="data", user="postgres", password="25812581", host="localhost", port="5432")
with conn:
    with conn.cursor() as cursor:
        
        cursor.execute("TRUNCATE TABLE Attributes RESTART IDENTITY CASCADE")
        
        cursor.execute("""INSERT INTO Attributes(name)
VALUES ('tag');
INSERT INTO Attributes(name)
VALUES ('vk_id');
INSERT INTO Attributes(name)
VALUES ('access_token');
INSERT INTO Attributes(name)
VALUES ('last_sync_timestamp');""")
        
        #Cities
        cursor.execute("TRUNCATE TABLE cities RESTART IDENTITY CASCADE")
        with open('city.csv', newline='', encoding="utf-8") as csvfile:
            spamreader = csv.reader(csvfile, delimiter=',')
            next(spamreader, None)
            for id, row in enumerate(spamreader):
                city, lat, lon = row[9], float(row[20]), float(row[21])
                cursor.execute("INSERT INTO cities (id, location, name) VALUES (%s, %s, %s);", (id, location(lat,lon), city))
        
        cursor.execute("TRUNCATE TABLE tags RESTART IDENTITY CASCADE")
        cursor.execute("TRUNCATE TABLE Events RESTART IDENTITY CASCADE")
        cursor.execute("TRUNCATE TABLE Events_attribute_value RESTART IDENTITY CASCADE")
        
        for file in glob.glob("*.sql"):
                with open(file, "r", encoding="utf-8") as f:
                   cursor.execute(f.read())
                   
        #$2a$10$PaiePy.c9ynQumHOH/QFeOP/9j1WbWyIBZ7ggCoG9V.gaYHRrbkvO
        cursor.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE")
        
        cursor.execute("INSERT INTO users (id, email, username, password) VALUES (%s, %s, %s, %s);", (1, "test@example.org", "user", "$2a$10$PaiePy.c9ynQumHOH/QFeOP/9j1WbWyIBZ7ggCoG9V.gaYHRrbkvO"))
        cursor.execute("INSERT INTO users (id, email, username, password) VALUES (%s, %s, %s, %s);", (2, "admin@example.org", "admin", "$2a$10$PaiePy.c9ynQumHOH/QFeOP/9j1WbWyIBZ7ggCoG9V.gaYHRrbkvO"))
        
        cursor.execute("TRUNCATE TABLE user_authority RESTART IDENTITY CASCADE")
        
        cursor.execute("INSERT INTO user_authority (id, authority) VALUES (%s, %s);", (1, "ROLE_USER"))
        cursor.execute("INSERT INTO user_authority (id, authority) VALUES (%s, %s);", (2, "ROLE_ADMIN"))
        
        cursor.execute("TRUNCATE TABLE authorities RESTART IDENTITY CASCADE")
        
        cursor.execute("INSERT INTO authorities (user_id, authority_id) VALUES (%s, %s);", (1, 1))
        cursor.execute("INSERT INTO authorities (user_id, authority_id) VALUES (%s, %s);", (2, 1))
        cursor.execute("INSERT INTO authorities (user_id, authority_id) VALUES (%s, %s);", (2, 2))
        
    conn.commit()

conn.close()