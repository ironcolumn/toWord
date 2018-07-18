import Utils.CommonUtil;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.pobjects.graphics.text.LineText;
import org.icepdf.core.pobjects.graphics.text.PageText;
import org.icepdf.core.pobjects.graphics.text.WordText;
import org.icepdf.core.util.GraphicsRenderingHints;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import static Utils.CommonUtil.splitChars;

/**
 * @author zzy on 2018/7/14 0:18.
 * @version 1.0
 */
public class PDFsToPhotos {

    //设置APPID/AK/SK
    public static final String APP_ID = "11534646";

    public static final String API_KEY = "mcsGPnWfIxEeSKZlEshekf5T";

    public static final String SECRET_KEY = "RecAVXKyHmPqdNWnGK7CN1cmQWbQNcUa";

    private static final Set < String > numCh = Collections.unmodifiableSet ( new HashSet <> ( Arrays.asList ( "一" , "二" , "三" , "四" , "五" , "六" , "七" , "八" , "九" , "十" ) ) );

    public static void main ( String[] args ) throws Exception {

        //        pdfToText ( "D:/result/1.pdf" );
        pdfsToWordICE ( "D:/2018行规（印刷版）" );

    }

    public static void pdfsToWordOCR ( String filepath ) throws Exception {

        File file = new File ( filepath );
        //         如果目录下文件存在
        if ( file.exists ( ) && file.isDirectory ( ) ) {
            File[] list = file.listFiles ( );
            //取文件名子存入name中
            List < File > toSort = new ArrayList <> ( );
            for ( File pdf : list ) {
                toSort.add ( pdf );
            }
            toSort.sort ( Comparator.comparing ( File :: lastModified ).reversed ( ) );
            toSort.forEach ( pdf -> {
                try {
                    pdfToImage ( pdf.getAbsolutePath ( ) );
                } catch ( Exception e ) {
                    e.printStackTrace ( );
                }
            } );
            PhotosToWord.photosToWord ( filepath );

        }
    }

    public static void pdfsToWordICE ( String filepath ) throws Exception {

        File file = new File ( filepath );
        //         如果目录下文件存在
        if ( file.exists ( ) && file.isDirectory ( ) ) {
            File[] list = file.listFiles ( );
            //取文件名子存入name中
            List < File > toSort = new ArrayList <> ( );
            for ( File pdf : list ) {
                if ( pdf.getName ( ).contains ( "pdf" ) ) {
                    toSort.add ( pdf );
                }
            }
            String name = toSort.get ( 0 ).getName ( );
            toSort.sort ( Comparator.comparing ( File :: lastModified ).reversed ( ) );
            final String[] result = { "" };
            for ( File pdf : toSort ) {
                try {
                    result[ 0 ] = result[ 0 ] + pdfToString ( pdf.getAbsolutePath ( ) );
                } catch ( Exception e ) {
                    e.printStackTrace ( );
                }
            }

            stringToWord ( result[ 0 ] , name , filepath );
        }
    }

    /**
     * 将指定pdf文件的首页转换为指定路径的缩略图
     *
     * @param filepath 原文件路径，
     */
    public static void pdfToImage ( String filepath ) throws Exception {

        Document               document  = null;
        List < BufferedImage > imageList = new ArrayList < BufferedImage > ( );

        float rotation = 0f;
        //float scale = 2.0f;

        document = new Document ( );
        document.setFile ( filepath );
        File file = new File ( filepath );
        // maxPages = document.getPageTree().getNumberOfPages();
        for ( int i = 0 ; i < document.getNumberOfPages ( ) ; i++ ) {
            BufferedImage img = ( BufferedImage ) document.getPageImage ( i , GraphicsRenderingHints.SCREEN , Page.BOUNDARY_CROPBOX , rotation , 3 );
            //            imageList.add ( img );
            int                   index   = i + 1;
            File                  outFile = new File ( file.getParentFile ( ).getAbsolutePath ( ) + "/" + file.getName ( ) + index + ".jpg" );
            ByteArrayOutputStream out     = new ByteArrayOutputStream ( );
            // 写图片
            ImageIO.write ( img , "jpg" , out );
            byte[]           b      = out.toByteArray ( );
            FileOutputStream output = new FileOutputStream ( outFile );
            output.write ( b );
            out.close ( );
            output.close ( );
        }

    }

    public static String pageTextToString ( PageText pageText ) {

        final String[] var1 = { "" };
        pageText.getPageLines ( ).forEach ( o -> {
            LineText       lineText = ( LineText ) o;
            final String[] row      = { "" };
            lineText.getWords ( ).forEach ( o1 -> {
                WordText wordText = ( WordText ) o1;
                row[ 0 ] = row[ 0 ].concat ( wordText.getText ( ) ).trim ( );
            } );
            if ( row[ 0 ].length ( ) > 1 ) {
                String start = row[ 0 ].substring ( 0 , 1 );
                String end   = row[ 0 ].substring ( row[ 0 ].length ( ) - 1 , row[ 0 ].length ( ) );
                if ( ! start.matches ( "[\u4e00-\u9fa5]" ) ) {
                    row[ 0 ] = "\n".concat ( row[ 0 ] );
                }
                if ( row[0].startsWith ( "第" ) && splitChars.contains ( row[0].substring ( 2 , 3 ) ) ) {
                    row[0]= row[0].concat ( "\n" );
                }
                if ( end.equals ( "." ) || end.equals ( "。" ) ) {
                    row[ 0 ] = row[ 0 ].concat ( "\n" );
                }
            }
            var1[ 0 ] = var1[ 0 ].concat ( row[ 0 ] );
        } );
        var1[ 0 ] = var1[ 0 ].replace ( "\n\n" , "\n" );
        return CommonUtil.washText ( var1[ 0 ] );
    }

    public static void pdfToText ( String filepath , String filenName ) throws Exception {

        Document document = null;
        document = new Document ( );
        if ( filepath.contains ( "pdf" ) ) {
            document.setFile ( filepath );
        }
        File       file   = new File ( filepath );
        FileWriter writer = null;
        for ( int i = 0 ; i < document.getNumberOfPages ( ) ; i++ ) {
            PageText pageText = document.getPageText ( i );
            if ( ! pageText.equals ( "" ) ) {
                try {
                    writer = new FileWriter ( file.getParentFile ( )
                            .getPath ( ) + "/" + filenName.substring ( 0 , filenName.length ( ) - 4 ) + ".txt" , true );
                    writer.write ( pageTextToString ( pageText ) );
                    writer.flush ( );//刷新内存，将内存中的数据立刻写出。
                } catch ( IOException e ) {
                    e.printStackTrace ( );
                }
            }
        }
        if ( writer != null ) {
            writer.close ( );
        }
    }

    public static String pdfToString ( String filepath ) throws Exception {

        Document document = null;
        document = new Document ( );
        if ( filepath.contains ( "pdf" ) ) {
            document.setFile ( filepath );
        }
        String result = "";
        for ( int i = 0 ; i < document.getNumberOfPages ( ) ; i++ ) {
            PageText pageText = document.getPageText ( i );
            if ( ! pageText.equals ( "" ) ) {
                result = result.concat ( pageTextToString ( pageText ) );
            }
        }
        return result;
    }

    public static void stringToWord ( String text , String fileName , String path ) throws IOException {

        XWPFDocument  document   = new XWPFDocument ( );
        XWPFParagraph body;
        String[]      subContent = text.split ( "\n" );
        for ( String str : subContent ) {
            body = document.createParagraph ( );
            body.setAlignment ( ParagraphAlignment.LEFT );
            XWPFRun r4 = body.createRun ( );
            r4.setText ( str );
        }
        FileOutputStream out = new FileOutputStream ( new File ( path + "/" + fileName.substring ( 0 , fileName.length ( ) - 4 ) + ".docx" ) );
        document.write ( out );
        out.close ( );
    }

}
