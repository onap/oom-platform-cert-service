package leveledlogger

import (
	"bytes"
	"fmt"
	"log"
	"os"
	"testing"
)

func TestKlogger(t *testing.T) {
	const resultLogName = "testdata/test_result.log"
	const expectedLogName = "testdata/test_expected.log"

	SetConfigFileName("testdata/test_logger_config.json")
	logger := GetLoggerWithName("loggername")

	logger.Debug("this is a debug message")
	logger.Info("this is an info message")
	logger.Warning("this is a warning message", "key1", "value1")
	logger.Error(fmt.Errorf("this is an error message"), "err msg")

	resultLogBytes := readFile(resultLogName)
	expectedLogBytes := readFile(expectedLogName)

	if areEqual(resultLogBytes, expectedLogBytes) {
		removeFile(resultLogName)
	} else {
		t.Fatal("Logs are different than expected. Please check: " + resultLogName)
	}
}

func areEqual(slice1 []byte, slice2 []byte) bool {
	return bytes.Compare(slice1, slice2) == 0
}

func removeFile(fileName string) {
	if _, err := os.Stat(fileName); err == nil {
		e := os.Remove(fileName)
		if e != nil {
			log.Fatal(e)
		}
	}
}
