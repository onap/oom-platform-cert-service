// ============LICENSE_START=======================================================
// oom-certservice-k8s-external-provider
// ================================================================================
// Copyright (c) 2019 Smallstep Labs, Inc.
// Modifications Copyright (C) 2020 Nokia. All rights reserved.
// ================================================================================
// This source code was copied from the following git repository:
// https://github.com/smallstep/step-issuer
// The source code was modified for usage in the ONAP project.
// ================================================================================
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============LICENSE_END=========================================================
//

module onap.org/oom-certservice/k8s-external-provider

go 1.15

require (
	github.com/go-logr/logr v0.2.1
	github.com/go-logr/zapr v0.2.0
	github.com/jetstack/cert-manager v1.0.3
	github.com/stretchr/testify v1.6.1
	go.uber.org/zap v1.10.0
	gonum.org/v1/netlib v0.0.0-20190331212654-76723241ea4e // indirect
	k8s.io/api v0.19.0
	k8s.io/apimachinery v0.19.0
	k8s.io/client-go v0.19.0
	k8s.io/klog/v2 v2.3.0
	k8s.io/utils v0.0.0-20200729134348-d5654de09c73
	sigs.k8s.io/controller-runtime v0.6.2
)
