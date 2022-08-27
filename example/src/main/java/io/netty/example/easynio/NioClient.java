package io.netty.example.easynio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class NioClient {
    public static void main(String[] args) throws Exception {
        NioClient nioClient = new NioClient();
    }

    private SocketChannel clientSocketChannel;
    private Selector selector;
    private final List<String> responseQueue = new ArrayList<String>();
    private CountDownLatch connected = new CountDownLatch(1);

    public NioClient() throws Exception {
        clientSocketChannel = SocketChannel.open();
        clientSocketChannel.configureBlocking(false);
        selector = Selector.open();
        clientSocketChannel.register(selector, SelectionKey.OP_CONNECT);
        clientSocketChannel.connect(new InetSocketAddress(8080));

        new Thread(() -> {
            try {
                handleKeys();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        if (connected.getCount() != 0) {
            connected.await();
        }

        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String s = scanner.nextLine();
                try {
                    clientSocketChannel.write(ByteBuffer.wrap(s.getBytes("UTF-8")));
                    clientSocketChannel.register(selector, SelectionKey.OP_READ, responseQueue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        System.err.println("client 启动完成 ");
    }

    private void handleKeys() throws Exception {
        while (true) {
            int selectNums = selector.select(30 * 1000L);
            if (selectNums == 0) {
                continue;
            }
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
        if (key.isConnectable()) {
            handleConnectableKey(key);
        }
        if (key.isReadable()) {
            handleReadableKey(key);
        }
        if (key.isWritable()) {
            handleWritableKey(key);
        }
    }

    private void handleWritableKey(SelectionKey key) throws Exception {
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        List<String> responseQueue = (List<String>) key.attachment();
        if (responseQueue.isEmpty()) {
            clientSocketChannel.register(selector, SelectionKey.OP_READ, responseQueue);
            return;
        }
        for (String content : responseQueue) {
            clientSocketChannel.write(ByteBuffer.wrap(content.getBytes("UTF-8")));
        }
        responseQueue.clear();
        clientSocketChannel.register(selector, SelectionKey.OP_READ, responseQueue);
    }

    private void handleReadableKey(SelectionKey key) throws Exception {
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        int read = clientSocketChannel.read(readBuffer);
        System.err.println("读取到了：" + read + "个字节");
        if (readBuffer.position() > 0) {
            readBuffer.flip();
            byte[] bytes = new byte[readBuffer.remaining()];
            readBuffer.get(bytes);
            String body = new String(bytes, "UTF-8");
            System.err.println("读取数据：" + body);
            clientSocketChannel.register(selector, SelectionKey.OP_WRITE, key.attachment());
        }
    }

    private void handleConnectableKey(SelectionKey key) throws Exception {
        if (!clientSocketChannel.isConnectionPending()) {
            return;
        }
        clientSocketChannel.finishConnect();
        System.err.println("接收新的Channel");
        clientSocketChannel.register(selector, SelectionKey.OP_READ, responseQueue);
        connected.countDown();
    }
}