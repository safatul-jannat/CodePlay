package com.example.codeplayfx.compiler;

public class DummyRunner implements CodeRunner{
    @Override
    public void run(String code){
        System.out.println("===Backend got this===");
        System.out.println(code);
        System.out.println("======================");

    }
}
