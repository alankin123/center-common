//package com.jiuzhou.center.common.db.page;
//
//import org.apache.ibatis.session.RowBounds;
//
//public class PageSqlFactory {
//    
//    /**
//     * 根据数据库方言获取分页查询语句（目前只支持mysql、oracle、sqlServer；默认mysql）
//     * @param dialect
//     * @param originalSql
//     * @param rowBounds
//     * @return
//     * @author Taocong
//     * @date 2017年9月28日 下午4:12:57
//     */
//    public static String getPageSqlByDialect(String dialect, String originalSql, RowBounds rowBounds) {
//        String pageSql = null;
//        if (null == dialect || "mysql".equals(dialect)) {
//            pageSql = originalSql + " LIMIT " + rowBounds.getOffset() + "," + rowBounds.getLimit();
//        } else if ("oracle".equals(dialect)) {
//            StringBuilder sqlBuilder = new StringBuilder();
//            sqlBuilder.append("select * from ( select tmp_page.*, rownum row_id from ( ");
//            sqlBuilder.append(originalSql);
//            sqlBuilder.append(" ) tmp_page where rownum <= ");
//            sqlBuilder.append(rowBounds.getLimit());
//            sqlBuilder.append(" ) where row_id > ");
//            sqlBuilder.append(rowBounds.getOffset());
//            pageSql = sqlBuilder.toString();
//        } else if ("sqlServer".equals(dialect)) {
//            /* 查看sql中是否有排序规则：
//             * 1.如果没有，按手动新增字段n（n=0）生成rown_number
//             * 2.如果有，按指定的排序规则生成rown_number
//             * 
//             *  eg：
//             *  1.select * from a where 1=1
//             *  ->select * from (select row_number() over(ORDER BY n) as rownumber,* 
//             *      from ( select top 20 n=0, * from a where 1=1)t )tt where rownumber> 0
//             *
//             *  2.select * from a where 1=1 ORDER BY b
//             *  ->select * from (select row_number() over(ORDER BY b) as rownumber,* 
//             *      from ( select top 20 n=0, * from a where 1=1 ORDER BY b)t )tt where rownumber> 0
//             */
//            // 找到sql中的排序
//            // (由于indexof不能使用正则，所以先使用replaceAll对sql中的排序语法规范化;(?i)标识忽略大小写;\\s*表示空格出现一次或多次)
//            int orderStartIndex = originalSql.replaceAll("(?i)ORDER\\s+BY", "ORDER BY").lastIndexOf("ORDER BY");
//            String orderStr = "ORDER BY n";
//            // 有排序，且是最外层的排序
//            if (orderStartIndex != -1 && originalSql.lastIndexOf(")") < orderStartIndex) {
//                orderStr = originalSql.substring(orderStartIndex);
//            }
//            pageSql = originalSql.replaceFirst("(?i)select", "select * from (select row_number() over(" + orderStr
//                    + ") as rownumber,* from ( select top " + (rowBounds.getOffset() + rowBounds.getLimit()) + " n=0,");
//            pageSql += ")t )tt where rownumber> " + rowBounds.getOffset();
//        }
//
//        return pageSql;
//    }
//}