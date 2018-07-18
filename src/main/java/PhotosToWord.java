import com.baidu.aip.ocr.AipOcr;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zzy on 2018/7/14 0:18.
 * @version 1.0
 */
public class PhotosToWord {

    //设置APPID/AK/SK
    public static final String APP_ID = "11534646";

    public static final String API_KEY = "mcsGPnWfIxEeSKZlEshekf5T";

    public static final String SECRET_KEY = "RecAVXKyHmPqdNWnGK7CN1cmQWbQNcUa";

    private static final Set < String > numCh = Collections.unmodifiableSet ( new HashSet <> ( Arrays.asList ( "一" , "二" , "三" , "四" , "五" , "六" , "七" , "八" , "九" , "十" ) ) );

    public static void main ( String[] args ) {

        //         调用接口
        String path = "D:/news";
        photosToWord ( path );
    }

    public static void photosToWord ( String path ) {
        // 初始化一个AipOcr
        AipOcr client = new AipOcr ( APP_ID , API_KEY , SECRET_KEY );

        //         可选：设置网络连接参数
        client.setConnectionTimeoutInMillis ( 2000 );
        client.setSocketTimeoutInMillis ( 60000 );

        //         可选：设置代理服务器地址, http和socket二选一，或者均不设置
        //        client.setHttpProxy ( "proxy_host" , proxy_port );  // 设置http代理
        //        client.setSocketProxy ( "proxy_host" , proxy_port );  // 设置socket代理

        //         可选：设置log4j日志输出格式，若不设置，则使用默认配置
        //         也可以直接通过jvm启动参数设置此环境变量
        //        System.setProperty ( "aip.log4j.conf" , "path/to/your/log4j.properties" );
        File file = new File ( path );
        //         如果目录下文件存在
        if ( file.exists ( ) && file.isDirectory ( ) ) {
            File[] list = file.listFiles ( );
            //取文件名子存入name中
            List < File > toSort = new ArrayList <> ( );
            for ( File photo : list ) {
                if ( photo.getName ( ).contains ( "jpg" ) ) {
                    toSort.add ( photo );
                }

            }
            toSort.stream ( ).collect ( Collectors.toList ( ) ).sort ( Comparator.comparing ( File :: lastModified ).reversed ( ) );
            String finalresult = "";
            for ( int i = 0 ; i < toSort.size ( ) ; i++ ) {
                File       photo     = toSort.get ( i );
                JSONObject res       = client.basicAccurateGeneral ( path + "/" + photo.getName ( ) , new HashMap < String, String > ( ) );
                JSONArray  jsonArray = res.getJSONArray ( "words_result" );
                String     result    = "";
                for ( int j = 0 ; j < jsonArray.length ( ) ; j++ ) {
                    String words     = jsonArray.getJSONObject ( j ).get ( "words" ).toString ( );
                    String substring = words.substring ( 0 , 1 );
                    if ( ! substring.matches ( "[\u4e00-\u9fa5]" ) ) {
                        words = "\n".concat ( words );
                    }
                    result = result.concat ( words );
                }
                finalresult = finalresult.concat ( result );
            }
            System.out.println ( finalresult );
        }
    }

}
