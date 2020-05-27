package com.messagex.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.messagex.api.request.AuthoriseRequest;
import com.messagex.api.request.Mail;
import com.messagex.api.response.AuthoriseResponse;
import com.messagex.config.MessagexOptions;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Messagex {
  private static ObjectMapper mapper = new ObjectMapper();

  private MessagexOptions messagexOptions;
  private final CloseableHttpClient httpClient = HttpClients.createDefault();

  static {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public Messagex(MessagexOptions messagexOptions) {
    this.messagexOptions = messagexOptions;
  }

  public AuthoriseResponse authenticate() throws IOException, AuthenticationException {
    AuthoriseRequest authoriseRequest = new AuthoriseRequest(messagexOptions);
    String authoriseRequestStr = new String();
    try {
      authoriseRequestStr = mapper.writeValueAsString(authoriseRequest);
    } catch (JsonProcessingException ex) {
      throw ex;
    }
    AuthoriseResponse authoriseResponse = new AuthoriseResponse();
    HttpPost httpRequest = new HttpPost(messagexOptions.getBaseUrl() + "/api/authorise");
    httpRequest.setHeader("Content-Type", "application/json");
    try {
      httpRequest.setEntity(new StringEntity(authoriseRequestStr));
      try {
        CloseableHttpResponse httpResponse = httpClient.execute(httpRequest);
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
          JsonNode httpResponseNode = mapper.readTree(EntityUtils.toString(httpResponse.getEntity()));
          authoriseResponse = mapper.readValue(httpResponseNode.get("data").asText(), AuthoriseResponse.class);
          return authoriseResponse;
        } else {
          throw new AuthenticationException("MessageX Authentication Failed");
        }
      } catch (ClientProtocolException ex) {
        throw ex;
      } catch (IOException ex) {
        throw ex;
      }
    } catch (UnsupportedEncodingException ex) {
      throw ex;
    }
  }

  public void sendMail(Mail mail) {
    
  }

}
