create table customer (id bigint not null auto_increment, email varchar(255), name varchar(255), phone varchar(255), primary key (id)) engine=InnoDB;
create table service_advisor (id bigint not null auto_increment, user_id bigint, email varchar(255), name varchar(255), password varchar(255), primary key (id)) engine=InnoDB;
create table service_record (service_date date, id bigint not null auto_increment, service_advisor_id bigint, vehicle_id bigint, issue_type varchar(255), status varchar(255), primary key (id)) engine=InnoDB;
create table service_record_work_items (service_record_id bigint not null, work_items_id bigint not null) engine=InnoDB;
create table users (id bigint not null auto_increment, email varchar(255), password varchar(255), phone varchar(255), username varchar(255), role enum ('Admin','ServiceAdvisor'), primary key (id)) engine=InnoDB;
create table vehicle (last_service_date date, customer_id bigint, id bigint not null auto_increment, model varchar(255), registration_number varchar(255), primary key (id)) engine=InnoDB;
create table work_item (cost float(53) not null, quantity integer not null, id bigint not null auto_increment, description varchar(255), primary key (id)) engine=InnoDB;
alter table service_advisor add constraint UKctlwxavy19sj9uvhxhuyrx8ao unique (user_id);
alter table service_advisor add constraint UKbxsfp2sgbuj2vpa9p1dmvr3gb unique (email);
alter table service_record_work_items add constraint UK38sskf8ohnfua7h9y1l5lob0h unique (work_items_id);
alter table users add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);
alter table service_advisor add constraint FK6hxipfaa8ew0k8vpsu0ttgidq foreign key (user_id) references users (id);
alter table service_record add constraint FKin77kepj7a0pnbpuhysbi9dwj foreign key (service_advisor_id) references service_advisor (id);
alter table service_record add constraint FKjb8h28088ieuklg5kv0t542u2 foreign key (vehicle_id) references vehicle (id);
alter table service_record_work_items add constraint FKs95aarqnp2m4yathufnd69pta foreign key (work_items_id) references work_item (id);
alter table service_record_work_items add constraint FKqku54vdjctgr8je0kyf02w7lg foreign key (service_record_id) references service_record (id);
alter table vehicle add constraint FKlwqsusjj6iodeb0df1b554vxq foreign key (customer_id) references customer (id);


