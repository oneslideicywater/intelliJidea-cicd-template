package com.oneslide.cicdtemplate.cicd;

public class EasyBuild {


    public EasyBuild() {
        this.jenkins= new Jenkins();
        this.auth=new AuthInfo();
        this.job=new JobInfo();
    }

    public Jenkins jenkins;
    public AuthInfo auth;
    public JobInfo job;

    public static class Jenkins {
        public String url;
    }

    public static class AuthInfo{
        public String user;
        public String token;
    }

    public static class JobInfo {
        public String name;
        public String branch;
    }
}
