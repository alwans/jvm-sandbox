package com.alibaba.jvm.sandbox.demo;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.ProcessControlException;
import com.alibaba.jvm.sandbox.api.ProcessController;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import org.kohsuke.MetaInfServices;

import javax.annotation.Resource;




import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
@MetaInfServices(Module.class)
@Information(id = "broken-clock-tinker",version = "1.0.0", author = "wl")
public class BrokenClockTinkerModule implements Module {

    @Resource
    private ModuleEventWatcher moduleEventWatcher;



    @Command("repairCheckState")
    public void repairCheckState() {

        new EventWatchBuilder(moduleEventWatcher)
                .onClass("com.jvm.test.controller.Clock")
                .onBehavior("checkState")
                .onWatch(new AdviceListener() {

                    /**
                     * 拦截{@code com.taobao.demo.Clock#checkState()}方法，当这个方法抛出异常时将会被
                     * AdviceListener#afterThrowing()所拦截
                     */
                    @Override
                    protected void afterThrowing(Advice advice) throws Throwable {

                        // 在此，你可以通过ProcessController来改变原有方法的执行流程
                        // 这里的代码意义是：改变原方法抛出异常的行为，变更为立即返回；void返回值用null表示
                        ProcessController.returnImmediately(null);
                    }
                });

    }


    @Command("setHttpProxy")
    public void setHttpProxy(){
        new EventWatchBuilder(moduleEventWatcher)
                .onClass("com.jvm.test.controller.HttpUtil")
                .onBehavior("getHttpClient")
                .onWatch(new AdviceListener() {

                    /**
                     * 拦截{@code com.taobao.demo.Clock#checkState()}方法，当这个方法抛出异常时将会被
                     * AdviceListener#afterThrowing()所拦截
                     */
                    @Override
                    protected void afterReturning(Advice advice) throws Throwable {
                        HttpHost proxy = new HttpHost("192.168.131.134", 8888);
                        //超时时间单位为毫秒
                        RequestConfig defaultRequestConfig = RequestConfig.custom()
                                .setConnectTimeout(3000).setSocketTimeout(3000)
                                .setProxy(proxy).build();
                        //构建客户端
                        final CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
                        // 在此，你可以通过ProcessController来改变原有方法的执行流程
                        // 这里的代码意义是：改变原方法抛出异常的行为，变更为立即返回；void返回值用null表示
                        System.out.println("拦截成功-----");
                        ProcessController.returnImmediately(httpClient);
                    }
                });
    }

    @Command("changeNum")
    public void changeNum(){
        new EventWatchBuilder(moduleEventWatcher)
                .onClass("com.jvm.test.controller.TestController")
                .onBehavior("test3")
                .onWatch(new AdviceListener(){
                    @Override
                    protected void afterReturning(Advice advice) throws ProcessControlException {
                        String result = "{\"method\": \"test3\",\"mesage\": \"success\",\"num\":-1}";
                        ProcessController.returnImmediately(result);
                    }
                });
    }

    @Command("adviceTest")
    public void adviceTest(){
        new EventWatchBuilder(moduleEventWatcher)
                .onClass("com.jvm.test.controller.TestController")
                .onBehavior("adviceTest")
                .onWatch(new AdviceListener(){
                    /**
                     * 修改了adviceTest方法的入参
                     * @param advice 通知信息
                     * @throws Throwable
                     */
                    @Override
                    protected void before(Advice advice) throws Throwable {
                       Object[] params =  advice.getParameterArray();
                       int pos = 0;
                       for(Object param : params){
                           if(param instanceof String){
                               advice.changeParameter(pos, "changParam-"+String.valueOf(pos+1));
                               pos++;
                           }
                       }

                    }
                });
    }

    @Command("callTest")
    public void callTest(){
        new EventWatchBuilder(moduleEventWatcher)
                .onClass("com.jvm.test.controller.TestController")
                .onBehavior("callB")
                .onWatch(new AdviceListener(){
                    @Override
                    protected void beforeCall(Advice advice,
                                              int callLineNum,
                                              String callJavaClassName, String callJavaMethodName, String callJavaMethodDesc) {
                        System.out.println("callLineNum-------->"+String.valueOf(callLineNum));
                        try {
                            ProcessController.returnImmediately("change call-B method: returnImmediately");
                        } catch (ProcessControlException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}