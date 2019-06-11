package com.demo.vrdemo.client;

import org.apache.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class VRDemoClient {

    private static Logger logger = Logger.getLogger(VRDemoClient.class);

    private final static WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    private WebSocketStompClient stompClient;
    private StompSession stompSession;

    public VRDemoClient() {
        try {
            connect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void connect() {

        Transport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
        List<Transport> transports = Collections.singletonList(webSocketTransport);

        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());

        this.stompClient = new WebSocketStompClient(sockJsClient);

        String url = "ws://{host}:{port}/vrp-websocket";
        ListenableFuture<StompSession> f = stompClient.connect(url, headers, new ConnectedHandler(), "localhost", 8080);
        try {
            stompSession = f.get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendClear() {
        if(!stompSession.isConnected())
            connect();

        stompSession.send("/app/clear", "{}".getBytes());
    }

    public void sendLocation(String lat, String lng, String locName) {
        if(!stompSession.isConnected())
            connect();

        String location = "{\"lat\":" + lat + ",\"lng\":" + lng + ",\"description\":\"" + locName + "\"}";
        stompSession.send("/app/location", location.getBytes());
    }

    private class ConnectedHandler extends StompSessionHandlerAdapter {
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
            logger.info("Now connected");
        }
    }

    public StompSession getStompSession() {
        return stompSession;
    }
    
    public static void main(String[] args) throws Exception {
        VRDemoClient vrDemoClient = new VRDemoClient();

        StompSession stompSession = vrDemoClient.getStompSession();

        logger.info("Sending clear message");
        vrDemoClient.sendClear();

        Thread.sleep(2000);

        logger.info("Sending location messages");
        vrDemoClient.sendLocation("51.000000000000000", "5.000000000000000", "My Location 1");
        vrDemoClient.sendLocation("50.000000000000000", "5.000000000000000", "My Location 2");
        vrDemoClient.sendLocation("51.000000000000000", "4.000000000000000", "My Location 3");
        vrDemoClient.sendLocation("50.500000000000000", "4.500000000000000", "My Location 4");
        logger.info("Sent location messages");
        
        stompSession.disconnect();
        //vrDemoClient.stompClient.stop();
    }   
}
