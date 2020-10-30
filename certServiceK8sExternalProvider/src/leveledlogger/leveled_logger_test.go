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

package leveledlogger

import (
	"bytes"
	"fmt"
	"log"
	"os"
	"testing"
)

func TestLoggerOnWarningLevel(t *testing.T) {
	const resultLogName = "testdata/test_result_warn.log"
	const expectedLogName = "testdata/test_expected_warn.log"

	SetConfigFileName("testdata/test_logger_config_warn.json")
	logger := GetLoggerWithName("loggername")

	logOnAllLevels(logger)

	resultLogBytes := readFile(resultLogName)
	expectedLogBytes := readFile(expectedLogName)

	assertLogEquals(t, resultLogBytes, expectedLogBytes, resultLogName)
}

func TestLoggerOnDebugLevel(t *testing.T) {
	const resultLogName = "testdata/test_result_debug.log"
	const expectedLogName = "testdata/test_expected_debug.log"

	SetConfigFileName("testdata/test_logger_config_debug.json")
	logger := GetLoggerWithName("loggername")

    logOnAllLevels(logger)

	resultLogBytes := readFile(resultLogName)
	expectedLogBytes := readFile(expectedLogName)

	assertLogEquals(t, resultLogBytes, expectedLogBytes, resultLogName)
}

func logOnAllLevels(logger LeveledLogger) {
	logger.Debug("this is a debug message")
	logger.Info("this is an info message")
	logger.Warning("this is a warning message", "key1", "value1")
	logger.Error(fmt.Errorf("this is an error message"), "err msg")
}

func assertLogEquals(t *testing.T, resultLogBytes []byte, expectedLogBytes []byte, resultLogName string) {
	if areEqual(resultLogBytes, expectedLogBytes) {
		removeTemporaryFile(resultLogName)
	} else {
		t.Fatal("Logs are different than expected. Please check: " + resultLogName)
	}
}

func areEqual(slice1 []byte, slice2 []byte) bool {
	return bytes.Compare(slice1, slice2) == 0
}

func removeTemporaryFile(fileName string) {
	if _, err := os.Stat(fileName); err == nil {
		e := os.Remove(fileName)
		if e != nil {
			log.Fatal(e)
		}
	}
}
