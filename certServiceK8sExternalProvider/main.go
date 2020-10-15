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

package main

import (
	"flag"
	"fmt"
	certmanager "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"k8s.io/apimachinery/pkg/runtime"
	clientgoscheme "k8s.io/client-go/kubernetes/scheme"
	_ "k8s.io/client-go/plugin/pkg/client/auth/gcp"
	"k8s.io/utils/clock"
	app "onap.org/oom-certservice/k8s-external-provider/src"
	certserviceapi "onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
	controllers "onap.org/oom-certservice/k8s-external-provider/src/cmpv2controller"
	"os"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/log/zap"
)

var (
	scheme   = runtime.NewScheme()
	setupLog = ctrl.Log.WithName("setup")
)

func init() {
	_ = clientgoscheme.AddToScheme(scheme)
	_ = certmanager.AddToScheme(scheme)
	_ = certserviceapi.AddToScheme(scheme)
}

func main() {
	fmt.Println()
	fmt.Println("                                        ***  Cert Service Provider v1.0.2  ***")
	fmt.Println()

	setupLog.Info("Parsing arguments...")
	var metricsAddr string
	var enableLeaderElection bool
	flag.StringVar(&metricsAddr, "metrics-addr", ":8080", "The address the metric endpoint binds to.")
	flag.BoolVar(&enableLeaderElection, "enable-leader-election", false,
		"Enable leader election for controller manager. Enabling this will ensure there is only one active controller manager.")
	flag.Parse()

	ctrl.SetLogger(zap.New(zap.UseDevMode(true)))

	setupLog.Info("Creating k8s Manager...")
	manager, err := ctrl.NewManager(ctrl.GetConfigOrDie(), ctrl.Options{
		Scheme:             scheme,
		MetricsBindAddress: metricsAddr,
		LeaderElection:     enableLeaderElection,
	})
	if err != nil {
		setupLog.Error(err, app.FAILED_TO_CREATE_CONTROLLER_MANAGER.Message)
		os.Exit(app.FAILED_TO_CREATE_CONTROLLER_MANAGER.Code)
	}

	setupLog.Info("Registering CMPv2IssuerController...")
	if err = (&controllers.CMPv2IssuerController{
		Client:   manager.GetClient(),
		Log:      ctrl.Log.WithName("controllers").WithName("CMPv2Issuer"),
		Clock:    clock.RealClock{},
		Recorder: manager.GetEventRecorderFor("cmpv2-issuer-controller"),
	}).SetupWithManager(manager); err != nil {
		setupLog.Error(err, app.FAILED_TO_REGISTER_CMPv2_ISSUER_CONTROLLER.Message)
		os.Exit(app.FAILED_TO_REGISTER_CMPv2_ISSUER_CONTROLLER.Code)
	}

	setupLog.Info("Registering CertificateRequestController...")
	if err = (&controllers.CertificateRequestController{
		Client:   manager.GetClient(),
		Log:      ctrl.Log.WithName("controllers").WithName("CertificateRequest"),
		Recorder: manager.GetEventRecorderFor("certificate-requests-controller"),
	}).SetupWithManager(manager); err != nil {
		setupLog.Error(err, app.FAILED_TO_REGISTER_CMPv2_ISSUER_CONTROLLER.Message)
		os.Exit(app.FAILED_TO_REGISTER_CERT_REQUEST_CONTROLLER.Code)
	}

	setupLog.Info("Starting k8s manager...")
	if err := manager.Start(ctrl.SetupSignalHandler()); err != nil {
		setupLog.Error(err, app.EXCEPTION_WHILE_RUNNING_CONTROLLER_MANAGER.Message)
		os.Exit(app.EXCEPTION_WHILE_RUNNING_CONTROLLER_MANAGER.Code)
	}
	setupLog.Info("Application is up and running.")

}
