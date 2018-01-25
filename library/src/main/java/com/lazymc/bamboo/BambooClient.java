package com.lazymc.bamboo;

import android.net.LocalSocket;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

class BambooClient implements IBambooServer {

    private final LocalSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean isConnected;

    public BambooClient(LocalSocket socket) throws IOException {
        isConnected = true;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        this.socket = socket;
    }

    @Override
    public boolean write(String key, String data) throws Exception {

        final ResultWrapper<Boolean> result = new ResultWrapper<>(false);

        JSONObject object = new JSONObject();
        try {
            object.put("key", key);
            object.put("value", data);
            object.put("op", "set");
            String value = object.toString();
            outputStream.write(value.getBytes());
            outputStream.write('\0');

            String res = null;
            res = readFrom();
            if ("ok".equals(res)) {
                result.set(true);
            }

            return result.get();
        } catch (Exception e) {
            close();
            throw e;
        }
    }

    @Override
    public String read(String key) throws Exception {

        final ResultWrapper<String> result = new ResultWrapper<>("");

        JSONObject object = new JSONObject();
        try {
            object.put("key", key);
            object.put("op", "get");
            String value = object.toString();
            outputStream.write(value.getBytes());
            outputStream.write('\0');

            String res = null;
            try {
                res = readFrom();
                result.set(res);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result.get();
        } catch (Exception e) {
            close();
            throw e;
        }
    }

    @Override
    public boolean cut(String key) throws Exception {
        final ResultWrapper<Boolean> result = new ResultWrapper<>(false);

        JSONObject object = new JSONObject();
        try {
            object.put("key", key);
            object.put("op", "cut");
            String value = object.toString();
            outputStream.write(value.getBytes());
            outputStream.write('\0');

            String res = null;
            res = readFrom();
            if ("ok".equals(res)) {
                result.set(true);
            }
        } catch (Exception e) {
            close();
            throw e;
        }

        return result.get();
    }

    @Override
    public boolean remove(String key) throws Exception {
        final ResultWrapper<Boolean> result = new ResultWrapper<>(false);

        JSONObject object = new JSONObject();
        try {
            object.put("key", key);
            object.put("op", "remove");
            String value = object.toString();
            outputStream.write(value.getBytes());
            outputStream.write('\0');

            String res = null;
            res = readFrom();
            if ("ok".equals(res)) {
                result.set(true);
            }
        } catch (Exception e) {
            close();
            throw e;
        }

        return result.get();
    }

    @Override
    public boolean clearRef() throws Exception {
        final ResultWrapper<Boolean> result = new ResultWrapper<>(false);

        JSONObject object = new JSONObject();
        try {
            object.put("op", "clearRef");
            String value = object.toString();
            outputStream.write(value.getBytes());
            outputStream.write('\0');

            String res = null;
            res = readFrom();
            if ("ok".equals(res)) {
                result.set(true);
            }
            return result.get();
        } catch (Exception e) {
            close();
            throw e;
        }
    }

    @Override
    public boolean isClose() {
        return !isConnected;
    }

    public void close() {
        isConnected = false;
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String readFrom() throws IOException {
        byte[] buffer = new byte[1024];
        int read = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (read != -1) {
            read = inputStream.read(buffer);
            if (read > 0) {
                bos.write(buffer, 0, read);
                if (buffer[read - 1] == '\0') {
                    return new String(bos.toByteArray()).trim();
                }
            }
        }
        return "";
    }
}
