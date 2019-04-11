package com.wpc.cloud.zuul.fallback;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

/**
 * Edgware.RELEASE以前的版本中，zuul网关中有一个ZuulFallbackProvider接口
 * 其中fallbackResponse()方法允许程序员在回退处理中重建输出对象，通常是输出“xxx服务不可用，请稍候重试”之类的提示，但是无法捕获到更详细的出错信息，排错很不方便。
 * 估计spring-cloud团队意识到了这个问题，在Edgware.RELEASE中将该接口标记为过时@Deprecated，同时在它下面派生出了一个新接口。
 * 把异常信息也当作参数传进来了，这样就友好多了，在处理回退时可以输出更详细的信息
 */
@Component
public class MyFallbackProvider implements FallbackProvider {

  /**
   * 返回值表示需要针对此微服务做回退处理(该名称一定要是注册进入 eureka微服务中的那个 serviceId名称)；
   * @return
   */
  @Override
  public String getRoute() {
    // 表明是为了哪个微服务提供回退，*或者null表示为所有微服务提供回退
    return "consumer";
  }

  @Override
  public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
    return new ClientHttpResponse() {
      /**
       * 网关向api服务请求是失败了，但是消费者客户端向网关发起的请求是OK的，
       * 不应该把api的404,500等问题抛给客户端
       * 网关和api服务集群对于客户端来说是黑盒子
       */
      @Override
      public HttpStatus getStatusCode() throws IOException {
        return HttpStatus.OK;
      }

      @Override
      public int getRawStatusCode() throws IOException {
        return HttpStatus.OK.value();
      }

      @Override
      public String getStatusText() throws IOException {
        return HttpStatus.OK.getReasonPhrase();
      }

      @Override
      public void close() {

      }

      @Override
      public InputStream getBody() throws IOException {
        if (cause != null && cause.getCause() != null) {
          String reason = cause.getCause().getMessage();
          //输出详细的回退原因
          System.out.println(reason);
        }
        return new ByteArrayInputStream(("fallback" + ":" + MyFallbackProvider.this.getRoute()).getBytes());
      }

      @Override
      public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
      }
    };
  }
}