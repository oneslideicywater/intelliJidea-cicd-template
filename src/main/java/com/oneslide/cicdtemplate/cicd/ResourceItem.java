package com.oneslide.cicdtemplate.cicd;

import java.util.ArrayList;
import java.util.List;

public class ResourceItem {

    public String source;
    public String dist;
    public String name;

    public static ResourceItem nodeJsDist(){
        ResourceItem item =new ResourceItem();
        item.name = "dist";
        item.source = "dist";
        item.dist = "";
        return item;
    }

    public static List<ResourceItem> mavenResource(){

        List<ResourceItem> result = new ArrayList<>();

        ResourceItem item1 =new ResourceItem();
        item1.name = "jar";
        item1.source = "target/*encrypted.jar";
        item1.dist = "";


        ResourceItem item2 =new ResourceItem();
        item2.name = "lic";
        item2.source = "target/*encrypted.lic";
        item2.dist = "";


        ResourceItem item3 =new ResourceItem();
        item3.name = "yml-config";
        item3.source = "target/classes/config/*.yml";
        item3.dist = "config";

        result.add(item1);
        result.add(item2);
        result.add(item3);
        return result;
    }
}
