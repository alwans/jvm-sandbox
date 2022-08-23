package com.alibaba.jvm.sandbox.module.mgr;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.resource.ConfigInfo;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@MetaInfServices(Module.class)
@Information(id = "sandbox-control", version = "0.0.3", author = "luanjia@taobao.com")
public class ControlModule implements Module {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ConfigInfo configInfo;

    // 卸载jvm-sandbox
    private void uninstall() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Class<?> classOfAgentLauncher = getClass().getClassLoader()
                .loadClass("com.alibaba.jvm.sandbox.agent.AgentLauncher");

        MethodUtils.invokeStaticMethod(
                classOfAgentLauncher,
                "uninstall",
                configInfo.getNamespace()
        );
    }

    @Command("heartbeat")
    public void heartbeat(final PrintWriter writer) {
        writer.println("{\"msg\":\"I'm fine\"}");
        writer.flush();
    }

    // @Http("/shutdown")
    @Command("shutdown")
    public void shutdown(final PrintWriter writer) {

        logger.info("prepare to shutdown jvm-sandbox[{}].", configInfo.getNamespace());

        // 关闭HTTP服务器
        final Thread shutdownJvmSandboxHook = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    uninstall();
                } catch (Throwable cause) {
                    logger.warn("shutdown jvm-sandbox[{}] failed.", configInfo.getNamespace(), cause);
                }
            }
        }, String.format("shutdown-jvm-sandbox-%s-hook", configInfo.getNamespace()));
        shutdownJvmSandboxHook.setDaemon(true);

        // 在卸载自己之前，先向这个世界发出最后的呐喊吧！
//        writer.println(String.format("jvm-sandbox[%s] shutdown finished.", configInfo.getNamespace()));
        // 改成json返回
        Map<String, Object> result = new HashMap<String, Object>(3);
        result.put("code", 200);
        result.put("data", null);
        result.put("msg", String.format("jvm-sandbox[%s] shutdown finished.", configInfo.getNamespace()));
        writer.println(result);
        writer.flush();
        writer.close();

        shutdownJvmSandboxHook.start();

    }

}
