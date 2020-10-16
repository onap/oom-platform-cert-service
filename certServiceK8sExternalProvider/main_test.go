/*
 * ============LICENSE_START=======================================================
 * oom-certservice-k8s-external-provider
 * ================================================================================
 * Copyright (C) 2020 Nokia. All rights reserved.
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
	"github.com/stretchr/testify/assert"
	"os"
	"testing"
)

func Test_shouldParseArguments_defaultValues(t *testing.T) {
	os.Args = []string{
		"first-arg-is-omitted-by-method-parse-arguments-so-this-only-a-placeholder"}
	flag.CommandLine = flag.NewFlagSet(os.Args[0], flag.ExitOnError)

	metricsAddr, enableLeaderElection := parseInputArguments()

	assert.Equal(t, ":8080", metricsAddr)
	assert.False(t, enableLeaderElection)
}

func Test_shouldParseArguments_valuesFromCLI(t *testing.T) {
	os.Args = []string{
		"first-arg-is-omitted-by-method-parse-arguments-so-this-only-a-placeholder",
		"--metrics-addr=127.0.0.1:555",
		"--enable-leader-election=true"}
	flag.CommandLine = flag.NewFlagSet(os.Args[0], flag.ExitOnError)

	metricsAddr, enableLeaderElection := parseInputArguments()

	assert.Equal(t, "127.0.0.1:555", metricsAddr)
	assert.True(t, enableLeaderElection)

}
