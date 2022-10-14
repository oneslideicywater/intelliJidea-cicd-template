package com.oneslide.cicdtemplate;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.oneslide.cicdtemplate.cicd.EasyBuild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class EasyBuildAction extends AnAction {

    public EasyBuildAction(){
        super();
    }

    public EasyBuildAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // get project path
        Project currentProject = event.getProject();

        try {
            // read current branch
            String data= Files.readString(Paths.get(currentProject.getBasePath(),".git","HEAD"));
            String branch=data.replace("ref: refs/heads/","");
            EasyBuild build= new EasyBuild();
            // read cicd config
            String content=Files.readString(Paths.get(currentProject.getBasePath(),"cicd.yaml"));
            Yaml yaml = FileUtil.getYamlInstance();
            LinkedHashMap m = yaml.load(content);
            LinkedHashMap buildObj = (LinkedHashMap) m.get("easybuild");

            LinkedHashMap jenkins = (LinkedHashMap) buildObj.get("jenkins");
            build.jenkins.url=(String) jenkins.get("url");

            LinkedHashMap job = (LinkedHashMap)buildObj.get("job");
            build.job.name= (String) job.get("name");
            build.job.branch= branch.trim();

            LinkedHashMap auth = (LinkedHashMap)buildObj.get("auth");
            build.auth.user= (String) auth.get("user");
            build.auth.token=(String)auth.get("token");

            // request for jenkins
            // request url
            boolean isSuccess = TriggerBuild(build);
            if (isSuccess){
                MyNotifier.notifyInfo(currentProject,String.format("%s/%s Success Trigger :)",build.job.name,build.job.branch));
            }else{
                MyNotifier.notifyError(currentProject,String.format("%s/%s Trigger Failed :(",build.job.name,build.job.branch));
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    /**
     * Determines whether this menu item is available for the current context.
     * Requires a project to be open.
     *
     * @param e Event received when the associated group-id menu is chosen.
     */
    @Override
    public void update(AnActionEvent e) {
        // Set the availability based on whether a project is open
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }


    public boolean TriggerBuild(EasyBuild build){

        // set up common basic auth
        HttpClient client = HttpClient.newBuilder()
                .build();
        String reqUrl=String.format("%s/job/%s/job/%s/build?delay=0sec",build.jenkins.url,build.job.name,build.job.branch);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(reqUrl))
                .header("Authorization",getBasicAuthenticationHeader(build.auth.user,build.auth.token))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        AtomicBoolean triggerResult = new AtomicBoolean(false);
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::statusCode)
                .thenAccept((status)->{
                    if(status == 201){
                        triggerResult.set(true);
                    }
                }).join();
        return triggerResult.get();
    }
    private static final String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

}

