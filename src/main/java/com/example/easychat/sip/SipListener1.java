package com.example.easychat.sip;

import javax.sip.*;
import javax.sip.message.*;
import java.text.ParseException;
import java.util.Properties;
import java.util.TooManyListenersException;

public class SipListener1 implements SipListener {

    private SipProvider sipProvider;
    private SipFactory sipFactory;

    public void init() throws PeerUnavailableException, TransportNotSupportedException, InvalidArgumentException, ObjectInUseException, TooManyListenersException {
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("javax.sip");

        // 创建一个 SIP 实体
        SipStack sipStack = sipFactory.createSipStack(new Properties());
        ListeningPoint listeningPoint = sipStack.createListeningPoint("localhost", 5060, "udp");
        sipProvider = sipStack.createSipProvider(listeningPoint);

        // 添加 SIP 监听器
        sipProvider.addSipListener(this);
    }

    @Override
    public void processRequest(RequestEvent requestEvent) {
        Request request = requestEvent.getRequest();
        try {
            if (request.getMethod().equals(Request.INVITE)) {
                // 处理邀请请求（发起呼叫）
                respondToInvite(requestEvent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void respondToInvite(RequestEvent requestEvent) throws SipException, InvalidArgumentException, ParseException {
        // 响应 INVITE 请求以建立语音通话
        Response response = sipFactory.createMessageFactory().createResponse(200, (Request) requestEvent.getRequest());
        sipProvider.sendResponse(response);
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {
        // 处理响应
    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        // 超时处理
    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {
        // IO异常处理
    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        // 事务终止处理
    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        // 对话终止处理
    }
}
