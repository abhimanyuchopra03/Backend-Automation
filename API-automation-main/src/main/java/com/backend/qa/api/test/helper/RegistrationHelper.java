package com.backend.qa.api.test.helper;


import com.backend.qa.api.dto.RequestDTO;
import com.backend.qa.apihelper.ApiHelper;
import com.backend.qa.util.PropertiesUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.backend.qa.api.test.requestDTO.RegisterRequestDto;
import com.backend.qa.apibase.APITestBase;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.HttpClients;

public class RegistrationHelper extends APITestBase {
   ApiHelper apihelper;
   String baseUrl = null;
  public RegistrationHelper(){
    this.apihelper = new ApiHelper();
    this.baseUrl = PropertiesUtil.getEnvConfigProperty("baseUserAPI");

  }
  ObjectMapper objectMapper = new ObjectMapper();
  public HttpResponse registerationHelper(String userName,String password)
      throws IOException {
    String requestString = objectMapper.writeValueAsString(
        RegisterRequestDto.builder().username(userName).password(password).build());
    RequestDTO requestDTORegistration = new RequestDTO();
    requestDTORegistration.setRequestBody(requestString);
    requestDTORegistration.setApiPath(PropertiesUtil.getEnvConfigProperty("registrationUserAPI"));
    requestDTORegistration.setMethodType("POST");
    requestDTORegistration.setResponseCode(200);
    httpClient = HttpClients.createDefault();
    return apihelper.createRequest(requestDTORegistration, baseUrl, httpClient, httpContext, false);
  }

  public HttpResponse LoginHelper(String userName,String password)
      throws IOException {
    String requestString = objectMapper.writeValueAsString(
        RegisterRequestDto.builder().username(userName).password(password).build());
    RequestDTO requestDTORegistration = new RequestDTO();
    requestDTORegistration.setRequestBody(requestString);
    requestDTORegistration.setApiPath(PropertiesUtil.getEnvConfigProperty("LoginUserAPI"));
    requestDTORegistration.setMethodType("POST");
    requestDTORegistration.setResponseCode(200);
    httpClient = HttpClients.createDefault();
    return apihelper.createRequest(requestDTORegistration, baseUrl, httpClient, httpContext, false);
  }





}
