package Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zzy on 2018/7/16 1:27.
 * @version 1.0
 */
public class CommonUtil {

    public static final Set < String > splitChars = Collections.unmodifiableSet ( new HashSet <> ( Arrays.asList ( "章" , "节" , "条" ) ) );

    static String[] units = { "" , "十" , "百" , "千" , "万" , "十万" , "百万" , "千万" , "亿" , "十亿" , "百亿" , "千亿" , "万亿" };

    static char[] numArray = { '零' , '一' , '二' , '三' , '四' , '五' , '六' , '七' , '八' , '九' };

    public static boolean containsNum ( String str ) {

        for ( int i = str.length ( ) ; -- i >= 0 ; ) {
            if ( Character.isDigit ( str.charAt ( i ) ) ) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNum ( String str ) {

        for ( int i = str.length ( ) ; -- i >= 0 ; ) {
            if ( ! Character.isDigit ( str.charAt ( i ) ) ) {
                return false;
            }
        }
        return true;
    }

    public static String washText ( String text ) {

        for ( int time = 1 ; time < 3 ; time++ ) {
            String   result = "";
            String[] split  = text.split ( "\n" );
            for ( int i = 0 ; i < split.length ; i++ ) {
                String row = split[ i ].trim ( );
                if ( row.length ( ) > 1 ) {
                    String start = row.substring ( 0 , 1 );
                    String end   = row.substring ( row.length ( ) - 1 , row.length ( ) );
                    //如果不以汉字开头则添加换行符
                    if ( ! start.matches ( "[\u4e00-\u9fa5]" ) ) {
                        row = "\n".concat ( row );
                    }
                    if ( row.startsWith ( "第" ) && splitChars.contains ( row.substring ( 2 , 3 ) ) ) {
                        row = row.concat ( "\n" );
                    }
                    //如果以句号或.结尾，则在结尾添加换行符
                    if ( end.equals ( "." ) || end.equals ( "。" ) ) {
                        row = row.concat ( "\n" );
                    }
                }
                //第二次清洗
                if ( time == 2 ) {
                    String s = row.replaceAll ( "\n" , "" );
                    if ( s.length ( ) < 2 || ( s.length ( ) < 4 && isNum ( s ) ) ) {
                        row = "";
                    }
                    if ( row.length ( ) > 2 ) {
                        String end = row.substring ( row.length ( ) - 1 , row.length ( ) );
                        if ( ! ( row.startsWith ( "第" ) && splitChars.contains ( row.substring ( 2 , 3 ) ) ) && end.matches ( "[^" + "" + "(a-zA-Z0-9\\\\u4e00-\\\\u9fa5)]" ) || isNum ( end ) || end
                                .equals ( "(" ) || end.equals ( "（" ) || end.equals ( ")" ) || end.equals ( "）" ) ) {
                            row = row.concat ( "\n\n" );
                        }
                    }
                }
                result = result.concat ( row );
            }
            result = result.replace ( "\n\n\n" , "" );
            result = result.replace ( "\n\n" , "\n" );
            text = result;
        }
        return text;
    }

    public static String foematInteger ( int num ) {

        char[]        val = String.valueOf ( num ).toCharArray ( );
        int           len = val.length;
        StringBuilder sb  = new StringBuilder ( );
        for ( int i = 0 ; i < len ; i++ ) {
            String  m      = val[ i ] + "";
            int     n      = Integer.valueOf ( m );
            boolean isZero = n == 0;
            String  unit   = units[ ( len - 1 ) - i ];
            if ( isZero ) {
                if ( '0' == val[ i - 1 ] ) {
                    // not need process if the last digital bits is 0
                    continue;
                } else {
                    // no unit for 0
                    sb.append ( numArray[ n ] );
                }
            } else {
                sb.append ( numArray[ n ] );
                sb.append ( unit );
            }
        }
        return sb.toString ( );
    }
}
