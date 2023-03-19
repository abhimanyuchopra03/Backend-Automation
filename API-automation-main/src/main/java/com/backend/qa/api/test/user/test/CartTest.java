package com.backend.qa.api.test.user.test;

import com.backend.qa.api.dto.RequestDTO;
import com.backend.qa.api.test.helper.RegistrationHelper;
import com.backend.qa.api.test.ResponseDTO.CartResponseDto;
import com.backend.qa.api.test.ResponseDTO.ProductsRepsonseDto;
import com.backend.qa.api.test.requestDTO.AddRequestDto;
import com.backend.qa.api.test.requestDTO.CheckoutRequestDto;
import com.backend.qa.apihelper.ApiHelper;
import com.backend.qa.listener.TestListener;
import com.backend.qa.util.JavaUtil;
import com.backend.qa.util.LoggerUtil;
import com.backend.qa.util.LoggerUtil.LogLevel;
import com.backend.qa.util.PropertiesUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.backend.qa.apibase.APITestBase;
import com.backend.qa.util.DataProviderUtil;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.backend.qa.util.DataProviderUtil.mapKeys;

@Listeners(TestListener.class)
public class CartTest extends APITestBase {

  static String baseUrl = null;
  static HashMap<String, Integer> productQuantity;

  static HashMap<Integer, String> createdUserMap;
  static String username;
  static String password;
  ApiHelper apihelper = null;
  HttpResponse httpResponse = null;
  RequestDTO requestDTO = null;
  String apiResponse = null;
  int responseCode = 0;
  int failureCount = 0;
  ObjectMapper objectMapper = new ObjectMapper();
  int quantityAdded = 1;
  RegistrationHelper registrationHelper;
  static HashMap<String, String> registredUser;

  @BeforeClass
  public void initializeClass() throws Exception {
    LoggerUtil
        .setlog(LogLevel.ONLYLOGS, "Initializing class variables");
    apihelper = new ApiHelper();
    username = java.util.UUID.randomUUID().toString().substring(0, 5);
    password = PropertiesUtil.getConstantProperty("password");
    baseUrl = PropertiesUtil.getEnvConfigProperty("baseUserAPI");
    registredUser = new HashMap<>();
    productQuantity = new HashMap<>();
    registrationHelper = new RegistrationHelper();
    registrationHelper.registerationHelper(username,password);
    registrationHelper.LoginHelper(username,password);


  }

  @BeforeMethod
  public void beforeMethod()
      throws SQLException, URISyntaxException, IOException {
    failureCount = 0;
  }





  /*
      This test will first get the list of all the products
   */

  @Test(dataProvider = "getProducts", priority = 3, enabled = true)
  public void testPutAPI(String testName, HashMap<String, Object> requestDtoMap)
      throws IOException {
    ObjectMapper om = new ObjectMapper();
    requestDTO = (RequestDTO) requestDtoMap.get(mapKeys.reqDTO.toString());
    String origPathBody = requestDTO.getPathParamJson();
    LinkedHashMap<String, String> replacePathBody = new LinkedHashMap<String, String>();
    replacePathBody.put("username", username);
    String pathBody = JavaUtil.replacePreRequisite(replacePathBody, origPathBody);
    if (pathBody != null) {
      requestDTO.setPathParamJson(pathBody);
    }
    httpClient = HttpClients.createDefault();
    httpResponse = apihelper.createRequest(requestDTO, baseUrl, httpClient, httpContext, false);
    apiResponse = apihelper.getResponse(requestDTO, httpResponse);
    responseCode = httpResponse.getStatusLine().getStatusCode();
    Assert.assertEquals(responseCode, 200);
    ProductsRepsonseDto[] productsRepsonseDto = om.readValue(apiResponse, ProductsRepsonseDto[].class);
    for (ProductsRepsonseDto ps : productsRepsonseDto) {
      if (ps.getProduct_qty() >= 2) {
        productQuantity.put(ps.getProduct_name(), ps.getProduct_qty());
      }
      Assert.assertNotNull(ps.getProduct_name());
      Assert.assertNotNull(ps.getProduct_descr());
    }
  }

   /*
      This test will add all the products to cart
   */


  @Test(dataProvider = "addToCart", priority = 4, enabled = true)
  public void addToCart(String testName, HashMap<String, Object> requestDtoMap,
      String productObject)
      throws IOException {
    requestDTO = (RequestDTO) requestDtoMap.get(mapKeys.reqDTO.toString());
    String origPathBody = requestDTO.getPathParamJson();
    LinkedHashMap<String, String> replacePathBody = new LinkedHashMap<String, String>();
    replacePathBody.put("username", username);
    replacePathBody.put("product", productObject);
    String pathBody = JavaUtil.replacePreRequisite(replacePathBody, origPathBody);
    if (pathBody != null) {
      requestDTO.setPathParamJson(pathBody);
    }
    AddRequestDto addRequestDto = AddRequestDto.builder().quantity(quantityAdded).build();
    String requestString = objectMapper.writeValueAsString(addRequestDto);
    requestDTO.setRequestBody(requestString);
    httpClient = HttpClients.createDefault();
    httpResponse = apihelper.createRequest(requestDTO, baseUrl, httpClient, httpContext, false);
    apiResponse = apihelper.getResponse(requestDTO, httpResponse);
    responseCode = httpResponse.getStatusLine().getStatusCode();
    Assert.assertEquals(responseCode, 200);
  }

   /*
      This test will validate all the products added to cart are present are not
   */

  @Test(dataProvider = "getCartInfo", priority = 5, enabled = true)
  public void getCurrentCartInfo(String testName, HashMap<String, Object> requestDtoMap)
      throws IOException {
    requestDTO = (RequestDTO) requestDtoMap.get(mapKeys.reqDTO.toString());
    String origPathBody = requestDTO.getPathParamJson();
    LinkedHashMap<String, String> replacePathBody = new LinkedHashMap<String, String>();
    replacePathBody.put("username", username);
    String pathBody = JavaUtil.replacePreRequisite(replacePathBody, origPathBody);
    if (pathBody != null) {
      requestDTO.setPathParamJson(pathBody);
    }
    httpClient = HttpClients.createDefault();
    httpResponse = apihelper.createRequest(requestDTO, baseUrl, httpClient, httpContext, false);
    ObjectMapper om = new ObjectMapper();
    apiResponse = apihelper.getResponse(requestDTO, httpResponse);
    List<CartResponseDto> myObjects = Arrays
        .asList(om.readValue(apiResponse, CartResponseDto[].class));
    Assert.assertEquals(productQuantity.size(), myObjects.size());
    Assert.assertEquals(productQuantity.size(),
        myObjects.stream().filter(e -> e.getProduct_qty() == quantityAdded).collect(Collectors.toList())
            .size());
  }

  /*
      This test will call the checkout API
   */


  @Test(dataProvider = "checkout", priority = 6, enabled = true)
  public void checkout(String testName, HashMap<String, Object> requestDtoMap)
      throws IOException {
    LinkedHashMap<String, String> replacePathBody = new LinkedHashMap<String, String>();
    requestDTO = (RequestDTO) requestDtoMap.get(mapKeys.reqDTO.toString());
    String origPathBody = requestDTO.getPathParamJson();
    replacePathBody.put("username", username);
    String pathBody = JavaUtil.replacePreRequisite(replacePathBody, origPathBody);
    if (pathBody != null) {
      requestDTO.setPathParamJson(pathBody);
    }
    CheckoutRequestDto checkoutRequestDto = CheckoutRequestDto.builder()
        .username(username).build();
    String requestString = objectMapper.writeValueAsString(checkoutRequestDto);
    requestDTO.setRequestBody(requestString);
    httpClient = HttpClients.createDefault();
    httpResponse = apihelper.createRequest(requestDTO, baseUrl, httpClient, httpContext, false);
    apiResponse = apihelper.getResponse(requestDTO, httpResponse);
    responseCode = httpResponse.getStatusLine().getStatusCode();
    Assert.assertEquals(responseCode, 200);

  }

  /*
      Here I am defining the data provider for each test , which will read the data from excel and create object .
      These object will passed to test.

   */




  @DataProvider(name = "addToCart", parallel = false)
  public Object[][] addTocartDP() throws Exception {
    Object[][] sheetArray = DataProviderUtil.provideDataMap(
        PropertiesUtil.getConstantProperty("TestData_API_User"), "getUsers_v1",
        "addproduct", true);


    Object[][] newObject = new Object[productQuantity.size()][3];
    int i = 0;
    for (Map.Entry<String, Integer> hm : productQuantity.entrySet()) {
      Object[][] object = DataProviderUtil.sheetMapToDPMap(sheetArray);
      newObject[i][0] = object[0][0]+": product - "+hm.getKey();
      newObject[i][1] = object[0][1];
      newObject[i][2] = hm.getKey();
      i++;
    }
    return newObject;
  }

  @DataProvider(name = "getCartInfo", parallel = false)
  public Object[][] getCartInfoDP() throws Exception {
    Object[][] sheetArray = DataProviderUtil.provideDataMap(
        PropertiesUtil.getConstantProperty("TestData_API_User"), "getUsers_v1",
        "getCartInfo", true);
    return DataProviderUtil.sheetMapToDPMap(sheetArray);
  }

  @DataProvider(name = "checkout", parallel = false)
  public Object[][] checkoutDP() throws Exception {
    Object[][] sheetArray = DataProviderUtil.provideDataMap(
        PropertiesUtil.getConstantProperty("TestData_API_User"), "getUsers_v1",
        "Checkout", true);
    return DataProviderUtil.sheetMapToDPMap(sheetArray);
  }

  @DataProvider(name = "getProducts", parallel = false)
  public Object[][] getProductsDP() throws Exception {
    Object[][] sheetArray = DataProviderUtil.provideDataMap(
        PropertiesUtil.getConstantProperty("TestData_API_User"), "getUsers_v1",
        "getProducts", true);
    return DataProviderUtil.sheetMapToDPMap(sheetArray);
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
