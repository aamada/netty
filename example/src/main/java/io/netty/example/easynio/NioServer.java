package io.netty.example.easynio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NioServer {
    public static void main(String[] args) throws Exception {
        NioServer nioServer = new NioServer();
    }
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public NioServer() throws Exception {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        selector = Selector.open();
        SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        key.attach(new Cjm());
        System.err.println("server 启动完成");
        handleKeys();
    }

    public static class Cjm {
        private String name = "cjm";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private void handleKeys() throws Exception {
        while (true) {
            int selectNum = selector.select(10 * 1000L);
            if (selectNum == 0) {
                continue;
            }
            System.err.println("准备好的数量：" + selectNum);
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (!key.isValid()) {
                    continue;
                }
                try {
                    handleKey(key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleKey(SelectionKey key) throws Exception {
        if (key.isAcceptable() && key.isValid()) {
            handleAcceptableKey(key);
        }
        if (key.isReadable() && key.isValid()) {
            try {
                handleReadableKey(key);
            } catch (Exception e) {
                key.cancel();
                key.channel().close();
                System.err.println(e.getMessage());
                return;
            }
        }
        if (key.isWritable() && key.isValid()) {
            handleWritableKey(key);
        }
    }

    private void handleWritableKey(SelectionKey key) throws Exception {
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        List<String> responseQueue = (List<String>) key.attachment();
        for (String content : responseQueue) {
            System.err.println("写入返回的数据：" + content);
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.put(content.getBytes("UTF-8"));
            byteBuffer.flip();
            clientSocketChannel.write(byteBuffer);
        }
        responseQueue.clear();
        clientSocketChannel.register(selector, SelectionKey.OP_READ, responseQueue);
    }

    private void handleReadableKey(SelectionKey key) throws Exception {
        if (!key.isValid()) {
            key.cancel();
            key.channel().close();
            return;
        }
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        int read = clientSocketChannel.read(readBuffer);
        System.err.println("读取到了"+read+"个字节");
        if (readBuffer.position() > 0) {
           readBuffer.flip();
            byte[] bytes = new byte[readBuffer.remaining()];
            readBuffer.get(bytes);
            String body = new String(bytes, "UTF-8");
            System.err.println("收到的消息：" + body);
            List<String> responseQueue = (List<String>) key.attachment();
            responseQueue.add("响应：" + body);
            clientSocketChannel.register(selector, SelectionKey.OP_WRITE, key.attachment());

        }
    }

    private void handleAcceptableKey(SelectionKey key) throws Exception {
        SocketChannel clientSocketChannel = ((ServerSocketChannel)key.channel()).accept();
        clientSocketChannel.configureBlocking(false);
        System.err.println("接收新的Channel");
        clientSocketChannel.register(selector, SelectionKey.OP_READ, new ArrayList<String>());
    }
}