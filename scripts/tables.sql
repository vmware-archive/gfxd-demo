---------------------------------
-- RAW SENSOR DATA
---------------------------------
DROP TABLE IF EXISTS raw_sensor;
CREATE TABLE raw_sensor (
id				bigint,
timestamp 		bigint,
value 			float(23),
property 		smallint,
plug_id			integer,
household_id	integer,
house_id		integer,
CONSTRAINT RAW_SENSOR_PK PRIMARY KEY (ID)
)
PARTITION BY COLUMN (house_id);


---------------------------------
-- LOAD AVERAGE PREDICTION 
---------------------------------
DROP TABLE IF EXISTS load_averages;
CREATE TABLE load_averages (
house_id		integer,
household_id	integer,
plug_id			integer,
week_day		smallint,
time_segment	smallint,
total_load		float(23),
entries			integer
)
PARTITION BY COLUMN (house_id);
