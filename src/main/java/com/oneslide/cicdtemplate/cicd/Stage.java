package com.oneslide.cicdtemplate.cicd;

public class Stage {

    public String name;
    public String[] shell;

    public Stage(String name, String[] shell) {
        this.name = name;
        this.shell = shell;
    }
}
