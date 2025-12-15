package kernel.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

/**
 * 提供高精度的运算支持.
 * 所以函数以double为参数类型，兼容int与float.
 * 
 */
public abstract class NumberUtils extends org.springframework.util.NumberUtils {

    /**
     * 精确的加法运算.
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).doubleValue();
    }

    /**
     * 
     * 精确的减法运算.
     * 
     * @param v1 被减数
     * @param v2 减数
     */
    public static double subtract(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算.
     */
    public static double multiply(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算，并对运算结果截位.
     * 
     * @param scale 运算结果小数后精确的位数
     */
    public static double multiply(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算.
     * 
     * @see #divide(double, double, int)
     */
    public static double divide(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算.
     * 由scale参数指定精度，以后的数字四舍五入.
     * 
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位
     */
    public static double divide(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }

        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理.
     * 
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(v);
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static int compare(Number left, Number right) throws IllegalArgumentException {
        Assert.notNull(left, "left number must not be null");
        Assert.notNull(right, "right number must not be null");

        if (left.getClass().equals(Byte.class)) {
            return ((Byte) left).compareTo((Byte) right);
        }
        else if (left.getClass().equals(Short.class)) {
            return ((Short) left).compareTo((Short) right);
        }
        else if (left.getClass().equals(Integer.class)) {
            return ((Integer) left).compareTo((Integer) right);
        }
        else if (left.getClass().equals(Long.class)) {
            return ((Long) left).compareTo((Long) right);
        }
        else if (left.getClass().equals(BigInteger.class)) {
            return ((BigInteger) left).compareTo((BigInteger) right);
        }
        else if (left.getClass().equals(Float.class)) {
            return ((Float) left).compareTo((Float) right);
        }
        else if (left.getClass().equals(Double.class)) {
            return ((Double) left).compareTo((Double) right);
        }
        else if (left.getClass().equals(BigDecimal.class)) {
            return ((BigDecimal) left).compareTo((BigDecimal) right);
        }
        else {
            throw new IllegalArgumentException("Could not compare left number [" + left + "] of type ["
                    + left.getClass().getName() + "] to right number [" + right + "] of type ["
                    + right.getClass().getName() + "]");
        }
    }

    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    /**
     * �����ָ�ʽ��Ϊ��Ǯ��ʽ
     * 
     * @param number
     *            ���ʽ����BigDecimal
     * @return С����2λ��ʽ���ַ�
     */
    public static String formatNumericFee(BigDecimal number) {
        return number.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * �����ָ�ʽ��Ϊ����ʽ
     * 
     * @param number
     *            ���ʽ����BigDecimal
     * @return С����6λ��ʽ���ַ�
     */
    public static String formatNumericTraffic(BigDecimal number) {
        return number.setScale(6, BigDecimal.ROUND_HALF_UP).toString();
    }

    // ���һ��ת����������bigdecimalת��ΪTB��GB��MB����ʽ
    public static String mbToTGM(BigDecimal mb) {
        StringBuffer sb = new StringBuffer(200);
        int g = 0;
        int t = 0;
        BigDecimal m = new BigDecimal(0);

        t = new Float(mb.divide(new BigDecimal(1024 * 1024), BigDecimal.ROUND_DOWN).floatValue()).intValue();
        mb = mb.subtract((new BigDecimal(t).multiply(new BigDecimal(1024 * 1024))));
        g = new Float(mb.divide(new BigDecimal(1024), BigDecimal.ROUND_DOWN).floatValue()).intValue();
        mb = mb.subtract((new BigDecimal(g).multiply(new BigDecimal(1024))));
        m = mb.setScale(2, BigDecimal.ROUND_HALF_UP);

        sb.append(new Integer(t).toString()).append(" TB ");

        sb.append(new Integer(g).toString()).append(" GB ");

        sb.append(m.toString()).append(" MB ");

        return sb.toString();
    }

    // ��value��ǰ4���ֽڶs����һ�������
    public static Integer readInt(byte[] value) {
        if (value == null || value.length < 4) {
            return null;
        }
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(value);
            DataInputStream dis = new DataInputStream(bais);
            Integer retval = dis.readInt();
            bais.close();
            dis.close();
            return retval;
        } catch (Exception e) {
            return null;
        }
    }

    // ��value��ǰ}���ֽڶs����һ�������
    public static Short readShort(byte[] value) {
        if (value == null || value.length < 2) {
            return null;
        }
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(value);
            DataInputStream dis = new DataInputStream(bais);
            Short retval = dis.readShort();
            bais.close();
            dis.close();
            return retval;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] intToByteArray(Integer value) {
        if (value == null) {
            return null;
        }

        try {
            byte[] data = null;
            ByteArrayOutputStream bais = new ByteArrayOutputStream(4);
            DataOutputStream dos = new DataOutputStream(bais);
            dos.writeInt(value);
            data = bais.toByteArray();
            bais.close();
            dos.close();
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] longToByteArray(long value) {
        try {
            byte[] data = null;
            ByteArrayOutputStream bais = new ByteArrayOutputStream(8);
            DataOutputStream dos = new DataOutputStream(bais);
            dos.writeLong(value);
            data = bais.toByteArray();
            bais.close();
            dos.close();
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public static Long readUnsingedInt(byte[] data, int offset) {
        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
            dis.skip(offset);
            Long retval = null;
            int tmp = dis.readInt();
            if (tmp < 0) {
                retval = tmp + 4294967296L;
            }
            else {
                retval = Long.valueOf(tmp);
            }

            dis.close();
            return retval;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}