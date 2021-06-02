package com.davi;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

public class App 
{
    public static void main( String[] args )
    {
        Configuration config = new Configuration();
        config.setPort(8080);

        config.setAuthorizationListener(new AuthorizationListener() {
            public boolean isAuthorized(HandshakeData handshakeData) {
                System.out.println("yes!" + handshakeData.getAddress());
                return true;
            }
        });
        final SocketIOServer server = new SocketIOServer(config);

        server.addEventListener("message", String.class, new DataListener<String>() {

            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception {
                System.out.println("hey");
            }
        });
        server.start();
    }
}
