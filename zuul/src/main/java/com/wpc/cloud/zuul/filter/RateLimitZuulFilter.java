package com.wpc.cloud.zuul.filter;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVICE_ID_KEY;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * 在Zuul上实现限流是个不错的选择，只需要编写一个过滤器就可以了，关键在于如何实现限流的算法。
 * 常见的限流算法有漏桶算法以及令牌桶算法。
 *參考链接：http://www.itmuch.com/spring-cloud-sum/spring-cloud-ratelimit/
 */
@Component
public class RateLimitZuulFilter extends ZuulFilter {

    //RateLimiter是guava提供的基于令牌桶算法的实现类，可以非常简单的完成限流特技，并且根据系统的实际情况来调整生成token的速率
    //1代表一秒最多多少个
    //private final RateLimiter rateLimiter = RateLimiter.create(1.0);

    private Map<String, RateLimiter> map = Maps.newConcurrentMap();

    //@Autowired
    //private SystemPublicMetrics systemPublicMetrics;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        // 这边的order一定要大于org.springframework.cloud.netflix.zuul.filters.pre.PreDecorationFilter的order
        // 也就是要大于5
        // 否则，RequestContext.getCurrentContext()里拿不到serviceId等数据。
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * Spring Boot Actuator提供的Metrics 能力进行实现基于内存压力的限流——当可用内存低于某个阈值就开启限流，否则不开启限流
     * @return
     */
    @Override
    public boolean shouldFilter() {
        // 这里可以考虑弄个限流开启的开关，开启限流返回true，关闭限流返回false，你懂的。

        //=========spring boot 2.x里metrics默认换成了micrometer，原来的MetricWriter之类的全干掉了===============
        /*Collection<Metric<?>> metrics = systemPublicMetrics.metrics();
        Optional<Metric<?>> freeMemoryMetric = metrics.stream()
            .filter(t -> "mem.free".equals(t.getName())).findFirst();
        // 如果不存在这个指标，稳妥起见，返回true，开启限流
        if (!freeMemoryMetric.isPresent()) {
            return true;
        }
        long freeMemory = freeMemoryMetric.get().getValue().longValue();
        // 如果可用内存小于1000000KB，开启流控
        return freeMemory < 1000000L;*/
        return true;
    }

    @Override
    public Object run() {
        try {
            RequestContext currentContext = RequestContext.getCurrentContext();
            HttpServletResponse response = currentContext.getResponse();
            String key = null;
            // 对于service格式的路由，走RibbonRoutingFilter
            String serviceId = (String) currentContext.get(SERVICE_ID_KEY);
            if (serviceId != null) {
                key = serviceId;
                //putIfAbsent在放入数据时，如果存在重复的key，那么putIfAbsent不会放入值。
                map.putIfAbsent(serviceId, RateLimiter.create(1000.0));
            }
            // 如果压根不走RibbonRoutingFilter，则认为是URL格式的路由
            else {
                // 对于URL格式的路由，走SimpleHostRoutingFilter
                URL routeHost = currentContext.getRouteHost();
                if (routeHost != null) {
                    String url = routeHost.toString();
                    key = url;
                    map.putIfAbsent(url, RateLimiter.create(2000.0));
                }
            }
            RateLimiter rateLimiter = map.get(key);
            if (!rateLimiter.tryAcquire()) {
                HttpStatus httpStatus = HttpStatus.TOO_MANY_REQUESTS;
                response.setContentType(MediaType.TEXT_PLAIN_VALUE);
                response.setStatus(httpStatus.value());
                response.getWriter().append(httpStatus.getReasonPhrase());
                currentContext.setSendZuulResponse(false);
//                throw new ZuulException(
//                        httpStatus.getReasonPhrase(),
//                        httpStatus.value(),
//                        httpStatus.getReasonPhrase()
//                );
            }
        } catch (Exception e) {
            ReflectionUtils.rethrowRuntimeException(e);
        }
        return null;
    }
}