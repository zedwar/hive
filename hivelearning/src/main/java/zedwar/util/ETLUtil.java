package zedwar.util;

public class ETLUtil {
    /**
     * 1.过滤掉长度不够的，小于9个字段的
     * 2.去掉类别字段中的空格
     * 3.修改相关视频ID字段的分隔符，用'\t'替换为’&‘
     * @param oriStr 输入数据，原始数据
     * @return 过滤后的数据
     */
    public static String etlStr(String oriStr){
        StringBuffer sb = new StringBuffer();
        // 1.切割
        String[] fields = oriStr.split("\t");
        // 2.对字段长度进行过滤
        if (fields.length<9){
            return null;
        }
        // 3.去掉类别字段中的空格
        fields[3] = fields[3].replaceAll(" ", "");
        // 4.修改相关视频ID字段的分隔符，用'\t'替换为’&‘
        for (int i = 0; i < fields.length; i++) {

            //对非相关ID进行处理
            if (i<9){
                if (i==fields.length-1){
                    sb.append(fields[i]);
                }else{
                    sb.append(fields[i]).append("\t");
                }
            }else {
                if (i==fields.length-1){
                    sb.append(fields[i]);
                }else {
                    sb.append(fields[i]).append("&");
                }
            }
        }
        return sb.toString();
    }
}
