package com.nibss.agencybankingservice.wrappers;

import com.nibss.agencybankingservice.exceptions.UnableToDecryptException;
import com.nibss.cryptography.Hasher;
import com.nibss.exceptions.EncryptionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileCopyUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

@Slf4j
public class CustomRequestWrapper extends HttpServletRequestWrapper {


    private Hasher hasher;

    public CustomRequestWrapper(HttpServletRequest request, Hasher hasher) {
        super(request);
        this.hasher = hasher;
    }

    @Override
    public BufferedReader getReader() throws IOException, UnableToDecryptException {
        String content = FileCopyUtils.copyToString(super.getReader());
        log.trace("request body from reader before decryption: {}", content);

        String decrypted;
        try {
            decrypted = hasher.decrypt(content);
        } catch (EncryptionException e) {
            throw new UnableToDecryptException("could not decrypt request",e);
        }
        log.trace("request body after decryption: {}", decrypted);
        return new BufferedReader(new StringReader(decrypted));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException, UnableToDecryptException {

        byte[] content = FileCopyUtils.copyToByteArray(super.getInputStream());
        String encrypted = new String(content);
        log.trace("request body before decryption: {}", encrypted);

        String decrypted;
        try {
            decrypted = hasher.decrypt(encrypted);
        } catch (EncryptionException e) {
            throw new UnableToDecryptException("could not decrypt request",e);
        } catch (Exception e) {
            throw new UnableToDecryptException("could not decrypt request",e);
        }
        log.trace("request body after decryption: {}", decrypted);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(decrypted.getBytes());

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return inputStream.available() <= 0;
            }

            @Override
            public boolean isReady() {
                return inputStream.available() > 0;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                
            }

            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
        };
    }
}
