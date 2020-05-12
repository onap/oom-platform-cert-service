.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2020 NOKIA
.. _offeredapis:

Offered APIs
=============

AAF Cert Service Api
--------------------

.. code-block:: yaml

    openapi: 3.0.1
    info:
      title: CertService Documentation
      description: Certification service API documentation
      version: 1.0.0
    servers:
      - url: http://localhost:8080
        description: Generated server url
    tags:
      - name: Actuator
        description: Monitor and interact
        externalDocs:
          description: Spring Boot Actuator Web API Documentation
          url: https://docs.spring.io/spring-boot/docs/current/actuator-api/html/
    paths:
      /v1/certificate/{caName}:
        get:
          tags:
            - CertificationService
          summary: sign certificate
          description: Web endpoint for requesting certificate signing. Used by system
            components to gain certificate signed by CA.
          operationId: signCertificate
          parameters:
            - name: caName
              in: path
              description: Name of certification authority that will sign CSR.
              required: true
              schema:
                type: string
            - name: CSR
              in: header
              description: Certificate signing request in form of PEM object encoded in
                Base64 (with header and footer).
              required: true
              schema:
                type: string
            - name: PK
              in: header
              description: Private key in form of PEM object encoded in Base64 (with header
                and footer).
              required: true
              schema:
                type: string
          responses:
            "200":
              description: certificate successfully signed
              content:
                application/json; charset=utf-8:
                  schema:
                    $ref: '#/components/schemas/CertificationModel'
            "500":
              description: something went wrong during connecting to cmp client
              content:
                application/json; charset=utf-8:
                  schema:
                    $ref: '#/components/schemas/ErrorResponseModel'
            "404":
              description: CA not found for given name
              content:
                application/json; charset=utf-8:
                  schema:
                    $ref: '#/components/schemas/ErrorResponseModel'
            "400":
              description: given CSR or/and PK is incorrect
              content:
                application/json; charset=utf-8:
                  schema:
                    $ref: '#/components/schemas/ErrorResponseModel'
      /ready:
        get:
          tags:
            - CertificationService
          summary: check is container is ready
          description: Web endpoint for checking if service is ready to be used.
          operationId: checkReady
          responses:
            "200":
              description: configuration is loaded and service is ready to use
              content:
                application/json; charset=utf-8:
                  schema:
                    type: string
            "503":
              description: configuration loading failed and service is unavailable
              content:
                application/json; charset=utf-8:
                  schema:
                    type: string
      /reload:
        get:
          tags:
            - CertificationService
          summary: reload service configuration from file
          description: Web endpoint for performing configuration reload. Used to reload
            configuration file from file.
          operationId: reloadConfiguration
          responses:
            "200":
              description: configuration has been successfully reloaded
              content:
                application/json; charset=utf-8:
                  schema:
                    type: string
            "500":
              description: something went wrong during configuration loading
              content:
                application/json; charset=utf-8:
                  schema:
                    $ref: '#/components/schemas/ErrorResponseModel'
      /actuator/health:
        get:
          tags:
            - Actuator
          summary: Actuator web endpoint 'health'
          operationId: handle_0
          responses:
            "200":
              description: default response
              content: {}
      /actuator/health/**:
        get:
          tags:
            - Actuator
          summary: Actuator web endpoint 'health-path'
          operationId: handle_1
          responses:
            "200":
              description: default response
              content: {}
      /actuator:
        get:
          tags:
            - Actuator
          summary: Actuator root web endpoint
          operationId: links_2
          responses:
            "200":
              description: default response
              content: {}
    components:
      schemas:
        ErrorResponseModel:
          type: object
          properties:
            errorMessage:
              type: string
        CertificationModel:
          type: object
          properties:
            certificateChain:
              type: array
              items:
                type: string
            trustedCertificates:
              type: array
              items:
                type: string
