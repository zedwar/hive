#类型转换
    select cast('1' as int);
#DDL
##数据库
    defalut是在当前默认位置创建库
    desc database extended +库名;
    drop database hive;空库才能drop
    drop database hive cascade;强行删除
##数据表
    create [external] table [if not exists] table_name
    [(col_name data_type[comment col_comment],...)]
    [comment table_comment]
    [partitioned by (col_name data_type[comment col_comment],...)]
    [clustered by (col_name,col_name,...)]
    [sorted by (col_name [asc|desc],...)] info num_buckets buckets]
    [row_format row_format]
    [stored as file_format]
    [location hdfs_path]
##内外部表
    alter table 表 set tblproperties('EXTERNAL' = 'TRUE');注意大写的必须大写
    desc formatted 表；
##分区表
    partitioned by (month string);
    alter table 表名 add partition(month='2019-08') partition(month='2019-09');
    alter table 表名 add partition(month='2019-08'),partition(month='2019-09');    
    show partitions 表名；
##二级分区表
    parttioned by (month string,day string)
##修改表
    alter table 表名 rename to 新表名;
    alter table 表名 add columns (name string);
    alter table 表名 change column name sex string;
    alter table 表名 replace columns (name int);整张表替换,数据不会改变
##删除表
    drop 
#DML
    导出数据
    dfs -get /user/hive/warehouse/student/month=201709/000000_0
    /opt/module/datas/export/students.txt
    
    bin/hive -e 'select * from default.student;' > /opt/module/datas/export/student4.txt;
    
    export table defalut.student to /user/hive/warehouse/export/student'; 
#查询
    <=>指向符在空值时为true
##group by
    having对分组后进行过滤
##join
    join语法不支持or但支持and
    实在要用where，不支持非等值连接
##sort
    order by全局排序只有一个reduce
    sort by多个reduce，最后结果无序
    distribute by按字段分区
    当字段一样时，sortby和dis by都用clusterby
##分桶表
    create table stu_buck(id int,name string)
    clustered by(id)
    into 4 buckets
    row format delimited fields terminated by '\t';
    select * from stu_buck tablesample(bucket 1 out of 4 on id);
##时间类
    date_format
    date_add
    date_sub
    datediff('2019-07-03','2019-07-06')
    select regexp_replace('2019/07/03','/','-');
    select 
        id,
        sum(if(sex='男',1,0)) male_count,
    from
        emp_Sec
    group by
        id;       
##行转列
    concat(id,'_',name,'_',sex)
    concat_ws('_',id,name,sex)
    collect_set(col);同一列去重，但是也会列转行，这个是聚合函数，需要搭配group by使用
##列转行
    explode
    select movie,explode(category) from movie_info;错误
    lateral view;  
    lateral view udtf(expression) tableAlias as column ; 
    select 
        movie,category_name
    from 
    movie_info lateral view explode(category) table_tmp as category_name;
##窗口函数
    over() 
    select name,count(*) over ()
    from business
    where substring(orderdate,1,7) = '2017-04'
    group by name;
    over仅作用于count
    select orderdate,cost,sum(cost) over(order by orderdate)
    from business;
    
    select orderdate,cost,sum(cost) over(distribute by orderdate)
        from business;
    select name,orderdate,cost,lag(orderdate,1,'1970-01-01')over(distribute by name sort by orderdate)
    from business;
##排序    
     rank1,1,3,4 over(partition by subject oeder by score desc) rank1
     dense_rank1,1,2,3
     row_number1,2,3,4   
#练习
    select
        userID,
        date_format(regexp_replace(visitDate,'/','-'),'yyyy-MM') visitDate,
        visitCount
    from
        action;t1
    
    select
        userId,
        mn,
        sum(visitCount),
        
    from
        t1
    group by 
        userId,mn;    