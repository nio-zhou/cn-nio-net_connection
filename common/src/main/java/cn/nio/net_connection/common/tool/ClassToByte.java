/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.nio.net_connection.common.tool;

import org.slf4j.LoggerFactory;

import java.awt.List;
import java.lang.reflect.Field;
import java.util.Queue;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  对象转换成字节数组
 *  @author nio
 */
public class ClassToByte {
    static org.slf4j.Logger logger = LoggerFactory.getLogger("");
    public static byte[] toStream(Object object){
        if(object==null){
            return new byte[0];
        }
        if(object instanceof Integer){
            return intToByte((Integer)object);
        }else
        if(object instanceof Long){
            return longToByte((Long)object);
        }else
        if(object instanceof Short){
            return shortToByte((Short)object);
        }else
        if(object instanceof String){
            return stringToByte((String)object);
        }else
        if(object instanceof Float){
            return floatToByte((Float)object);
        }else
        if(object instanceof Double){
            return doubleToByte((Double)object);
        }else
        if(object instanceof Date){
            return dateToBytes((Date)object);
        }else
        if(object instanceof Date){
            return dateToBytes((Date)object);
        }else if(object.getClass().isArray()){
        }else if(object instanceof List){
        }else if(object instanceof Map){
        }else if(object instanceof Set){
        }else if(object instanceof Queue){
        }else{
            return objectDtoToBytes(object);
        }
        return new byte[0];
    }
    
    
    static byte[] objectDtoToBytes(Object object){
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        fields = sort(fields,0);
        byte[] r = null;
        for(Field field:fields){
            try {
                field.setAccessible(true);
                Object value = field.get(object);
                byte[] bs= toStream(value);
                if(r!=null){
                    r = concat(r,bs);
                }else{
                    r = bs;
                }
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ClassToByte.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ClassToByte.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(r==null){
            r = new byte[0];
        }
        int size = r.length;
        r = concat(intToByte(size),r);
        return r;        
    }
    
    
    public static Field[] sort(Field[] fields,int version){
        return sortVersion1(fields);
    }
 
    /**
     * 版本1的排序方式
     * @param fields
     * @return 
     */
    public static Field[] sortVersion1(Field[] fields){
        Arrays.sort(fields,new Comparator<Field>(){
            @Override
            public int compare(Field o1, Field o2) {
                if(o1==null&&o2==null)return 0;
                if(o1==null&&o2!=null)return -1;
                if(o1!=null&&o2==null)return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
        return fields;  
    }
    
    
    
    public static byte[] intToByte(int val){
        byte[] b = new byte[4];
        b[3] = (byte)(val & 0xff);
        b[2] = (byte)((val >> 8) & 0xff);
        b[1] = (byte)((val >> 16) & 0xff);
        b[0] = (byte)((val >> 24) & 0xff);
        return b;
    }
    
    public static Integer Byte2Int(Byte[]bytes) {
        return (bytes[3]&0xff)<<24
                | (bytes[2]&0xff)<<16
                | (bytes[1]&0xff)<<8
                | (bytes[0]&0xff);
    }
    
   
    
    public static Date bytesToDate(byte[] bytes) {
        long t = byteToLong(bytes);
        return new Date(t);
    }
    
     public static byte[] dateToBytes(Date date) {
        return longToByte(date.getTime());
    }

    /**
     * short到字节数组的转换.
     */
    public static byte[] shortToByte(short number) {
        return shortToByte(number,true);
    }

    /**
     * short到字节数组的转换.
     */
    public static byte[] shortToByte(short number,boolean hl) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = b.length - 1; i >= 0; i--) {
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
            temp = temp >> 8;// 向右移8位
        }
        return b;
    }

    /**
   * 字节数组到short的转换. 
   */
  public static Short byteToShort(byte[] b) { 
    short s = 0; 
    short s0 = (short) (b[1] & 0xff);// 最低位
    short s1 = (short) (b[0] & 0xff);
    s1 <<= 8; 
    s = (short) (s0 | s1); 
    return s; 
  } 

  public static int byteToInt(byte by){
      return  (by&0xff);
  }
   
  /** 
   * 字节数组到int的转换. 
   */
  public static Integer byteToInt(byte[] b) { 
    int s = 0; 
    int s0 = b[3] & 0xff;// 最低位
    int s1 = b[2] & 0xff;
    int s2 = b[1] & 0xff;
    int s3 = b[0] & 0xff;
    s3 <<= 24; 
    s2 <<= 16; 
    s1 <<= 8; 
    s = s0 | s1 | s2 | s3; 
    return s; 
  }





  /** 
   * long类型转成byte数组 
   */
  public static byte[] longToByte(long num) {
      byte[] byteNum = new byte[8];
      for (int ix = 0; ix < 8; ++ix) {
          int offset = 64 - (ix + 1) * 8;
          byteNum[ix] = (byte) ((num >> offset) & 0xff);
      }
      return byteNum;
  }
  
  /** 
   * 字节数组到long的转换. 
   */
  public static Long byteToLong(byte[] byteNum) {
      long num = 0;
      for (int ix = 0; ix < 8; ++ix) {
          num <<= 8;
          num |= (byteNum[ix] & 0xff);
      }
      return num;
  }
    
  /** 
   * double到字节数组的转换. 
   */
  public static byte[] doubleToByte(double num) {  
    byte[] b = new byte[8];  
    long l = Double.doubleToLongBits(num);  
    for (int i = 7; i >=0; i--) {
      b[i] = new Long(l).byteValue();  
      l = l >> 8;  
    } 
    return b; 
  } 
    

    
  /** 
   * float到字节数组的转换. 
   */
  public static byte[] floatToByte(float x) { 
      return null;
    //先用 Float.floatToIntBits(f)转换成int 
  } 
    

  /** 
   * string到字节数组的转换. 
   */
  public static byte[] stringToByte(String str) { 
       try { 
        byte[] v =  str.getBytes("UTF-8"); 
        byte[] l = shortToByte((short)v.length);
        return concat(v,l);
       }catch(Exception e){
          e.printStackTrace();
       }
       return new byte[0];
  }
    

    /** 
   * 合并俩个数组
   */
  static byte[] concat(byte[] a, byte[] b) {
    byte[] c= new byte[a.length+b.length];
    System.arraycopy(a, 0, c, 0, a.length);
    System.arraycopy(b, 0, c, a.length, b.length);
    return c;
}

    /**
     * Hex字符串转byte
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * hex字符串转byte数组
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex) {
        if(inHex!=null){
            inHex = inHex.replaceAll(" ","");
            inHex = inHex.replaceAll("　","");
        }
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            // 奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            // 偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }


    public static void main(String[] args) {
        logger.debug( bytesToHexString(longToByte((short)11)));
        logger.debug(byteToLong(longToByte((short)32))+"");
        String inHex = "03 03 03 03";
        inHex = inHex.replaceAll(" ","");
        logger.debug(inHex);
    }



    /**
     * @Author nio
     * @Description  处理16进制字符串
     * @Date 15:48 2019/11/5
     * @Param
     * @return
     **/
    public static String fixHexStr(String commands){
        String show = "";
        for (int i = 0; i < commands.length(); i += 2) {
            String b = commands.substring(i, i + 2);
            show = show+b+" ";
        }
        return show;
    }



    /**
     * 数组转换成十六进制字符串
     * @return HexString
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2){
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }



}
