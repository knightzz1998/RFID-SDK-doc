package cn.knightzz.utils;

import com.payne.connect.net.NetworkHandle;
import com.payne.connect.port.SerialPortHandle;
import com.payne.reader.Reader;
import com.payne.reader.base.Consumer;
import com.payne.reader.bean.config.AntennaCount;
import com.payne.reader.bean.receive.Failure;
import com.payne.reader.bean.receive.InventoryFailure;
import com.payne.reader.bean.receive.InventoryTag;
import com.payne.reader.bean.receive.InventoryTagEnd;
import com.payne.reader.bean.receive.Success;
import com.payne.reader.bean.receive.Version;
import com.payne.reader.bean.send.BufferInventory;
import com.payne.reader.bean.send.CustomSessionTargetInventory;
import com.payne.reader.bean.send.InventoryConfig;
import com.payne.reader.process.ReaderImpl;
import com.payne.reader.util.ArrayUtils;
import com.payne.reader.util.ThreadPool;

/**
 * @author 王天赐
 * @title: RfidUtils
 * @projectName RFID-SDK-doc
 * @description:
 * @website http://knightzz.cn/
 * @github https://github.com/knightzz1998
 * @date 2021/12/30 16:26
 */
public class RfidUtils {

    public static void main(String[] args) {
    }


    private static void reader() {


        Reader mReader = ReaderImpl.create(AntennaCount.SIXTEEN_CHANNELS);
        mReader.setOriginalDataCallback(
                new Consumer<byte[]>() {
                    @Override
                    public void accept(byte[] onSend) throws Exception {
                        System.out.println("---reader 1 send :" + ArrayUtils.bytesToHexString(onSend, 0, onSend.length));
                    }
                },
                new Consumer<byte[]>() {
                    @Override
                    public void accept(byte[] onReceive) throws Exception {
                        System.out.println("===reader 1 recv:" + ArrayUtils.bytesToHexString(onReceive, 0, onReceive.length));
                    }
                });

        NetworkHandle handle = new NetworkHandle("172.16.11.107", 4001);


        boolean linkSuccess = mReader.connect(handle);
        if (linkSuccess) {
            System.out.println("reader1 connect success");
        } else {
            throw new RuntimeException("reader1 connect fail");
        }
        System.out.println("reader1 firmware version:");
        mReader.getFirmwareVersion(
                new Consumer<Version>() {
                    @Override
                    public void accept(Version version) throws Exception {
                        System.out.println("reader1 firmware version: v" + version.getVersion());
                    }
                },
                new Consumer<Failure>() {

                    @Override
                    public void accept(Failure failure) throws Exception {
                        System.out.println("reader1 firmware version fail err code: " + (failure.getErrorCode() & 0xFF));
                    }
                });

        System.out.println("reader1 begin inventory:");


        mReader.setWorkAntenna(
                0,
                new Consumer<Success>() {
                    @Override
                    public void accept(Success success) throws Exception {
                        // TODO Auto-generated method stub
                        System.out.println("reader1 setWorkAntenna,  ok: " + (success));
                    }
                },
                new Consumer<Failure>() {

                    @Override
                    public void accept(Failure failure) throws Exception {
                        System.out.println("reader1 setWorkAntennaerr code:" + (failure.getErrorCode() & 0xFF));
                    }
                }
        );


        CustomSessionTargetInventory inventory = new CustomSessionTargetInventory.Builder()
                .build();

        InventoryConfig config = new InventoryConfig.Builder()
                .setInventory(inventory)
                .setOnInventoryTagSuccess(new Consumer<InventoryTag>() {

                    @Override
                    public void accept(InventoryTag tag) throws Exception {
                        System.out.println("reader1 inventory tag :" + tag.getEpc());
                    }
                })
                .setOnInventoryTagEndSuccess(new Consumer<InventoryTagEnd>() {

                    @Override
                    public void accept(InventoryTagEnd arg0) throws Exception {
                        System.out.println("reader1 InventoryTagEnd");
                    }
                })
                .setOnFailure(new Consumer<InventoryFailure>() {

                    @Override
                    public void accept(InventoryFailure failure) throws Exception {
                        System.out.println("reader1 inventory fail :" + (failure.getErrorCode() & 0xFF));
                    }
                })
                .build();
        mReader.setInventoryConfig(config);
        //true -- inventory loop
        mReader.startInventory(true);


    }
}
