package cn.nio.net_connection.common.Encoder;

import cn.nio.net_connection.common.tool.ClassToByte;
import cn.nio.net_connection.common.tool.SnowflakeIdWorker;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName ProxyMess
 * @Author nio
 * @Description //TODO $
 * @Date $ $
 **/
public class ProxyMess {
    static Logger logger = LoggerFactory.getLogger(ProxyMess.class);

    private long messageId = SnowflakeIdWorker.getID();
    private String outId="";
    private byte[] bytes=new byte[0];
    private ConnetType type= ConnetType.data;


    public ProxyMess() {
    }

    public enum ConnetType{
        data,dataall,Connect,disConnect,test,login;
    }



    public ConnetType getType() {
        return type;
    }
    public void setType(ConnetType type) {
        this.type = type;
    }

    public String getOutId() {
        return outId;
    }

    public void setOutId(String outId) {
        this.outId = outId;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    //byte[1]messageId[8]outlength[4]outId[outlength]bytesLength[4]bytes[bytesLength]

    public byte[] toBytes(){

        byte[] outIdByte = outId.getBytes(CharsetUtil.UTF_8);
        int outLength=outIdByte.length;
        int bytesLength = bytes.length;
        byte[] rbytes = new byte[outLength+bytesLength+17];


        rbytes[0] = (byte) type.ordinal();

        byte[] messIdbytes = ClassToByte.longToByte(messageId);
        System.arraycopy(messIdbytes,0,rbytes,1,8);

        byte[] outLengthBytes = ClassToByte.intToByte(outLength);
        System.arraycopy(outLengthBytes,0,rbytes,9,4);
        System.arraycopy(outIdByte,0,rbytes,13,outLength);

        byte[] bytesLengthbytes = ClassToByte.intToByte(bytesLength);
        System.arraycopy(bytesLengthbytes,0,rbytes,13+outLength,4);
        System.arraycopy(bytes,0,rbytes,17+outLength,bytesLength);

        return rbytes;
    }

    //byte[1]messageId[8]outlength[4]outId[outlength]bytesLength[4]bytes[bytesLength]
    public ProxyMess(byte[] bytesAll) throws Exception {

        this.type = ConnetType.values()[bytesAll[0]];

        byte messIdbytes[] = new byte[8];
        System.arraycopy(bytesAll,1,messIdbytes,0,8);
        this.messageId = ClassToByte.byteToLong(messIdbytes);

        byte[] outLengthBytes = new byte[4];
        System.arraycopy(bytesAll,9,outLengthBytes,0,4);
        int outLength = ClassToByte.byteToInt(outLengthBytes);
        byte outIdByte[] = new byte[outLength];
        System.arraycopy(bytesAll,13,outIdByte,0,outLength);
        this.outId = new String(outIdByte,CharsetUtil.UTF_8);

        byte[] bytesLengthbytes = new byte[4];
        System.arraycopy(bytesAll,13+outLength,bytesLengthbytes,0,4);
        int bytesLength = ClassToByte.byteToInt(bytesLengthbytes);
        this.bytes = new byte[bytesLength];
        System.arraycopy(bytesAll,17+outLength,this.bytes,0,bytesLength);

    }




    public static void main(String[] args) throws Exception {
        ProxyMess re = new ProxyMess();
        re.setOutId("99999");
        re.setBytes("111111111111".getBytes(CharsetUtil.UTF_8));
        re.setMessageId(System.currentTimeMillis());
        System.out.println(re.getOutId()+"---"+re.getValue()+"--"+re.getMessageId());
        re = new ProxyMess(re.toBytes());
        System.out.println(re.getOutId()+"---"+re.getValue()+"--"+re.getMessageId());

    }

    public String getValue() {
        return new String(bytes,CharsetUtil.UTF_8);
    }


    public void setValue(String value) {
        if(value==null||value.isEmpty()){
            this.bytes = new byte[0];
        }else{
            this.bytes = value.getBytes(CharsetUtil.UTF_8);
        }
    }
}
