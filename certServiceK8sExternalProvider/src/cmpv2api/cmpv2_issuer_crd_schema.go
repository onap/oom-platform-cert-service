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
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func init() {
	SchemeBuilder.Register(&CMPv2Issuer{}, &CMPv2IssuerList{})
}

// CMPv2IssuerSpec defines the desired state of CMPv2Issuer
type CMPv2IssuerSpec struct {
	// URL is the base URL for the CertService certificates instance.
	URL string `json:"url"`
	// CaName is the name of the external CA server
	CaName string `json:"caName"`
	// KeyRef is a reference to a Secret containing the provisioner
	CertSecretRef SecretKeySelector `json:"certSecretRef"`
}

// CMPv2IssuerStatus defines the observed state of CMPv2Issuer
type CMPv2IssuerStatus struct {

	// +optional
	Conditions []CMPv2IssuerCondition `json:"conditions,omitempty"`
}

type CMPv2Issuer struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   CMPv2IssuerSpec   `json:"spec,omitempty"`
	Status CMPv2IssuerStatus `json:"status,omitempty"`
}

// +kubebuilder:object:root=true

// CMPv2IssuerList contains a list of CMPv2Issuer
type CMPv2IssuerList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []CMPv2Issuer `json:"items"`
}

// SecretKeySelector contains the reference to a secret.
type SecretKeySelector struct {
	// The name of the secret in the pod's namespace to select from.
	Name string `json:"name"`

	// The key of the secret to select private key from. Must be a valid secret key.
	KeyRef string `json:"keyRef,omitempty"`
	// The key of the secret to select cert from. Must be a valid secret key.
	CertRef string `json:"certRef,omitempty"`
	// The key of the secret to select cacert from. Must be a valid secret key.
	CacertRef string `json:"cacertRef,omitempty"`
}

// ConditionType represents a CMPv2Issuer condition type.
// +kubebuilder:validation:Enum=Ready
type ConditionType string

const (
	// ConditionReady indicates that a CMPv2Issuer is ready for use.
	ConditionReady ConditionType = "Ready"
)

// ConditionStatus represents a condition's status.
// +kubebuilder:validation:Enum=True;False;Unknown
type ConditionStatus string

// These are valid condition statuses. "ConditionTrue" means a resource is in
// the condition; "ConditionFalse" means a resource is not in the condition;
// "ConditionUnknown" means kubernetes can't decide if a resource is in the
// condition or not. In the future, we could add other intermediate
// conditions, e.g. ConditionDegraded.
const (
	// ConditionTrue represents the fact that a given condition is true
	ConditionTrue ConditionStatus = "True"

	// ConditionFalse represents the fact that a given condition is false
	ConditionFalse ConditionStatus = "False"

	// ConditionUnknown represents the fact that a given condition is unknown
	ConditionUnknown ConditionStatus = "Unknown"
)

// CMPv2IssuerCondition contains condition information for the CertService issuer.
type CMPv2IssuerCondition struct {
	// Type of the condition, currently ('Ready').
	Type ConditionType `json:"type"`

	// Status of the condition, one of ('True', 'False', 'Unknown').
	// +kubebuilder:validation:Enum=True;False;Unknown
	Status ConditionStatus `json:"status"`

	// LastTransitionTime is the timestamp corresponding to the last status
	// change of this condition.
	// +optional
	LastTransitionTime *metav1.Time `json:"lastTransitionTime,omitempty"`

	// Reason is a brief machine readable explanation for the condition's last
	// transition.
	// +optional
	Reason string `json:"reason,omitempty"`

	// Message is a human readable description of the details of the last
	// transition, complementing reason.
	// +optional
	Message string `json:"message,omitempty"`
}
