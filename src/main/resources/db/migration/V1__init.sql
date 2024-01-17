drop table if exists QRTZ_FIRED_TRIGGERS;
drop table if exists QRTZ_PAUSED_TRIGGER_GRPS;
drop table if exists QRTZ_SCHEDULER_STATE;
drop table if exists QRTZ_LOCKS;
drop table if exists QRTZ_SIMPLE_TRIGGERS;
drop table if exists QRTZ_CRON_TRIGGERS;
drop table if exists QRTZ_SIMPROP_TRIGGERS;
drop table if exists QRTZ_BLOB_TRIGGERS;
drop table if exists QRTZ_TRIGGERS;
drop table if exists QRTZ_JOB_DETAILS;
drop table if exists QRTZ_CALENDARS;

create table QRTZ_JOB_DETAILS
  (
    sched_name varchar(120) not null,
    job_name  varchar(200) not null,
    job_group varchar(200) not null,
    description varchar(250) null,
    job_class_name varchar(250) not null,
    is_durable bool not null,
    is_nonconcurrent bool not null,
    is_update_data bool not null,
    requests_recovery bool not null,
    job_data blob null,
    primary key (sched_name,job_name,job_group)
);

create table QRTZ_TRIGGERS
  (
    sched_name varchar(120) not null,
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    job_name  varchar(200) not null,
    job_group varchar(200) not null,
    description varchar(250) null,
    next_fire_time bigint null,
    prev_fire_time bigint null,
    priority integer null,
    trigger_state varchar(16) not null,
    trigger_type varchar(8) not null,
    start_time bigint not null,
    end_time bigint null,
    calendar_name varchar(200) null,
    misfire_instr smallint null,
    job_data blob null,
    primary key (sched_name,trigger_name,trigger_group),
    foreign key (sched_name,job_name,job_group)
	references QRTZ_JOB_DETAILS(sched_name,job_name,job_group)
);

create table QRTZ_SIMPLE_TRIGGERS
  (
    sched_name varchar(120) not null,
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    repeat_count bigint not null,
    repeat_interval bigint not null,
    times_triggered bigint not null,
    primary key (sched_name,trigger_name,trigger_group),
    foreign key (sched_name,trigger_name,trigger_group)
	references QRTZ_TRIGGERS(sched_name,trigger_name,trigger_group)
);

create table QRTZ_CRON_TRIGGERS
  (
    sched_name varchar(120) not null,
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    cron_expression varchar(120) not null,
    time_zone_id varchar(80),
    primary key (sched_name,trigger_name,trigger_group),
    foreign key (sched_name,trigger_name,trigger_group)
	references QRTZ_TRIGGERS(sched_name,trigger_name,trigger_group)
);

create table QRTZ_SIMPROP_TRIGGERS
  (
    sched_name varchar(120) not null,
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    str_prop_1 varchar(512) null,
    str_prop_2 varchar(512) null,
    str_prop_3 varchar(512) null,
    int_prop_1 int null,
    int_prop_2 int null,
    long_prop_1 bigint null,
    long_prop_2 bigint null,
    dec_prop_1 numeric(13,4) null,
    dec_prop_2 numeric(13,4) null,
    bool_prop_1 bool null,
    bool_prop_2 bool null,
    primary key (sched_name,trigger_name,trigger_group),
    foreign key (sched_name,trigger_name,trigger_group)
    references QRTZ_TRIGGERS(sched_name,trigger_name,trigger_group)
);

create table QRTZ_BLOB_TRIGGERS
  (
    sched_name varchar(120) not null,
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    blob_data blob null,
    primary key (sched_name,trigger_name,trigger_group),
    foreign key (sched_name,trigger_name,trigger_group)
        references QRTZ_TRIGGERS(sched_name,trigger_name,trigger_group)
);

create table QRTZ_CALENDARS
  (
    sched_name varchar(120) not null,
    calendar_name  varchar(200) not null,
    calendar blob not null,
    primary key (sched_name,calendar_name)
);


create table QRTZ_PAUSED_TRIGGER_GRPS
  (
    sched_name varchar(120) not null,
    trigger_group  varchar(200) not null,
    primary key (sched_name,trigger_group)
);

create table QRTZ_FIRED_TRIGGERS
  (
    sched_name varchar(120) not null,
    entry_id varchar(95) not null,
    trigger_name varchar(200) not null,
    trigger_group varchar(200) not null,
    instance_name varchar(200) not null,
    fired_time bigint not null,
    sched_time bigint not null,
    priority integer not null,
    state varchar(16) not null,
    job_name varchar(200) null,
    job_group varchar(200) null,
    is_nonconcurrent bool null,
    requests_recovery bool null,
    primary key (sched_name,entry_id)
);

create table QRTZ_SCHEDULER_STATE
  (
    sched_name varchar(120) not null,
    instance_name varchar(200) not null,
    last_checkin_time bigint not null,
    checkin_interval bigint not null,
    primary key (sched_name,instance_name)
);

create table QRTZ_LOCKS
  (
    sched_name varchar(120) not null,
    lock_name  varchar(40) not null,
    primary key (sched_name,lock_name)
);

create index idx_qrtz_j_req_recovery on QRTZ_JOB_DETAILS(sched_name,requests_recovery);
create index idx_qrtz_j_grp on QRTZ_JOB_DETAILS(sched_name,job_group);

create index idx_qrtz_t_j on QRTZ_TRIGGERS(sched_name,job_name,job_group);
create index idx_qrtz_t_jg on QRTZ_TRIGGERS(sched_name,job_group);
create index idx_qrtz_t_c on QRTZ_TRIGGERS(sched_name,calendar_name);
create index idx_qrtz_t_g on QRTZ_TRIGGERS(sched_name,trigger_group);
create index idx_qrtz_t_state on QRTZ_TRIGGERS(sched_name,trigger_state);
create index idx_qrtz_t_n_state on QRTZ_TRIGGERS(sched_name,trigger_name,trigger_group,trigger_state);
create index idx_qrtz_t_n_g_state on QRTZ_TRIGGERS(sched_name,trigger_group,trigger_state);
create index idx_qrtz_t_next_fire_time on QRTZ_TRIGGERS(sched_name,next_fire_time);
create index idx_qrtz_t_nft_st on QRTZ_TRIGGERS(sched_name,trigger_state,next_fire_time);
create index idx_qrtz_t_nft_misfire on QRTZ_TRIGGERS(sched_name,misfire_instr,next_fire_time);
create index idx_qrtz_t_nft_st_misfire on QRTZ_TRIGGERS(sched_name,misfire_instr,next_fire_time,trigger_state);
create index idx_qrtz_t_nft_st_misfire_grp on QRTZ_TRIGGERS(sched_name,misfire_instr,next_fire_time,trigger_group,trigger_state);

create index idx_qrtz_ft_trig_inst_name on QRTZ_FIRED_TRIGGERS(sched_name,instance_name);
create index idx_qrtz_ft_inst_job_req_rcvry on QRTZ_FIRED_TRIGGERS(sched_name,instance_name,requests_recovery);
create index idx_qrtz_ft_j_g on QRTZ_FIRED_TRIGGERS(sched_name,job_name,job_group);
create index idx_qrtz_ft_jg on QRTZ_FIRED_TRIGGERS(sched_name,job_group);
create index idx_qrtz_ft_t_g on QRTZ_FIRED_TRIGGERS(sched_name,trigger_name,trigger_group);
create index idx_qrtz_ft_tg on QRTZ_FIRED_TRIGGERS(sched_name,trigger_group);


commit;