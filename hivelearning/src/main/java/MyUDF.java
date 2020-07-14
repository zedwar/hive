import org.apache.hadoop.hive.ql.exec.UDF;

public class MyUDF extends UDF {
    public int evaluate(int data){
        return data+5;
    }
}
