/*

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package api

import (
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func init() {
	SchemeBuilder.Register(&CertServiceIssuer{}, &CertServiceIssuerList{})
}

// CertServiceIssuerSpec defines the desired state of CertServiceIssuer
type CertServiceIssuerSpec struct {
	// URL is the base URL for the CertService certificates instance.
	URL string `json:"url"`

	// KeyRef is a reference to a Secret containing the provisioner
	// password used to decrypt the provisioner private key.
	KeyRef SecretKeySelector `json:"keyRef"`
}

// CertServiceIssuerStatus defines the observed state of CertServiceIssuer
type CertServiceIssuerStatus struct {
	// INSERT ADDITIONAL STATUS FIELD - define observed state of cluster
	// Important: Run "make" to regenerate code after modifying this file

	// +optional
	Conditions []CertServiceIssuerCondition `json:"conditions,omitempty"`
}

// +kubebuilder:object:root=true

// CertServiceIssuer is the Schema for the certserviceissuers API
// +kubebuilder:subresource:status
type CertServiceIssuer struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   CertServiceIssuerSpec   `json:"spec,omitempty"`
	Status CertServiceIssuerStatus `json:"status,omitempty"`
}

// +kubebuilder:object:root=true

// CertServiceIssuerList contains a list of CertServiceIssuer
type CertServiceIssuerList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []CertServiceIssuer `json:"items"`
}

// SecretKeySelector contains the reference to a secret.
type SecretKeySelector struct {
	// The name of the secret in the pod's namespace to select from.
	Name string `json:"name"`

	// The key of the secret to select from. Must be a valid secret key.
	// +optional
	Key string `json:"key,omitempty"`
}

// ConditionType represents a CertServiceIssuer condition type.
// +kubebuilder:validation:Enum=Ready
type ConditionType string

const (
	// ConditionReady indicates that a CertServiceIssuer is ready for use.
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

// CertServiceIssuerCondition contains condition information for the CertService issuer.
type CertServiceIssuerCondition struct {
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
