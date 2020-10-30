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

package leveledlogger

import (
	"encoding/json"
	"io/ioutil"
	"log"

	"github.com/go-logr/logr"
	"go.uber.org/zap/zapcore"
	"github.com/go-logr/zapr"
	"go.uber.org/zap"
)

const (
	WARNING = int(zapcore.WarnLevel) * -1
	INFO    = int(zapcore.InfoLevel) * -1
	DEBUG   = int(zapcore.DebugLevel) * -1
)

type LeveledLogger struct {
	Log logr.Logger
	ConfigFile string
}

var configFileName = "default"

func SetConfigFileName(newName string) {
	configFileName = newName
}

func GetLogger() LeveledLogger {
	var cfg zap.Config

	if err := json.Unmarshal(getConfig(), &cfg); err != nil {
		panic(err)
	}
	logger, err := cfg.Build()
	if err != nil {
		panic(err)
	}

	leveledLogger := LeveledLogger{
		Log: zapr.NewLogger(logger),
	}
	return leveledLogger
}

func GetLoggerWithValues(keysAndValues ...interface{}) LeveledLogger {
	leveledLogger := GetLogger()
	leveledLogger.Log = leveledLogger.Log.WithValues(keysAndValues...)
	return leveledLogger
}

func GetLoggerWithName(name string) LeveledLogger {
	leveledLogger := GetLogger()
	leveledLogger.Log = leveledLogger.Log.WithName(name)
	return leveledLogger
}

func (logger *LeveledLogger) Error(err error, message string, keysAndValues ...interface{}) {
	logger.Log.Error(err, message, keysAndValues...)
}

func (logger *LeveledLogger) Warning(message string, keysAndValues ...interface{}) {
	logger.log(message, WARNING, keysAndValues...)
}

func (logger *LeveledLogger) Info(message string, keysAndValues ...interface{}) {
	logger.log(message, INFO, keysAndValues...)
}

func (logger *LeveledLogger) Debug(message string, keysAndValues ...interface{}) {
	logger.log(message, DEBUG, keysAndValues...)
}

func (logger *LeveledLogger) log(message string, lvl int, keysAndValues ...interface{}) {
	logger.Log.V(lvl).Info(message, keysAndValues...)
}

func getDefaultConfig() []byte {
	return []byte(`{
  		"level": "error",
  		"encoding": "console",
  		"outputPaths": ["stdout"],
  		"encoderConfig": {
    		"timeKey": "timeKey",
    		"messageKey": "message",
    		"levelKey": "level",
    		"nameKey": "name",
    		"levelEncoder": "capital",
    		"timeEncoder": "iso8601"
  		}
		}`)
}

func getConfig() []byte {
	var config = []byte{}
	if configFileName == "default" {
		config = getDefaultConfig()
	} else {
		config = readFile(configFileName)
	}
	return config
}

func readFile(filename string) []byte {
	certRequest, err := ioutil.ReadFile(filename)
	if err != nil {
		log.Fatal(err)
	}
	return certRequest
}
