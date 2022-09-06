package com.oneslide.cicdtemplate;

import com.oneslide.cicdtemplate.cicd.Module;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.jar.JarFile;

public class MavenGenerator {


    public static void cicdYaml(String projectPath, FileUtil.MavenInfo info)  {

        Yaml yaml = FileUtil.getYamlInstance();
        Path cicdPath =Paths.get(projectPath,"cicd.yaml");
        try {
            String content = yaml.dumpAsMap(Module.generateMavenModule(info.name, "sample",new String[]{"zhangsan"},info.modules));
            Files.write(cicdPath,content.getBytes(), StandardOpenOption.CREATE);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /*
    * single module project and multi-module project is diff
    * */
    public static void copyFixFile(String projectPath,String[] modules){

        try {
            new FileUtil().copyPluginResource(String.format("/templates/%s","Jenkinsfile"),
                            Paths.get(projectPath,"Jenkinsfile"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (modules.length == 0){
            copyModuleFiles(projectPath,".");
        }
        else{
            Arrays.asList(modules).forEach(m->{
                copyModuleFiles(projectPath,m);
            });
        }
    }

    /*
    * @param projectPath project path
    * @param modPath     module path
    * */
    public static void copyModuleFiles(String projectPath, String modPath){

        FileUtil util =new FileUtil();
        String[] fixList = new String[]{
                "Dockerfile",
                "Dockerfile-arm64"
        };
        Arrays.asList(fixList).forEach(f ->{
            try {
                util.copyPluginResource(String.format("/templates/maven/%s",f), Paths.get(projectPath,modPath,f));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        String[] fixDirectoryList = new String[]{
                "deploy",
                ".helm"
        };
        Arrays.asList(fixDirectoryList).forEach(f ->{
            try {
                JarFile file = new FileUtil().pluginJarFile();
                FileUtil.copyResourcesToDirectory(file,"templates/maven/"+f, Paths.get(projectPath,modPath,f).toString());
                file.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
}
