package com.blog.payload;

import com.alibaba.fastjson2.JSONObject;
import com.blog.service.CommentService;
import com.blog.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint(value = "/websocket/comment/{username}")
@Slf4j
public class WebsocketComment {
    private static final ConcurrentHashMap<String, WebSocket> WEB_SOCKET_MAP = new ConcurrentHashMap<>();
    private static CommentService commentService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static final AtomicInteger userCount = new AtomicInteger(0);
    @Autowired
    public void setCommentService(CommentService commentService) {
        WebsocketComment.commentService = commentService;
    }

    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session, EndpointConfig config) {
        WebSocket webSocket = new WebSocket();
        webSocket.setSession(session);
        webSocket.setUsername(username);
        WEB_SOCKET_MAP.put(username, webSocket);
        increaseUserCount();
        log.info("open webSocket:{}", webSocket);
    }

    @OnMessage
    public void onMessage(@PathParam("username") String username,String msg, Session session) throws JsonProcessingException {
        if(StringUtils.isBlank(msg) || msg.isEmpty()) {
            return;
        }
        String json = msg.substring(0, msg.indexOf("}") + 1);
        log.info("收到訊息:{}", json);
        WsMessage wsMessage = JsonUtil.parseObject(json, WsMessage.class);
        executorService.execute(() -> {
            String action = wsMessage.getAction();
            if ("comment-like".equals(action)){
            long postIdLongValue = Long.parseLong(wsMessage.getPostId());
            long commentIdLongValue = Long.parseLong(wsMessage.getCommentId());
            commentService.addCommentLike(postIdLongValue, commentIdLongValue);
            Long commentLikeCount = commentService.getCommentLikeCount(commentIdLongValue);
            //回傳訊息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("commentId", commentIdLongValue);
            jsonObject.put("eventType", "comment-like");
            jsonObject.put("likeCount", commentLikeCount);
            try {
                sendMsgTo(username, jsonObject.toJSONString());
            } catch(IOException e) {
                log.error("websocket error: {}", e.getMessage());
            }
        } else if ("comment-dislike".equals(action)){
            long postIdLongValue = Long.parseLong(wsMessage.getPostId());
            long commentIdLongValue = Long.parseLong(wsMessage.getCommentId());
            commentService.addCommentDisLike(postIdLongValue, commentIdLongValue);
            Long disLikeCount = commentService.getCommentDisLikeCount(commentIdLongValue);
            //回傳訊息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("commentId", commentIdLongValue);
            jsonObject.put("eventType", "comment-dislike");
            jsonObject.put("disLikeCount", disLikeCount);
            try {
                sendMsgTo(username, jsonObject.toJSONString());
            } catch(IOException e) {
                log.error("websocket error: {}", e.getMessage());
            }}
        });
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("websocket error: {}", error.getMessage());
    }

    @OnClose
    public void onClose(Session session) {
        String username = session.getPathParameters().get("username");
        log.info("close webSocket:{}", username);
        WEB_SOCKET_MAP.remove(username);
        decreaseUserCount();
    }

    public void sendMsgTo(String username, String msg) throws IOException {
        WebSocket webSocket = WEB_SOCKET_MAP.get(username);
        if(webSocket != null){
            webSocket.getSession().getBasicRemote().sendText(msg);
        }
    }

    public void sendMsgToAll(String msg) throws IOException {
        for (WebSocket webSocket : WEB_SOCKET_MAP.values()) {
            webSocket.getSession().getBasicRemote().sendText(msg);
        }
    }
    public synchronized void increaseUserCount(){
        userCount.incrementAndGet();
    }

    public synchronized void decreaseUserCount(){
        userCount.decrementAndGet();
    }

    public synchronized Map<String, WebSocket> getWebSocketMap(){
        return WEB_SOCKET_MAP;
    }

}
