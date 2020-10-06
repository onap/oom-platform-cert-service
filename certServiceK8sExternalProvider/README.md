## Cert Service k8s external cert signing provider

### Build project

There are two methods for building the project:
    
 - mvn clean install
 - make

### Installation

Apply k8s files from 'deploy' directory in following order:
 
 - crd.yaml
 - roles.yaml
 - deployment.yaml
 - configuration.yaml


### Usage

To issue a certificate adjust and apply following k8s file:
 
 - certificate_example.yaml
