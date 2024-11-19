import psycopg2
import csv

def location(lat, lon):
    return "latitude=%f;longitude=%f" % (lat, lon)

conn = psycopg2.connect(dbname="data", user="postgres", password="25812581", host="localhost", port="5432")
with conn:
    with conn.cursor() as cursor:
        #Cities
        cursor.execute("DELETE FROM cities")
        with open('city.csv', newline='', encoding="utf-8") as csvfile:
            spamreader = csv.reader(csvfile, delimiter=',')
            next(spamreader, None)
            for id, row in enumerate(spamreader):
                city, lat, lon = row[9], float(row[20]), float(row[21])
                cursor.execute("INSERT INTO cities (id, location, name) VALUES (%s, %s, %s);", (id, location(lat,lon), city))
        
        
        
    conn.commit()

conn.close()