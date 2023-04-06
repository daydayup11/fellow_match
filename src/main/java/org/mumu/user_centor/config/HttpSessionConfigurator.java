package org.mumu.user_centor.config;

import org.mumu.user_centor.common.ErrorCode;
import org.mumu.user_centor.exception.BusinessException;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class HttpSessionConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        //获取httpsession
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        if(httpSession == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //将httpsession保存起来
        sec.getUserProperties().put(HttpSession.class.getName(),httpSession);
    }
}