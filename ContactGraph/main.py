import os
import sqlite3
import pandas as pd

DATA_DIR = "data"
DB = "main.db"
TABLE = "location"


def create_connection(db_file):
    """ create a database connection to a SQLite database """
    try:
        conn = sqlite3.connect(db_file)
        return conn
    except sqlite3.Error as e:
        print(e)


def create_table(conn):
    conn.execute(f'CREATE TABLE IF NOT EXISTS {TABLE} (_node_id integer primary key autoincrement, _latitude integer not null, _longitude integer not null, _latitude_gps integer, _longitude_gps integer, _latitude_wifi integer, _longitude_wifi integer, _altitude integer not null, _accuracy integer not null, _accuracy_gps integer, _accuracy_wifi integer, _activity integer not null, _place_name text, _time_location text not null, _place_comment text, userid integer)')
    conn.commit()


def get_db():
    for doc in os.listdir(DATA_DIR):
        if doc != DB and doc.endswith('.db'):
            yield int(''.join(filter(str.isdigit, doc))), os.path.join(DATA_DIR, doc)


def convertToLat(row):
    return int(row['_latitude']) * (10 ** -6)


def convertToLong(row):
    return int(row['_longitude']) * (10 ** -6)


def aggregate_data():

    df_list = []
    for user, dbfile in get_db():

        print(' File picked - ', dbfile)
        conn = sqlite3.connect(dbfile)
        df = pd.read_sql_query("SELECT * from locationTable", conn)
        df['_time_location'] = pd.to_datetime(df['_time_location'], infer_datetime_format=True).dt.date
        df['_latitude'], df['_longitude'] = df.apply(convertToLat, axis=1), df.apply(convertToLong, axis=1)
        df = df.loc[(df['_latitude'] != 0) & (df['_longitude'] != 0)]
        df["userid"] = user
        df = df[['userid', '_node_id','_latitude', '_longitude', '_time_location']]
        df.dropna(inplace=True)
        df_list.append(df)
    return pd.concat(df_list)


if __name__ == '__main__':

    conn = create_connection(os.path.join(DATA_DIR, DB))
    create_table(conn)
    main_df = aggregate_data()
    main_df.to_sql('location', conn, if_exists='replace', index=False)
    conn.close()
