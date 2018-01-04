package com.lazymc.bamboo;

import android.net.LocalServerSocket;
import android.net.LocalSocket;

import java.io.FileNotFoundException;
import java.io.IOException;

import static com.lazymc.bamboo.IOServer.THREAD_SERVICE;


/**
 * Created by longyu on 2017/12/18.
 * ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　＞　　　＜　┃
 * ┃　　　　　　　┃
 * ┃...　⌒　...　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 * ┃　　　┃
 * ┃　　　┃
 * ┃　　　┃
 * ┃　　　┃  神兽保佑
 * ┃　　　┃  代码无bug
 * ┃　　　┃
 * ┃　　　┗━━━┓
 * ┃　　　　　　　┣┓
 * ┃　　　　　　　┏┛
 * ┗┓┓┏━┳┓┏┛
 * ┃┫┫　┃┫┫
 * ┗┻┛　┗┻┛
 * <p>
 * 如果生命可以延续，代码也将永无止境。
 * bug的不期而遇，请接受加班的惩罚。
 */

class BambooServer implements IBambooServer {

    private LocalServerSocket serverSocket;
    private IOServer ioServer;

    public BambooServer(LocalServerSocket serverSocket, final IOServer ioServer) {
        this.serverSocket = serverSocket;
        this.ioServer = ioServer;
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    final LocalSocket client;
                    try {
                        client = BambooServer.this.serverSocket.accept();
                        THREAD_SERVICE.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    new ResponseInvoker(BambooServer.this, client);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }.start();
    }

    @Override
    public boolean write(final String key, String data) {
        int dataLen=data.getBytes().length;
        byte[] buffer = new byte[dataLen + 2];
        System.arraycopy(data.getBytes(), 0, buffer, 1, dataLen);
        buffer[buffer.length - 1] = Bamboo.END_TAG;
        buffer[0] = Bamboo.END_TAG;
        return ioServer.write(key, buffer);
    }

    @Override
    public String read(String key) {
        byte[] read = ioServer.read(key);
        if (read == null || read.length == 0 || read[read.length - 1] != Bamboo.END_TAG || read[0] != Bamboo.END_TAG) {

            return "";
        }
        byte[] source = new byte[read.length - 2];
        System.arraycopy(read, 1, source, 0, source.length);
        return new String(source);
    }

    @Override
    public boolean cut(String key) {
        return ioServer.cut(key);
    }

    @Override
    public boolean remove(String key) throws Exception {
        return ioServer.remove(key);
    }

    @Override
    public boolean clearRef() throws Exception {
        ioServer.clearRef();
        return true;
    }

    @Override
    public boolean isClose() {
        return false;
    }
}
