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

var klogger *LeveledLogger

var doOnce sync.Once

func CreateLeveledLogger(level int) *LeveledLogger {
	doOnce.Do(func() {
		klogger = &LeveledLogger{
			Log: klogr.New(),
		}
		klog.InitFlags(nil)
		flag.CommandLine.Set("v", fmt.Sprint(level))
		flag.CommandLine.Set("alsologtostderr", "true")
		flag.CommandLine.Set("skip_headers", "true")
		flag.CommandLine.Set("logtostderr", "false")
		flag.Parse()
	})
	return klogger
}

func getTimestamp() string {
	return time.Now().Format("2006-01-02 15:04:05")
}

func (logger *LeveledLogger) Error(err error, message string) {

	logger.Log.V(ERROR).Info(message, "level", "ERROR", "time", getTimestamp(), "cause", err)
}

func (logger *LeveledLogger) Warning(message string) {
	logger.Log.V(WARNING).Info(message, "level", "WARNING", "time", getTimestamp())
}

func (logger *LeveledLogger) Info(message string) {
	logger.Log.V(INFO).Info(message, "level", "INFO", "time", getTimestamp())
}

func (logger *LeveledLogger) Debug(message string) {
	logger.Log.V(DEBUG).Info(message, "level", "DEBUG", "time", getTimestamp())
}
