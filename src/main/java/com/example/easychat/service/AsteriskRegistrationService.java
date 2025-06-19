package com.example.easychat.service;

import com.example.easychat.entity.po.PsAor;
import com.example.easychat.entity.po.PsAuth;
import com.example.easychat.entity.po.PsEndpoint;
import com.example.easychat.repository.PsAorRepository;
import com.example.easychat.repository.PsAuthRepository;
import com.example.easychat.repository.PsEndpointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsteriskRegistrationService {

    @Autowired
    private PsEndpointRepository endpointRepository;

    @Autowired
    private PsAorRepository aorRepository;

    @Autowired
    private PsAuthRepository authRepository;

    @Transactional("asteriskTransactionManager")
    public void registerAccount(String username, String password) {
        PsAuth auth = new PsAuth();
        auth.setId(username + "-auth");
        auth.setAuthType("userpass");
        auth.setPassword(password);
        auth.setUsername(username);
        authRepository.save(auth);

        PsAor aor = new PsAor();
        aor.setId(username);
        aor.setMaxContacts(10);
        aorRepository.save(aor);

        PsEndpoint endpoint = new PsEndpoint();
        endpoint.setId(username);
        endpoint.setTransport("transport-udp");
        endpoint.setAors(username);
        endpoint.setAuth(username + "-auth");
        endpoint.setContext("default");
        endpoint.setDisallow("all");
        endpoint.setAllow("ulaw,alaw,h264,vp8");
        endpoint.setDirectMedia("no");
        endpoint.setMaxVideoStreams(1);
        endpoint.setRtpSymmetric("yes");
        endpoint.setForceRport("yes");
        endpoint.setRewriteContact("yes");
        endpointRepository.save(endpoint);
    }
}
