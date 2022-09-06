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

At Idea top menu, Select `Tools` - > `Genereate Pipeline Manifest`

you will get a bunch of manifest,including

- kubernetes helm charts
- Dockerfile for x86 and arm64
- cicd.yaml(the pipeline v3 manifest)


 Compatibility: please use version 2021.1.3 or above. there's path issue relevant with older version



