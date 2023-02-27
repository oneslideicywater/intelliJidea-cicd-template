## cicd-template

cicd-template is an IntelliJ idea Plugin which generate pipeline v3
manifest to common project structure,like 

- maven project(single module and multi-module)
- npm project


## Prerequisite

manifest is applied to proprietary jenkins pipeline with specific config.
so this is not general-purpose cicd pipeline manifest generator.

## Install

install via [plugin marketplace](https://plugins.jetbrains.com/plugin/19862-cicd-template) or download release page zip.


## Usage


### 生成流水线配置文件

At Idea top menu, Select `Tools` - > `Genereate Pipeline Manifest`

you will get a bunch of manifest,including

- kubernetes helm charts
- Dockerfile for x86 and arm64
- cicd.yaml(the pipeline v3 manifest)


 Compatibility: please use version 2021.1.3 or above. there's path issue relevant with older version


### 触发远程构建

触发远程构建需要在`cicd.yaml`中添加：

```yaml
easybuild: 
  jenkins:
    url: http://xxx.xxx.xxx.xxx:8080
  job: 
    name: "myapp"
  auth:
    user: "admin"
    token: "xxx"  
```

### 腾讯企业微信通知
- cicd.yaml

```yaml
env:
  BOTHOOK: "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?kexxx"
```

在顶级父模块添加BOTHOOK环境变量，将值更新为机器人的webhook地址。当构建完成时，即可获取机器人构建完成通知。



