drop table if exists raw_sensor;
create table raw_sensor
  (
    id bigint,
    timestamp bigint,
    weekday smallint,
    time_slice smallint,
    value float(23),
    property smallint,
    plug_id integer,
    household_id integer,
    house_id integer
--    CONSTRAINT RAW_SENSOR_PK PRIMARY KEY (ID)
  )
  partition by column (house_id)
  eviction by lrucount 1000000
  evictaction destroy
--  persistent asynchronous
;

-- ALTER TABLE FLIGHTAVAILABILITY
--    ADD CONSTRAINT FLIGHTAVAIL_PK Primary Key (
--       FLIGHT_ID,
--       SEGMENT_NUMBER,
--       FLIGHT_DATE);

drop table if exists load_averages;
create table load_averages
  (
    house_id integer,
    household_id integer,
    plug_id integer,
    weekday smallint,
    time_slice smallint,
    total_load float(23),
    event_count integer
  )
  partition by column (house_id)
;

drop index if exists load_averages_idx;
create index load_averages_idx on load_averages (house_id, household_id, plug_id, weekday, time_segment);

drop asynceventlistener if exists AggListener;
-- create asynceventlistener AggListener
-- (
--    listenerclass 'com.pivotal.gfxd.demo.AggregationListener'
--    initparams ''
--    batchsize 1000
--    batchtimeinterval 1000
-- ) server groups (group1);
-- 
-- alter table raw_sensor set asynceventlistener (AggListener);
-- 
-- call sys.start_async_event_listener('AggListener');
