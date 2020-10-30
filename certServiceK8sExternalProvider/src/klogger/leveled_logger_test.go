package klogger

import (
	"bytes"
	"fmt"
	"github.com/stretchr/testify/assert"
	"k8s.io/klog/v2"
	"strings"
	"testing"
)

func TestKlogger(t *testing.T) {
	llog := CreateLeveledLogger()

	llog2 := GetLoggerWithValues("key1", "val1")

	tmpWriteBuffer := getLogBuffer()

	llog.Log.WithValues("certificate-request-controller", "ererer")

	llog.Debug("this is a debug message", "ee")
	llog.Info("this is an info message", "oo")
	llog.Warning("this is an info message")
	llog.Warning("this is an info message", "oo", "ee")
	llog.Warning("this is an info message", "oo", "ee", "aa", "yy")

	llog2.Info("log 2 message")

	llog.Info("this is an second info message")
	llog.Error(fmt.Errorf("math: square root of negative number"), "this is an second info message")

	logsArray := convertBufferToStringArray(tmpWriteBuffer)

	expectedLog := "this is an info messag"
	assert.True(t, strings.Contains(logsArray[0], expectedLog), "Should contain: "+expectedLog+". Result log: "+logsArray[0])

	//t.Fail()
}

func convertBufferToStringArray(buffer *bytes.Buffer) []string {
	return strings.Split(buffer.String(), "\n")
}

func getLogBuffer() *bytes.Buffer {
	tmpWriteBuffer := bytes.NewBuffer(nil)
	klog.SetOutput(tmpWriteBuffer)
	return tmpWriteBuffer
}
