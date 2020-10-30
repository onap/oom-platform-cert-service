package klogger

import (
	"bytes"
	"github.com/stretchr/testify/assert"
	"k8s.io/klog/v2"
	"strings"
	"testing"
)

func TestKlogger(t *testing.T) {
	llog := CreateLeveledLogger(INFO)
	tmpWriteBuffer := getLogBuffer()

	llog.Debug("this is a debug message")
	llog.Info("this is an info message")

	logsArray := convertBufferToStringArray(tmpWriteBuffer)

	expectedLog := "\"msg\"=\"this is an info message\"  \"level\"=\"INFO\""
	assert.True(t, strings.Contains(logsArray[0], expectedLog), "Should contain: "+expectedLog+". Result log: "+logsArray[0])
}

func convertBufferToStringArray(buffer *bytes.Buffer) []string {
	return strings.Split(buffer.String(), "\n")
}

func getLogBuffer() *bytes.Buffer {
	tmpWriteBuffer := bytes.NewBuffer(nil)
	klog.SetOutput(tmpWriteBuffer)
	return tmpWriteBuffer
}
