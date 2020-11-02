# ============LICENSE_START=======================================================
# oom-certservice-k8s-external-provider
# ================================================================================
# Copyright (c) 2019 Smallstep Labs, Inc.
# Modifications copyright (C) 2020 Nokia. All rights reserved.
# ================================================================================
# This source code was copied from the following git repository:
# https://github.com/smallstep/step-issuer
# The source code was modified for usage in the ONAP project.
# ================================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ============LICENSE_END=========================================================
#

# Build the manager binary
FROM golang:1.15.2 as builder

WORKDIR /workspace
# Copy the Go Modules manifests
COPY go.* ./

# cache deps before building and copying source so that we don't need to re-download as much
# and so that source changes don't invalidate our downloaded layer
RUN go mod download

# Copy the go source
COPY main.go main.go
COPY src/ ./src

# Build
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 GO111MODULE=on go build -a -o oom-certservice-cmpv2issuer main.go


# Runtime container definition
FROM gcr.io/distroless/static:latest
WORKDIR /
COPY --from=builder /workspace/oom-certservice-cmpv2issuer .
ENTRYPOINT ["/oom-certservice-cmpv2issuer"]
