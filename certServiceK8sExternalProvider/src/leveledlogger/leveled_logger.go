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

	klogger := LeveledLogger{
		Log: zapr.NewLogger(logger),
	}
	return klogger
}

func GetLoggerWithValues(keysAndValues ...interface{}) LeveledLogger {
	klogger := GetLogger()
	klogger.Log = klogger.Log.WithValues(keysAndValues...)
	return klogger
}

func GetLoggerWithName(name string) LeveledLogger {
	klogger := GetLogger()
	klogger.Log = klogger.Log.WithName(name)
	return klogger
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
