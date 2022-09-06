package com.oneslide.cicdtemplate.cicd;

import java.util.*;

public class Module {

    public String name;
    public String namespace;
    public String[] maintainers;

    public String path;
    public Map<String,String> env;

    public List<Stage> stages;

    public List<ResourceItem> resources;

    public List<Module> modules;


    public static Module generateNodejsModule(String name,String ns,String[] ms){
        Module module =new Module();
        module.name = name;
        module.namespace=ns;
        module.maintainers=ms;
        module.path= ".";
        module.env = new HashMap<>();

        module.env.put("JOB_BASE_PATH", String.format("/jenkins/agent/workspace/%s_%s",ns,name));
        module.env.put("NODE_IMAGE", "registry.geoway.com/nodejs/node:14.20.0-buster-x86");
        // resource copy
        module.resources=new ArrayList<>();
        module.resources.add(ResourceItem.nodeJsDist());

        // stages
        module.stages =new ArrayList<>();
        module.stages.addAll(Arrays.asList(
                new Stage("build",new String[]{
                        "docker run --network host --rm -t -v ${JOB_BASE_PATH}_${BRANCH_NAME}:/opt/workdir ${NODE_IMAGE} /bin/bash /opt/workdir/build.sh"
                }),
                new Stage("docker",new String[]{
                        "docker build -t ${CURRENT_DOCKER_IMAGE_TAG} .",
                        "docker push ${CURRENT_DOCKER_IMAGE_TAG}",

                }),
                new Stage("docker-arm64",new String[]{
                        "docker buildx build -t ${CURRENT_DOCKER_IMAGE_TAG}-arm64 -f Dockerfile-arm64 --platform=linux/arm64 . --load",
                        "docker push ${CURRENT_DOCKER_IMAGE_TAG}-arm64"
                }),
                new Stage("helm",new String[]{
                        "(test ${GIT_BRANCH} = 'master' && kubectl config use-context prod) ||true",
                        "(test ${GIT_BRANCH} = 'develop' && kubectl config use-context dev) ||true",
                        "cd .helm; rm -f *.tgz; helm package .",
                        "cd .helm; curl --data-binary \"@${CHART_NAME}-${CHART_VERSION}.tgz\" ${HELM_REGISTRY}/api/charts",
                        "helm repo update geoway;helm upgrade ${APP_NAME} geoway/${CHART_NAME} --version ${CHART_VERSION} --install -n ${NAMESPACE}",
                        "kubectl rollout restart deployment ${CHART_NAME} -n ${NAMESPACE}"
                }),
                new Stage("helm-arm64",new String[]{
                        "echo [INFO] deploy to arm64 kubernetes",
                        "(test ${GIT_BRANCH} = 'develop' && kubectl config use-context arm64) ||true",
                        "(test ${GIT_BRANCH} = 'develop' && helm repo update geoway && helm upgrade --set imageName=${CURRENT_DOCKER_IMAGE_TAG}-arm64 ${APP_NAME} geoway/${CHART_NAME} --version ${CHART_VERSION} --install -n ${NAMESPACE}) || true",
                        "(test ${GIT_BRANCH} = 'develop' && kubectl rollout restart deployment ${CHART_NAME} -n ${NAMESPACE} ) || true"
                })
        ));

        return module;
    }


    public static Module generateMavenModule(String name,String ns,String[] ms,String[] modules){
        Module module =new Module();
        module.name = name;
        module.namespace=ns;
        module.maintainers=ms;
        module.path= ".";
        module.env = new HashMap<>();
        module.env.put("ns",ns);
        module.modules = new ArrayList<>();


        if (modules.length == 0 ){
            module.resources= ResourceItem.mavenResource();
            // stages
            module.stages =new ArrayList<>();
            addStageHelper(module);
            module.stages.add(0, new Stage("build",new String[]{
                    "mvn clean install"}
                    ));
        } else{
            Arrays.asList(modules).forEach(m->{
                Module mod = new Module();
                mod.name= m;
                mod.path = m;
                mod.stages=new ArrayList<>();
                mod.resources = ResourceItem.mavenResource();
                addStageHelper(mod);
                module.modules.add(mod);
            });
        }


        return module;
    }

    private static void addStageHelper(Module module){
        module.stages.addAll(Arrays.asList(
                new Stage("docker",new String[]{
                        "docker build -t ${CURRENT_DOCKER_IMAGE_TAG} .",
                        "docker push ${CURRENT_DOCKER_IMAGE_TAG}",

                }),
                new Stage("docker-arm64",new String[]{
                        "docker buildx build -t ${CURRENT_DOCKER_IMAGE_TAG}-arm64 -f Dockerfile-arm64 --platform=linux/arm64 . --load",
                        "docker push ${CURRENT_DOCKER_IMAGE_TAG}-arm64"
                }),
                new Stage("helm",new String[]{
                        "(test ${GIT_BRANCH} = 'master' && kubectl config use-context prod) ||true",
                        "(test ${GIT_BRANCH} = 'develop' && kubectl config use-context dev) ||true",
                        "cd .helm; rm -f *.tgz; helm package .",
                        "cd .helm; curl --data-binary \"@${CHART_NAME}-${CHART_VERSION}.tgz\" ${HELM_REGISTRY}/api/charts",
                        "helm repo update geoway;helm upgrade ${APP_NAME} geoway/${CHART_NAME} --version ${CHART_VERSION} --install -n ${NAMESPACE}",
                        "kubectl rollout restart deployment ${CHART_NAME} -n ${NAMESPACE}"
                }),
                new Stage("helm-arm64",new String[]{
                        "echo [INFO] deploy to arm64 kubernetes",
                        "(test ${GIT_BRANCH} = 'develop' && kubectl config use-context arm64) ||true",
                        "(test ${GIT_BRANCH} = 'develop' && helm repo update geoway && helm upgrade --set imageName=${CURRENT_DOCKER_IMAGE_TAG}-arm64 ${APP_NAME} geoway/${CHART_NAME} --version ${CHART_VERSION} --install -n ${NAMESPACE}) || true",
                        "(test ${GIT_BRANCH} = 'develop' && kubectl rollout restart deployment ${CHART_NAME} -n ${NAMESPACE} ) || true"
                })
        ));
    }
}
