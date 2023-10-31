package com.casko1.wheelbarrow.bot.server;

import com.casko1.wheelbarrow.bot.Wheelbarrow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ApiMessageServer {

    ServerSocket serverSocket;
    Socket socket = null;

    Logger logger = LoggerFactory.getLogger(Wheelbarrow.class);

    public ApiMessageServer() {
        try {
            serverSocket = new ServerSocket(9090, 0, InetAddress.getByName(null));
            logger.info("API functionality enabled. Server socket created.");
            waitForConnection();
        } catch (IOException e) {
            logger.error("Creating server socket failed!");
        }
    }


    public void startListening() throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        String str;
        while (!(str = dis.readUTF()).equals("exit")) {
            System.out.println("message=" + str);
        }
        logger.info("Client closed connection! Waiting for new connection...");
        socket.close();
        waitForConnection();
    }

    private void waitForConnection() {
        try {
            socket = serverSocket.accept();
            logger.info("Connection to API client established, listening for messages...");
            startListening();
        } catch (IOException e) {
            logger.warn("Accepting connection failed or connection closed by remote host!");
            waitForConnection();
        }
    }
}
