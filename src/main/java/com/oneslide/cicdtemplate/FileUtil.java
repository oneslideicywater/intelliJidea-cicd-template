package com.oneslide.cicdtemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtil {

    public InputStream readFromJARFile(String filename)
            throws IOException
    {
        InputStream is = getClass().getResourceAsStream(filename);
        return  is;
    }
    public void copyPluginResource(String sourcePath, Path dest) throws IOException {
        // resource file in jar
        InputStream inputStream =readFromJARFile(sourcePath);
        Files.copy(inputStream,dest,StandardCopyOption.REPLACE_EXISTING);

    }

    /*
    * copy directory in jar
    * */
    public void copyPluginDirectoryResource(String sourcePath,Path dest) throws IOException{

    }

    /**
     * Copies a directory from a jar file to an external directory.
     */
    public static void copyResourcesToDirectory(JarFile fromJar, String jarDir, String destDir)
            throws IOException {
        for (Enumeration<JarEntry> entries = fromJar.entries(); entries.hasMoreElements(); ) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().startsWith(jarDir + "/") && !entry.isDirectory()) {
                File dest = new File(destDir + "/" + entry.getName().substring(jarDir.length() + 1));
                File parent = dest.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }

                FileOutputStream out = new FileOutputStream(dest);
                InputStream in = fromJar.getInputStream(entry);

                try {
                    byte[] buffer = new byte[8 * 1024];

                    int s = 0;
                    while ((s = in.read(buffer)) > 0) {
                        out.write(buffer, 0, s);
                    }
                } catch (IOException e) {
                    throw new IOException("Could not copy asset from jar file", e);
                } finally {
                    try {
                        in.close();
                    } catch (IOException ignored) {
                    }
                    try {
                        out.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }


    public JarFile pluginJarFile() throws IOException {
        String url = getClass().getResource("/templates").getPath();
        File resource = new File(url);
        String s= resource.getParent().replaceAll("(!|file:\\\\)","");
        JarFile file = new JarFile(s);
//            FileUtil.copyResourcesToDirectory(file,"templates/maven/deploy", Paths.get(currentProject.getBasePath(),"deploy").toString());
//            FileUtil.copyResourcesToDirectory(file,"templates/maven/.helm", Paths.get(currentProject.getBasePath(),".helm").toString());
       return file;
    }
    public static Yaml getYamlInstance(){
        Representer representer = new Representer() {
            @Override
            protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
                // if value of property is null, ignore it.
                if (propertyValue == null) {
                    return null;
                }
                else {
                    return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
                }
            }
        };
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);


        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(representer,options);
        return yaml;
    }

    public static String judgeType(String projectPath){
        Path pom = Paths.get(projectPath, "pom.xml");
        Path nodejs = Paths.get(projectPath, "package.json");
        if(Files.exists(pom) && !Files.exists(nodejs) ){
            return "maven";
        }
        if(!Files.exists(pom) && Files.exists(nodejs) ){
            return "nodejs";
        }
        return "";
    }

    public static class MavenInfo{
        public String name;
        public String[] modules;
    }

    public static MavenInfo readInfo(String projectPath) throws DocumentException {
        SAXReader reader =new SAXReader();
        File inputFile = new File(Paths.get(projectPath,"pom.xml").toString());
        Document document = reader.read(inputFile);
        Element root = document.getRootElement();
        // get artifact Id
        String artifactId =root.element("artifactId").getText();

        // get modules list

        List<String> mods=new ArrayList<>();

        if(root.elements("modules").size() == 0){
            System.out.println("single module");
        }else {
            root.element("modules").elements().forEach(node -> {
                mods.add(node.getText());
            });
        }
        MavenInfo info =new MavenInfo();
        info.name=artifactId;
        info.modules = mods.toArray(new String[0]);
        return info;
    }

    public static Map<String,String> npmProjectInfo(String projectPath) throws IOException {

        String content=new String(Files.readAllBytes(Paths.get(projectPath,"package.json")));
        Gson gson =new Gson();

        JsonObject map = gson.fromJson(content, JsonObject.class);


        Map<String,String> info =new HashMap<>();
        info.put("name",map.get("name").getAsString());
        info.put("author",map.get("author").getAsString());
        return info;
    }

}
