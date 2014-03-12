-- DROPs in order
drop table if exists load_averages_shadow;
drop asynceventlistener if exists AggListener;
drop table if exists load_averages;
drop table if exists raw_sensor;
drop hdfsstore if exists sensorStore;

drop function if exists expired;
create function expired (timestamp bigint, age integer)
  returns integer
  language java
  parameter style java
  no sql
  external name 'com.pivotal.gfxd.demo.ExpirationPredicate.expired';

-- CREATES in order
create hdfsstore sensorStore
  NameNode 'hdfs://localhost:8020'
  HomeDir '/sensorStore'
  BatchSize 10
  BatchTimeInterval 2000
  WriteOnlyFileRolloverInterval 60;

create table raw_sensor
  (
    id bigint,
    timestamp bigint,
    value float(23),
    property smallint,
    plug_id integer,
    household_id integer,
    house_id integer,
    weekday smallint,
    time_slice smallint
  )
  partition by column (house_id)
  persistent
  eviction by criteria (expired(timestamp, 60) = 1)
  eviction frequency 60 seconds
  hdfsstore (sensorStore) writeonly;

drop index if exists raw_sensor_idx;
create index raw_sensor_idx on raw_sensor (weekday, time_slice, plug_id);

create table load_averages
  (
    house_id integer not null,
    household_id integer,
    plug_id integer not null,
    weekday smallint not null,
    time_slice smallint not null,
    total_load float(23),
    event_count integer

  )
  partition by column (house_id)
  colocate with (raw_sensor);

-- alter table load_averages
--    add constraint load_averages_pk primary key (house_id, plug_id, weekday, time_slice);

drop index if exists load_averages_idx;
create index load_averages_idx on load_averages (weekday, time_slice, plug_id);

-- This is a shadow table, used by the MapReduce job to insert it's results into.
-- An AEQ picks up the events and inserts them into the actual load_averages table.
create table load_averages_shadow
  (
    house_id integer not null,
    household_id integer,
    plug_id integer not null,
    weekday smallint not null,
    time_slice smallint not null,
    total_load float(23),
    event_count integer
  )
  partition by column (house_id)
  colocate with (raw_sensor)
  eviction by lrucount 1000
  evictaction destroy;

alter table load_averages_shadow
    add constraint load_averages_shadow_pk primary key (weekday, time_slice, plug_id);

-- drop index if exists load_averages_shadow_idx;
-- create index load_averages_shadow_idx on load_averages_shadow (weekday, time_slice, plug_id);

create asynceventlistener AggListener
(
   listenerclass 'com.pivotal.gfxd.demo.AggregationListener'
   initparams 'total_load'
   batchsize 1000
   batchtimeinterval 1000
) server groups (group1);

alter table load_averages_shadow set asynceventlistener (AggListener);

call sys.start_async_event_listener('AggListener');
