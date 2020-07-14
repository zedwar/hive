select
    id,
    sum(if(sex='男',1,0)) male_count,
from
    emp_Sec
group by
    id;