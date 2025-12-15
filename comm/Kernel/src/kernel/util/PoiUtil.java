package kernel.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
//import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import util.Strings;


/**  
30. 
31.     * 生成excel的通用模版  
33.     *  
35.     * @param response  响应,设置生成的文件类型,文件头编码方式和文件名,以及输出  
37.     * @param list      表的内容，List类型，里面的每个节点是String[]型  
39.     * @param firstLine 标题字符串数组 String[]  
41.     * @param sheetName 表名  
43.     * @param fileName  文件名  
45.     */ 
public class PoiUtil {
	
	public static String getCell(Cell cell) {
//		DecimalFormat df = new DecimalFormat("#.##");
//		if (cell == null)
//			return "";
//		switch (cell.getCellType()) {
//		case HSSFCell.CELL_TYPE_NUMERIC:
//			if(HSSFDateUtil.isCellDateFormatted(cell)){
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				return Strings.emptyIfNull(sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue())).toString());
//			}
//			return Strings.emptyIfNull(df.format(cell.getNumericCellValue()));
//		case HSSFCell.CELL_TYPE_STRING:
//			return Strings.emptyIfNull(cell.getStringCellValue());
//		case HSSFCell.CELL_TYPE_FORMULA:
//			return Strings.emptyIfNull(cell.getCellFormula());
//		case HSSFCell.CELL_TYPE_BLANK:
//			return "";
//		case HSSFCell.CELL_TYPE_BOOLEAN:
//			return Strings.emptyIfNull(cell.getBooleanCellValue() + "");
//		case HSSFCell.CELL_TYPE_ERROR:
//			return Strings.emptyIfNull(cell.getErrorCellValue() + "");
//		}
		return "";
	}
	

	public static String getValue(HSSFCell hssfCell) { 
//	        if (hssfCell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
//	            // 返回布尔类型的值
//	            return String.valueOf(hssfCell.getBooleanCellValue());
//	        } else if (hssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
//	            // 返回数值类型的值
//	            return String.valueOf(hssfCell.getNumericCellValue());
//	        } else {
//	            // 返回字符串类型的值
//	            return String.valueOf(hssfCell.getStringCellValue());
//	        }
		return "";
	 } 
	//填充内容
	public static int createCell(List list,Drawing patriarch,Workbook wb, Sheet sheet,Row row,Cell cell,CellStyle style, Integer i) throws IOException {   
		   //生成所有行的单元格内容，如果测试list设为null即可，或者将这一段代码注释掉   
		Object[] array1 = null;   
        if (null == list || list.size() == 0) {   
           // do nothing   
        } else {   
           for (int k = 0; k < list.size(); k++) {   
               row = sheet.createRow(++i);   
               row.setHeightInPoints(33);   
               array1 = (Object[]) list.get(k);   
               if (null != array1 && array1.length != 0) {   
                  for (int f = 0; f < array1.length; f++) {  
                 	 if (array1[f] instanceof BufferedImage){
                 		 ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();  
                 		 ImageIO.write((BufferedImage)array1[f],"jpg",byteArrayOut);  
                 		 HSSFClientAnchor anchor = new HSSFClientAnchor(0,0,1020,255,(short)f,k+1,(short)f,k+1);//
//                 		 anchor.setAnchorType(2);
                 		 patriarch.createPicture(anchor , wb.addPicture(byteArrayOut.toByteArray(),Workbook.PICTURE_TYPE_JPEG));  
                 	 }else{
                 	     cell = row.createCell((short) f);   
	                         cell.setCellStyle(style);   
	                       //  cell.setEncoding(HSSFCell.ENCODING_UTF_16);   
	                         cell.setCellValue(String.valueOf(array1[f])); 
                 	 }
                  }   
               }   
           }   
        }   
		return i;
	}
	//填充内容
	public static int createCommonSearchCell(List list,Workbook wb, Sheet sheet,Row row,Cell cell,CellStyle style, Integer i) throws IOException {   
		//生成所有行的单元格内容，如果测试list设为null即可，或者将这一段代码注释掉   
		Object[] array1 = null;   
		if (null == list || list.size() == 0) {   
			// do nothing   
		} else {   
			for (int k = 0; k < list.size(); k++) {   
				row = sheet.createRow(++i);   
				row.setHeightInPoints(33);   
				array1 = (Object[]) list.get(k);   
				if (null != array1 && array1.length != 0) {   
					for (int f = 0; f < array1.length; f++) {  
							cell = row.createCell((short) f);   
//							cell.setCellStyle(style);   
							//  cell.setEncoding(HSSFCell.ENCODING_UTF_16);   
							cell.setCellValue(String.valueOf(array1[f])); 
					}   
				}   
			}   
		}   
		return i;
	}
	
	//生成表头
   public static void createHead(HttpServletResponse response,String[] firstLine,Workbook wb, Sheet sheet ,CellStyle style,Row row,Cell cell, Integer i, String sheetName, String fileName) {   
	      try {   
	          response.setContentType("application/vnd.ms-excel");//设置生成的文件类型   
	          response.setHeader("Content-Disposition", "filename="  
	                 + new String(fileName.getBytes("gb2312"), "iso8859-1"));//设置文件头编码方式和文件名   
	        //  HSSFWorkbook wb = new HSSFWorkbook();//excel文件,一个excel文件包含多个表   
	          //HSSFSheet sheet = wb.createSheet();//表，一个表包含多个行   
	        //  wb.setSheetName(0, sheetName, HSSFWorkbook.ENCODING_UTF_16);// 设置sheet中文编码；   
	          //设置字体等样式   
	          Font font = wb.createFont();   
	          font.setFontHeightInPoints((short) 10);   
	          font.setFontName("Courier New");   
	         // HSSFCellStyle style = wb.createCellStyle();   
	          style.setFont(font);   
	          style.setWrapText(true);   
//	          style.setAlignment(CellStyle.ALIGN_LEFT);
//	          style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	          //声明一个画图的顶级管理器
	        // HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
	        //  HSSFRow row;//行，一行包括多个单元格   
	        //  HSSFCell cell;//单元格   
	          row = sheet.createRow(i);//由HSSFSheet生成行   
	          row.setHeightInPoints(30);   
	           //生成首行   
	           for (short j = 0; j < firstLine.length; j++) {   
	              cell = row.createCell(j);//由行生成单元格   
	              cell.setCellStyle(style);   
	           //   cell.setEncoding(HSSFCell.ENCODING_UTF_16);// 设置cell中文编码；   
	              cell.setCellValue(firstLine[j]);   
	              sheet.setColumnWidth(j, (short) (6000));   
	           }   
	       } catch (Exception ex) {   
	           ex.printStackTrace();   
	       }   
	     //  return;   
	    }   
	  
   public static void out(Workbook wb,HttpServletResponse response){
	      //输出   
	   try{
		   OutputStream out = response.getOutputStream();   
	       wb.write(out);   
	      //注意看以下几句的使用  
	       out.flush();  
	       out.close();  
	   } catch (Exception ex) {   
          // ex.printStackTrace();   
       }  
	  
   }
   public static void excel(HttpServletResponse response, List list,   
	          String[] firstLine, String sheetName, String fileName) {   
		   Object[] array1 = null;   
	      try {   
	          short i = 0;// row行标   
	          response.setContentType("application/vnd.ms-excel");//设置生成的文件类型   
	          response.setHeader("Content-Disposition", "filename="  
	                 + new String(fileName.getBytes("gb2312"), "iso8859-1"));//设置文件头编码方式和文件名   
	          HSSFWorkbook wb = new HSSFWorkbook();//excel文件,一个excel文件包含多个表   
	          HSSFSheet sheet = wb.createSheet();//表，一个表包含多个行   
	        //  wb.setSheetName(0, sheetName, HSSFWorkbook.ENCODING_UTF_16);// 设置sheet中文编码；   
	          //设置字体等样式   
	          HSSFFont font = wb.createFont();   
	          font.setFontHeightInPoints((short) 10);   
	          font.setFontName("Courier New");   
	          HSSFCellStyle style = wb.createCellStyle();   
	          style.setFont(font);   
	          style.setWrapText(true);   
//	          style.setAlignment(CellStyle.ALIGN_LEFT);
//	          style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	          //声明一个画图的顶级管理器
	          HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
	          HSSFRow row;//行，一行包括多个单元格   
	          HSSFCell cell;//单元格   
	           //生成首行   
	          if(firstLine!=null&&firstLine.length>0){
		          row = sheet.createRow(i);//由HSSFSheet生成行   
		          row.setHeightInPoints(25);   
		           for (short j = 0; j < firstLine.length; j++) {   
			              cell = row.createCell(j);//由行生成单元格     
			              cell.setCellStyle(style);   
			           //   cell.setEncoding(HSSFCell.ENCODING_UTF_16);// 设置cell中文编码；   
			              cell.setCellValue(firstLine[j]);   
			              sheet.setColumnWidth(j, (short) (6000));   
			           }  
	          }
	           //生成所有行的单元格内容，如果测试list设为null即可，或者将这一段代码注释掉   
	           if (null == list || list.size() == 0) {   
	              // do nothing   
	           } else {   
	              for (int k = 0; k < list.size(); k++) {   
	                  row = sheet.createRow(((firstLine==null||firstLine.length==0)&&k==0)?0:++i);   
	                  row.setHeightInPoints(33);   
	                  array1 = (Object[]) list.get(k);   
	                  if (null != array1 && array1.length != 0) {   
	                     for (int f = 0; f < array1.length; f++) {  
	                    	 if (array1[f] instanceof BufferedImage){
	                    		 ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();  
	                    		 ImageIO.write((BufferedImage)array1[f],"jpg",byteArrayOut);  
	                    		 HSSFClientAnchor anchor = new HSSFClientAnchor(0,0,1020,255,(short)f,k+1,(short)f,k+1);//
//	                    		 anchor.setAnchorType(2);
	                    		 patriarch.createPicture(anchor , wb.addPicture(byteArrayOut.toByteArray(),Workbook.PICTURE_TYPE_JPEG));  
	                    	 }else{
	                    	     cell = row.createCell((short) f);   
	  	                         cell.setCellStyle(style);   
	  	                       //  cell.setEncoding(HSSFCell.ENCODING_UTF_16);   
	  	                         cell.setCellValue(String.valueOf(array1[f])); 
	                    	 }
	                     }   
	                  }   
	              }   
	           }   
	           for(int j=0;j<firstLine.length;j++){
	        	   sheet.autoSizeColumn((short)j); 
	           }
	           //输出   
	           OutputStream out = response.getOutputStream();   
	           wb.write(out);   
	           out.close();   
	       } catch (Exception ex) {   
	           ex.printStackTrace();   
	       }   
	       return;   
	    }



}
