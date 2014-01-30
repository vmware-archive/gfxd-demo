#!/usr/bin/env python
#
# This script will process the sensor raw data file and create a sqlite
# load_averages database. The script will print out a value for every minute's
# worth of data which has been read. You want to make sure to generate at least
# 1 week worth of data (10,080 minutes).
#
# The script will also populate the plug_id column with a globally unique value
# instead of a per-household unique value. See get_plug_id() for the formula.
#
# Once you have the database, you can produce a CSV file with the following:
#     sqlite3 -csv load_averages.db "select * from load_averages"
#
# You can also use this to restrict the dataset to a smaller set of houses.

import sqlite3
import sys
import datetime

# bits would be an array containing: [plug_id, household_id, house_id]
def get_plug_id(bits):
    result = 19
    for i in bits:
        result = 91 * result + int(i) * 7

    return result
        

def mainline():
    #conn = sqlite3.connect(":memory:")
    conn = sqlite3.connect("load_averages.db")

    last_minute = 0
    total_minutes = 0

    with conn:
        cur = conn.cursor()
        cur.execute("PRAGMA synchronous = OFF")
        cur.execute("create table load_averages (house_id int, household_id int, plug_id int, weekday int, time_slice int, total_load real, event_count int)")
        cur.execute("create index index_1 on load_averages (plug_id, weekday, time_slice)")

        # 0 - id bigint,
        # 1 - timestamp bigint,
        # 2 - value float(23),
        # 3 - property smallint,
        # 4 - plug_id integer,
        # 5 - household_id integer,
        # 6 - house_id integer,
        batch = []
        for line in sys.stdin:
            line = line.strip()
            data = line.split(",")
            if data[3] == "0":
                continue

            ts = int(data[1])
            plug_id = get_plug_id(data[4:])

            # Calculate weekday and time_slice
            time = datetime.datetime.fromtimestamp(ts)
            time_slice = (time.hour * 60 + time.minute) / 5
            weekday = ((time.weekday() + 1) % 7) + 1

            if time.minute != last_minute:
                conn.commit()
                print total_minutes
                total_minutes += 1
                last_minute = time.minute

            cur.execute("select total_load, event_count from load_averages where plug_id=? and weekday=? and time_slice=?", [plug_id, weekday, time_slice])
            row = cur.fetchone()

            if row is None:
                cur.execute("insert into load_averages values(?, ?, ?, ?, ?, ?, ?)", [data[6], data[5], plug_id, weekday, time_slice, data[2], 1])
            else:
                t = float(data[2]) + row[0]
                c = row[1] + 1
                cur.execute("update load_averages set total_load=?, event_count=? where plug_id=? and weekday=? and time_slice=?", [t, c, plug_id, weekday, time_slice])
        

if __name__ == "__main__":
    mainline()
