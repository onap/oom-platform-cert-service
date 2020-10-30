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
	"os"

	certmanager "github.com/jetstack/cert-manager/pkg/apis/certmanager/v1"
	"k8s.io/apimachinery/pkg/runtime"
	clientgoscheme "k8s.io/client-go/kubernetes/scheme"
	_ "k8s.io/client-go/plugin/pkg/client/auth/gcp"
	"k8s.io/utils/clock"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/manager"

	app "onap.org/oom-certservice/k8s-external-provider/src"
	certserviceapi "onap.org/oom-certservice/k8s-external-provider/src/cmpv2api"
	controllers "onap.org/oom-certservice/k8s-external-provider/src/cmpv2controller"
	"onap.org/oom-certservice/k8s-external-provider/src/leveledlogger"
)

var (
	scheme   = runtime.NewScheme()
	setupLog leveledlogger.LeveledLogger
)

func init() {
	_ = clientgoscheme.AddToScheme(scheme)
	_ = certmanager.AddToScheme(scheme)
	_ = certserviceapi.AddToScheme(scheme)
	setupLog = leveledlogger.GetLogger()

	ctrl.SetLogger(setupLog.Log)
}

func main() {
	printVersionInfo()

	metricsAddr, enableLeaderElection := parseInputArguments()

	manager := createControllerManager(metricsAddr, enableLeaderElection)

	registerCMPv2IssuerController(manager)
	registerCertificateRequestController(manager)

	startControllerManager(manager)

	setupLog.Info("Application is up and running.")
}

func printVersionInfo() {
	fmt.Println()
	fmt.Println("                                     ***   CMPv2 Provider v1.0.0   ***")
	fmt.Println()
}

func parseInputArguments() (string, bool) {
	setupLog.Info("Parsing input arguments...")
	var metricsAddr string
	var enableLeaderElection bool
	flag.StringVar(&metricsAddr, "metrics-addr", ":8080", "The address the metric endpoint binds to.")
	flag.BoolVar(&enableLeaderElection, "enable-leader-election", false,
		"Enable leader election for controller manager. Enabling this will ensure there is only one active controller manager.")
	flag.Parse()
	return metricsAddr, enableLeaderElection
}

func startControllerManager(manager manager.Manager) {
	setupLog.Info("Starting CMPv2 controller manager...")
	if err := manager.Start(ctrl.SetupSignalHandler()); err != nil {
		exit(app.EXCEPTION_WHILE_RUNNING_CONTROLLER_MANAGER, err)
	}
}

func createControllerManager(metricsAddr string, enableLeaderElection bool) manager.Manager {
	setupLog.Info("Creating CMPv2 controller manager...")
	manager, err := ctrl.NewManager(ctrl.GetConfigOrDie(), ctrl.Options{
		Scheme:             scheme,
		MetricsBindAddress: metricsAddr,
		LeaderElection:     enableLeaderElection,
	})
	if err != nil {
		exit(app.FAILED_TO_CREATE_CONTROLLER_MANAGER, err)
	}
	return manager
}

func registerCMPv2IssuerController(manager manager.Manager) {
	setupLog.Info("Registering CMPv2IssuerController...")

	err := (&controllers.CMPv2IssuerController{
		Client:   manager.GetClient(),
		Log:      leveledlogger.GetLoggerWithValues("controllers", "CMPv2Issuer"),
		Clock:    clock.RealClock{},
		Recorder: manager.GetEventRecorderFor("cmpv2-issuer-controller"),
	}).SetupWithManager(manager)

	if err != nil {
		exit(app.FAILED_TO_REGISTER_CMPv2_ISSUER_CONTROLLER, err)
	}
}

func registerCertificateRequestController(manager manager.Manager) {
	setupLog.Info("Registering CertificateRequestController...")

	err := (&controllers.CertificateRequestController{
		Client:   manager.GetClient(),
		Log:      leveledlogger.GetLoggerWithValues("controllers", "CertificateRequest"),
		Recorder: manager.GetEventRecorderFor("certificate-requests-controller"),
	}).SetupWithManager(manager)

	if err != nil {
		exit(app.FAILED_TO_REGISTER_CERT_REQUEST_CONTROLLER, err)
	}
}

func exit(exitCode app.ExitCode, err error) {
	setupLog.Error(err, exitCode.Message)
	os.Exit(exitCode.Code)
}
