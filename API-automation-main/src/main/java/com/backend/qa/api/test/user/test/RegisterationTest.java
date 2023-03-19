package com.backend.qa.api.test.user.test;


import com.backend.qa.api.dto.RequestDTO;
import com.backend.qa.apihelper.ApiHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.backend.qa.api.test.helper.RegistrationHelper;
import com.backend.qa.api.test.requestDTO.RegisterRequestDto;
import com.backend.qa.apibase.APITestBase;
import com.backend.qa.listener.TestListener;
import com.backend.qa.util.DataProviderUtil;
import com.backend.qa.util.LoggerUtil;
import com.backend.qa.util.LoggerUtil.LogLevel;
import com.backend.qa.util.PropertiesUtil;
import com.backend.qa.util.DataProviderUtil.mapKeys;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.HttpClients;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class RegisterationTest extends APITestBase {

  static String baseUrl = null;
  static HashMap<String, String> registredUser;
  ApiHelper apihelper = null;
  HttpResponse httpResponse = null;
  RequestDTO requestDTO = null;
  String apiResponse = null;
  int responseCode = 0;
  int failureCount = 0;
  ObjectMapper objectMapper = new ObjectMapper();
  RegistrationHelper registrationHelper;
  static String username;
  static String password;

  @BeforeClass
  public void initializeClass() throws NumberFormatException, Exception {
    LoggerUtil
        .setlog(LogLevel.ONLYLOGS, "Initializing class variables for PETRA-Builder Services MIDL");
    apihelper = new ApiHelper();
    registredUser = new HashMap<>();
    baseUrl = PropertiesUtil.getEnvConfigProperty("baseUserAPI");
    username = java.util.UUID.randomUUID().toString().substring(0, 5);
    password = PropertiesUtil.getConstantProperty("password");
    this.registrationHelper = new RegistrationHelper();
  }

  @BeforeMethod
  public void beforeMethod()
      throws SQLException, ClientProtocolException, URISyntaxException, IOException {
    failureCount = 0;
  }

  @Test(dataProvider = "RegisterUser", priority = 1, enabled = true)
  public void testRegisterUser(String testName, HashMap<String, Object> requestDtoMap)
      throws IOException {
    requestDTO = (RequestDTO) requestDtoMap.get(mapKeys.reqDTO.toString());
    String requestString = objectMapper.writeValueAsString(
        RegisterRequestDto.builder().username(username).password(password).build());
    requestDTO.setRequestBody(requestString);
    httpClient = HttpClients.createDefault();
    httpResponse = apihelper.createRequest(requestDTO, baseUrl, httpClient, httpContext, false);
    responseCode = httpResponse.getStatusLine().getStatusCode();
    Assert.assertEquals(responseCode, 200);
  }


  @Test(dataProvider = "LoginUser", priority = 2, enabled = true)
  public void testLoginAPI(String testName, HashMap<String, Object> requestDtoMap)
      throws IOException {
    requestDTO = (RequestDTO) requestDtoMap.get(mapKeys.reqDTO.toString());
    RegisterRequestDto registerRequestDto = RegisterRequestDto.builder()
        .username(username).password(password).build();
    String requestString = objectMapper.writeValueAsString(registerRequestDto);
    requestDTO.setRequestBody(requestString);
    httpClient = HttpClients.createDefault();
    httpResponse = apihelper.createRequest(requestDTO, baseUrl, httpClient, httpContext, false);
    responseCode = httpResponse.getStatusLine().getStatusCode();
    Assert.assertEquals(responseCode, 200);

  }


  @DataProvider(name = "RegisterUser", parallel = false)
  public Object[][] registerUser() throws Exception {
    Object[][] sheetArray = DataProviderUtil.provideDataMap(
        PropertiesUtil.getConstantProperty("TestData_API_User"), "getUsers_v1",
        "RegisterUser", true);
    return DataProviderUtil.sheetMapToDPMap(sheetArray);
  }

  @DataProvider(name = "LoginUser", parallel = false)
  public Object[][] loginUser() throws Exception {
    Object[][] sheetArray = DataProviderUtil.provideDataMap(
        PropertiesUtil.getConstantProperty("TestData_API_User"), "getUsers_v1",
        "Login", true);
    return DataProviderUtil.sheetMapToDPMap(sheetArray);
  }


}
