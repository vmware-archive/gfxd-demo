drop table if exists raw_sensor;
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
  eviction by lrucount 1000000
  evictaction destroy;

drop index if exists raw_sensor_idx;
create index raw_sensor_idx on raw_sensor (weekday, time_slice, plug_id);

drop table if exists load_averages;
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

-- required for MR job updates
alter table load_averages
    add constraint LOAD_AVERAGES_PK PRIMARY KEY (house_id, plug_id, weekday, time_slice);

drop index if exists load_averages_idx;
create index load_averages_idx on load_averages (weekday, time_slice, plug_id);

drop asynceventlistener if exists AggListener;
create asynceventlistener AggListener
(
   listenerclass 'com.pivotal.gfxd.demo.AggregationListener'
   initparams ''
   batchsize 1000
   batchtimeinterval 1000
) server groups (group1);

alter table raw_sensor set asynceventlistener (AggListener);

call sys.start_async_event_listener('AggListener');
