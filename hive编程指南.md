#第四章数据类型和文件格式
##基本数据类型
    TINYINT\SMALLINT\INT\BIGINT\BOOLEAN\FLOAT\DOUBLE\STRING\TIMESTAMP\BINARY
##集合数据类型
    STRUCT\MAP\ARRAY
##数据库
    show databases like 'h.*';
    create database financials location '/my/preferred/directory';
    create database financial comment 'holds all financial tables';
    create table if not exits mydb.employees2 like mydb.employees;拷贝
    describe extended mydb.employees;
    describe mydb.employees.salary;描述某一个信息
    create external table if not exits mydb.employees2；
    create table employees(
        name STRING,
        salary FLOAT,
        subordinates ARRAY<STRING>,
        deductions MAP<STRING,FLOAT>,
        address STRUCT<street:STRING,city:STRING,state:STRING,zip:INT>
        )
    partitioned by (country STRING,state STRING);
    
    select * from employees where country = 'us' and state = 'il';
    
    show partitions employees;
    
    show partitions employees partition(country='us');
    
    show partitions employees partition(country='us',state='ak');
    
    load data local inpath '$(env:HOME)/california-employees'
    into table employees
    partition (country = 'us', state = 'ca');
    
    alter table log_messages add partition(year = 2012,month = 1,day =2)
    location 'hdfs://master_server/data/log_messages/2012/01/02';
    
    hadoop fs -rmr /data/log_messages/2011/01/02;
    
    create table employees(
        name STRING,
        salary FLOAT,
        subordinates ARRAY<STRING>,
        deductions MAP<STRING,FLOAT>,
        address STRUCT<street:STRING,city:STRING,state:STRING,zip:INT>
        )
    row format delimited 
    fields terminated by '\001'
    collection items terminated by '\002'
    map keys terminated by '\003'
    lines terminated by '\n'
    stored as textfile;
    
    drop table if exists employees;
    
    alter table log_messages rename to logmsgs;
    
    alter table log_messages add if not exists
    partition (year = 2011,month = 1,day = 1) location '/logs/2011/01/01';
    
    alter table log_messages partiton(year = 2011,month = 12,day =2)
    set location 's3n://outbucket/logs/2011/01/02';
    
    alter table log_messages drop if exists partition(year = 2011,month = 12,day =2);
    
    alter table log_messages
    change column hms hours_minutes_seconds int
    comment 'the hours, minutes, and seconds part of the timestamp'
    after severity;
    
    alter table log_messages add columns (
        app_name STRING COMMENT 'Application name',
        session_id LONG COMMENT 'The current session id');
    
    ALTER TABLE log_messages REPLACE COLUMNS (
        hours_mins_secs INT COMMENT 'hour,minute,seconds from timestamp',
        severity  STRING COMMENT 'The message severity'
        message  STRING COMMENT 'The rest of the message');
        
    ALTER TABLE log_messages SET TBLPROPERTIES (
        'notes' = 'The process id is no longer captures; this column is always NULL');
    
    
    ALTER TABLE log_messages
    PARTITION(year = 2012, month = 1,day = 1)
    SET FILEFORMAT SEQUENCEFILE;
    
    ALTER TABLE table_using_JSON_storage
    SET SERDE 'com.example.JSONSerDe'
    WITH SERDEPROPERTIES (
    'prop1' = 'value1',
    'prop2' = 'value2');
    
    ALTER TABLE table_using_JSON_storage
    SET SERDEPROPERTIES (
    'prop3' = 'value3');
    
    ALTER TABLE stocks
    CLUSTERED BY (exchange,symbol)
    SORTED BY (symbol)
    INTO 48 BUCKETS;
    
    ALTER TABLE log_messages TOUCH
    PARTITION(year = 2012,month = 1,day =1);
    
    
    
#第五章数据操作
##向管理表中装载数据
    load data local inpath '$(env:HOME)/california-employees'
    overwrite into table employees
    partition (country = 'us', state = 'ca');
    
##通过查询语句向表中插入数据
    insert overwrite table employees
    partition (country = 'us',state = 'or')
    select * from staged_employees se
    where se.cnty = 'us' and se.st = 'or';
   ###动态分区插入
    insert overwrite table employees
    partition (country,state)
    select ...,se.cnty,se.st
    from staged_employees se
    where se.cnty = 'us';
    
##单个查询语句中创建表并加载数据
    create table ca_employees
    as select name,salary,address
    from employees
    where se.state = 'ca';      
    
##导出数据
    insert overwrite local directory '/tmp/ca_employees'
    select name,salary,address
    from employees
    where se.state = 'ca';
    
#第六章查询
##select from
    create table employees(
            name STRING,
            salary FLOAT,
            subordinates ARRAY<STRING>,
            deductions MAP<STRING,FLOAT>,
            address STRUCT<street:STRING,city:STRING,state:STRING,zip:INT>
            )
        partitioned by (country STRING,state STRING);
    
    select name,subordinates[0] from employees;
    select name,deductions["state taxes"] from employees;
    select name,address.city from employees;
    
    select symbol,'price.*' from stocks;
    
    select upper(name),salary,deduction["federal taxes"],round(salary*(1-deduction["federal taxes"])) from employees;
    ###函数（104）
    select explode(subordinates) as sub from employees;
    
    select parse_url_tuple(url,'host','path','query') as (host,path,query)
    from url_table;
    
    select name,salary,
    case
        when salary <50000.0 then 'low'
        when salary>=5000.0 then 'high'
    end as bracker from employees; 
    
    select ame,address.street from employees where adress.street like '%Ave.';
    
    select name,address.street
    from employees where address.street RLIKE '.*(Chicago|Ontario).*';
    
    select year(ymd),avg(price_close) from stocks
    where exchange = 'NASDAQ' and symbol = 'aapl;
    group by year(ymd)
    having avg(price_close)>50.0;
    
    select a.ymd,a.price,b.price_close
    from stocks a join stocks b on a.ymd = b.ymd
    where a.symbol = 'AAPL' and b.symbol = 'IBM';
    
    select s.ymd,s.symbol,s.price_close,d.dividend
    from dividends d right outer join stocks s on d.ymd = s.ymd and d.symbol = s.symbol
    where s,symbol = 'AAPL';
    
    select s.ymd,s.symbol,s.price_close
    from stocks s
    order by s.ymd asc,s.symbol desc;
    
    select s.ymd,s.symbol,s.price_close
    from stocks s
    order by s.ymd asc,s.symbol desc;
    
    ###类型转换
    select name,salary from employees
    where cast(salary as float)<10000.0;
    
    ###抽样查询
    select * from numbers tablesample(bucket 3 out of 10 on rand()) s;
    
    ###数据块抽样
    
    select * from numbersflat tablesample(0.1 percent) s;
    
    ###union all

#第七章视图
    create view shorter_join as
    select *from people join cart
    on (cart.people_id=people.id) where firstname = 'john';
    
    
    select lastname from shorter_join where id = 3;
    
#第八章索引
##创建索引
    create table employees(
            name STRING,
            salary FLOAT,
            subordinates ARRAY<STRING>,
            deductions MAP<STRING,FLOAT>,
            address STRUCT<street:STRING,city:STRING,state:STRING,zip:INT>
            )
    partitioned by (country STRING,state STRING);            
    
    create index employees_index
    on table employees (country)
    as 'org.apache.hadoop.hive.ql.index.compact.CompactIndexHadler'
    with deferred rebuild
    indexproperties ('creator = 'me','created_at' = 'some_time')
    in table employees_index_table
    partitoned by (country,name)
    comment 'employees indexed by countey and name.';
##重建索引
    alter index employees_index
    on table employees
    partition (country = 'us')
    rebuild;
##显示索引
    show formatted index on employees;
    
##删除索引
    drop index if exists employees_index on table employees;
            
    
    