import datetime
import os
import sqlite3
import pandas as pd
from collections import deque
from math import sin, cos, sqrt, atan2, radians


DATA_DIR = "data"
DB = "main.db"
TABLE = "location"
R = 6373.0 # approximate radius of earth in km
LIMIT = 5 # in "km"
USERS = 11
FILE_PATH = os.path.join(os.path.dirname(os.path.realpath(__file__)), 'graph')


def create_connection(db_file):
    """ create a database connection to a SQLite database """

    try:
        conn = sqlite3.connect(db_file)
        return conn
    except sqlite3.Error as e:
        print(e)


def get_pastweek_locations(df, userid, date):

    filtered = df.loc[df['userid'] == userid]
    #print('Filtered DF - \n', filtered)
    if filtered.empty:
        return filtered

    firstday = str(date - datetime.timedelta(days=7))
    #print('First day - ', firstday)

    pastweek = filtered[(filtered['_time_location'] > firstday) & (filtered['_time_location'] <= str(date))]
    #print('Past week - \n', pastweek)
    if pastweek.empty:
        return pastweek

    return pastweek


# Ref: https://stackoverflow.com/questions/19412462/getting-distance-between-two-points-based-on-latitude-longitude
def get_distance(lat1, lon1, lat2, lon2, to_radians=True, earth_radius=6371):

    lat1 = radians(lat1)
    lon1 = radians(lon1)
    lat2 = radians(lat2)
    lon2 = radians(lon2)

    dlon = lon2 - lon1
    dlat = lat2 - lat1

    a = sin(dlat / 2) ** 2 + cos(lat1) * cos(lat2) * sin(dlon / 2) ** 2
    c = 2 * atan2(sqrt(a), sqrt(1 - a))

    distance = R * c
    # "Close" contact
    if distance <= LIMIT:
        return True
    return False


def get_close_contacts(df, locations, userid):

    filtered = df.loc[df['userid'] != userid]
    #print('Filtered DF - \n', filtered)
    if filtered.empty:
        return filtered

    samedays = filtered.merge(locations, on='_time_location', how='inner')
    #print('Same days DF - \n', samedays)
    if samedays.empty:
        return samedays

    samedays['close'] = samedays.apply(lambda row: get_distance(row['_latitude_x'], row['_latitude_x'],
                                                                row['_latitude_y'], row['_latitude_y']), axis=1)
    samedays = samedays.loc[samedays['close']]
    #print('Close DF - \n', samedays)
    if samedays.empty:
        return samedays

    return samedays


def fill_adj(contacts, userid, ADJ):

    for closeid in contacts['userid_x'].unique():
        ADJ[userid-1][closeid-1] = 1
        ADJ[closeid-1][userid-1] = 1

    return ADJ

def get_neighbors(contacts):

    contacts.drop_duplicates(['userid_x', '_time_location'], inplace=True)
    closeids = contacts['userid_x'].astype(int).tolist()
    dates = contacts['_time_location'].astype(str).tolist()
    neighbors = [[closeid,date] for closeid, date in zip(closeids, dates)]
    #print('Neighbors - \n', neighbors)

    return neighbors


def display(ADJ):

    for row in ADJ:
        print(row)


def save(ADJ):

    try:
        with open(FILE_PATH, "w") as f:
            firstrow = [i for i in range(1, 12)]
            f.write("ID" + str(firstrow) + "\n")
            for i, row in enumerate(ADJ):
                f.write(str(i+1) + " " + str(row) + "\n")

        print("Results saved in file - ", FILE_PATH)
    except:
        print("ERROR: Results not saved in file!")

def generate_graph(userid, date, ADJ):

    date = datetime.datetime.strptime(date, '%Y-%m-%d').date()
    userid = int(userid)

    #print('db connection ----->')
    conn = create_connection(os.path.join(DATA_DIR, DB))
    #print('<----- db connection')

    df = pd.read_sql_query(f"SELECT * from {TABLE}", conn, parse_dates=['_time_location'])

    #print('pastweek locations ----->')
    locations = get_pastweek_locations(df, userid, date)
    #print('<----- pastweek locations')
    if locations.empty:
        print('Locations are EMPTY! Exiting ...')
        return None, ADJ

    #print('close contacts ----->')
    contacts = get_close_contacts(df, locations, userid)
    #print('<----- close contacts')
    if contacts.empty:
        print('Close Contacts are EMPTY! Exiting ...')
        return None, ADJ

    #print('adj matrix ----->')
    ADJ = fill_adj(contacts, userid, ADJ)
    #print('<----- adj matrix')

    #print('close ids & dates ----->')
    neighbors = get_neighbors(contacts)
    #print('<----- close ids & dates')

    return neighbors, ADJ


def contact_tracing(userid = "1", date = "2011-06-14"):

    print(f'Contact tracing for ({userid}, {date}) ----->')
    ADJ = [[0 for _ in range(11)] for _ in range(11)]
    queue = deque([[userid, date]])
    visited = set()
    visited.add(userid)

    while queue:

        curr = queue.popleft()
        userid = curr[0]
        date = curr[1]

        #print(f'Graph for ({userid}, {date}) ----->')
        neighbors, ADJ = generate_graph(userid, date, ADJ)
        #print(f'<----- Graph for ({userid}, {date})')

        if neighbors:
            for id, day in neighbors:
                if id not in visited:
                    queue.append([id, day])
                    visited.add(id)

    #print('print result ----->')
    display(ADJ)
    #print('<----- print result')

    # print('save result ----->')
    save(ADJ)
    # print('<----- save result')

    print(f'<----- Contact Tracing for ({userid}, {date})')
    return ADJ

if __name__ == "__main__":
    contact_tracing()
