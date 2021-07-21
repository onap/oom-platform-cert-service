/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
 * ================================================================================
 * Copyright (c) 2019 Smallstep Labs, Inc.
 * Modifications copyright (C) 2020 Nokia. All rights reserved.
 * ================================================================================
 * This source code was copied from the following git repository:
 * https://github.com/smallstep/step-issuer
 * The source code was modified for usage in the ONAP project.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package cmpv2api

import (
	"k8s.io/apimachinery/pkg/runtime/schema"
	"sigs.k8s.io/controller-runtime/pkg/scheme"
)

var (
	// GroupVersion is group version used to register these objects
	GroupVersion = schema.GroupVersion{Group: "certmanager.onap.org", Version: "v1"}
	//GroupVersion2 = schema.GroupVersion{Group: "certmanager.onap.org", Version: "v2"}
	// SchemeBuilder is used to add go types to the GroupVersionKind scheme
	//SchemeBuilder = &scheme.Builder{GroupVersion: GroupVersion}
	SchemeBuilder = &scheme.Builder{GroupVersion: GroupVersion}
	//SchemeBuilderV2 = &scheme.Builder{GroupVersion: GroupVersion2}

	// AddToSchemeV1 adds the types in this group-version to the given scheme.
	AddToSchemeV1 = SchemeBuilder.AddToScheme
	//AddToSchemeV2 = SchemeBuilderV2.AddToScheme
)
var (
	// GroupVersion is group version used to register these objects
	//GroupVersion  = schema.GroupVersion{Group: "certmanager.onap.org", Version: "v1"}
	GroupVersion2 = schema.GroupVersion{Group: "certmanager.onap.org", Version: "v2"}
	// SchemeBuilder is used to add go types to the GroupVersionKind scheme
	//SchemeBuilder = &scheme.Builder{GroupVersion: GroupVersion}
	//SchemeBuilder   = &scheme.Builder{GroupVersion: GroupVersion}
	SchemeBuilderV2 = &scheme.Builder{GroupVersion: GroupVersion2}

	// AddToSchemeV1 adds the types in this group-version to the given scheme.
	//AddToSchemeV1 = SchemeBuilder.AddToScheme
	AddToSchemeV2 = SchemeBuilderV2.AddToScheme
)

const CMPv2IssuerKind = "CMPv2Issuer"
