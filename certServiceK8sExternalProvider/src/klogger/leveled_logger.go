package klogger

import (
	"flag"
	"fmt"
	"time"

	"github.com/go-logr/logr"
	"k8s.io/klog/v2"
	"k8s.io/klog/v2/klogr"
	"sync"
)

const (
	OFF     = 0
	ERROR   = 1
	WARNING = 2
	INFO    = 3
	DEBUG   = 4
)

type LeveledLogger struct {
	Log logr.Logger
}

const level = INFO

var doOnce sync.Once

func CreateLeveledLogger() LeveledLogger {
	doOnce.Do(func() {
		klog.InitFlags(nil)
		flag.CommandLine.Set("v", fmt.Sprint(level))
		flag.CommandLine.Set("alsologtostderr", "true")
		flag.CommandLine.Set("skip_headers", "true")
		flag.CommandLine.Set("logtostderr", "false")
		flag.Parse()
	})
	klogger := LeveledLogger{
		Log: klogr.New(),
	}
	return klogger
}

func GetLoggerWithValues(keysAndValues ...interface{}) LeveledLogger {
	klogger := LeveledLogger{
		Log: klogr.New().WithValues(keysAndValues...),
	}
	return klogger
}

func GetLoggerWithName(name string) LeveledLogger {
	klogger := LeveledLogger{
		Log: klogr.New().WithName(name),
	}
	return klogger
}

func (logger *LeveledLogger) Error(err error, message string, keysAndValues ...interface{}) {

	message = getTimestamp() + " ERROR " + message
	vals := append([]interface{}{"cause", err}, keysAndValues...)
	logger.Log.V(ERROR).Info(message, vals...)
}

func (logger *LeveledLogger) Warning(message string, keysAndValues ...interface{}) {
	message = getTimestamp() + " WARNING " + message
	logger.Log.V(WARNING).Info(message, keysAndValues...)
}

func (logger *LeveledLogger) Info(message string, keysAndValues ...interface{}) {
	message = getTimestamp() + " INFO " + message
	logger.Log.V(INFO).Info(message, keysAndValues...)
}

func (logger *LeveledLogger) Debug(message string, keysAndValues ...interface{}) {
	message = getTimestamp() + " DEBUG " + message
	logger.Log.V(DEBUG).Info(message, keysAndValues...)
}

func getTimestamp() string {
	return time.Now().Format("2006-01-02 15:04:05")
}
