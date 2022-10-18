package com.alibaba.jvm.sandbox.demo;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.ModuleLifecycleAdapter;
import com.alibaba.jvm.sandbox.api.resource.LoadedClassDataSource;

import org.kohsuke.MetaInfServices;

import javax.annotation.Resource;
import java.util.Iterator;

import static com.alibaba.jvm.sandbox.api.util.GaStringUtils.matching;

@MetaInfServices(Module.class)
@Information(id="wl-debug",version = "0.0.1",author = "wl")
public class Debug extends ModuleLifecycleAdapter implements Module {
    @Resource
    private LoadedClassDataSource dataSource;


    @Override
    public void loadCompleted() {
        System.out.println(">>>> 开始查找相关类 <<<<");
        Iterator<Class<?>> iterator = dataSource.iteratorForLoadedClasses();
        while (iterator.hasNext()){
            Class clz = iterator.next();
            if(matching(clz.getName(), "feign.Client")){
                System.out.println(clz.getName());
            }
            if(clz.getName().contains("feign.Client")){
                System.out.println(clz.getName());
            }

        }
        System.out.println(">>>> 查找完毕 <<<<");
    }
}
