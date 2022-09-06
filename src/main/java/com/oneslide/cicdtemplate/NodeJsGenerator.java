package com.oneslide.cicdtemplate;

import com.oneslide.cicdtemplate.cicd.Module;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;
import java.util.jar.JarFile;

public class NodeJsGenerator {



    public static void copyFixFile(String projectPath){
        FileUtil util =new FileUtil();
        String[] fixList = new String[]{
                "Dockerfile",
                "Dockerfile-arm64",
                "nginx.conf.template",
                "build.sh"
        };
        Arrays.asList(fixList).forEach(f ->{
            try {
                util.copyPluginResource(String.format("/templates/nodejs/%s",f), Paths.get(projectPath,f));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            JarFile file = new FileUtil().pluginJarFile();
            FileUtil.copyResourcesToDirectory(file, "templates/maven/.helm", Paths.get(projectPath, ".helm").toString());
            file.close();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        try {
            util.copyPluginResource(String.format("/templates/%s","Jenkinsfile"), Paths.get(projectPath,"Jenkinsfile"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void cicdYaml(String projectPath, Map<String,String> info)  {


        Yaml yaml =  FileUtil.getYamlInstance();
        Path cicdPath =Paths.get(projectPath,"cicd.yaml");
        try {
            String content = yaml.dumpAsMap(Module.generateNodejsModule(info.get("name"),"sample",new String[]{info.get("author")}));
            Files.write(cicdPath,content.getBytes(), StandardOpenOption.CREATE);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
