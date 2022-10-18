package com.alibaba.jvm.sandbox.qatest.core;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wl on 2021/2/23.
 */
public class DemoTest {
    static int[] ba = {0,1,2,3,4,5,6,7,8,9,10};
    static int count = 0;
    public static void main(String[] args) {
//        Map<Integer,Integer> map = new HashMap<Integer, Integer>();
//        for(int i=0;i<15;i++){
//            System.out.println(i);
//            map.put(i,i);
//        }
//        File file = new File("G:\\jvm\\jvm-sandbox\\sandbox-agent\\target");
//        Hashtable table = new Hashtable();
//        table.put(1,1);
//        LinkedHashMap map = new LinkedHashMap();
//        map.put(1,1);
//        test(0,0);
//        System.out.println(count);
//        String s1 = new String("aa");
//        String s2 = "aa";
//        String s3 = s1.intern();
//        System.out.println(s1==s2);
//        System.out.println(s2==s3);
//        System.out.println(s1==s3);
//        String s = new String("1");
//        String s2 = "1";
//        s.intern();
//        System.out.println(s == s2);
//
//        String s3 = new String("11") ;
//        s3.intern();
//
//        String s4 = "11";
//
//        System.out.println(s3 == s4);
//        StringBuilder sb = new StringBuilder();
        System.out.println("中文你好");

    }

    public static int test(int i,int sum){
        int pos =1;
        int q_count=10;
        for(;i<q_count ;i++){
            int score;
            while(pos<12){
                score = ba[pos-1];
                int yuqi_sum = sum + score+ (q_count-i-1)*10;
                if(i==9 && (sum+score)==90){
                    count++;
                    return 1;
                }else if(yuqi_sum>=90){
                    test(i+1, sum+score);
                    pos++;
                }else{
                    pos++;
                }
            }
        }
        return 1;
    }
}
