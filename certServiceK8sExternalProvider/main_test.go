package main

import (
	"os"
	"testing"
	"github.com/stretchr/testify/assert"
	"flag"
)

func TestParseArguments(t *testing.T) {

	// check default values
	metricsAddr, enableLeaderElection := parseInputArguments()

	assert.Equal(t, ":8080", metricsAddr)
	assert.False(t, enableLeaderElection)

	// check values provides from "command line"
	os.Args = []string {
		"first-arg-is-omitted-by-method-parse-arguments-so-this-only-a-placeholder",
		"--metrics-addr=127.0.0.1:555",
		"--enable-leader-election=true" }
	flag.CommandLine = flag.NewFlagSet(os.Args[0], flag.ExitOnError)

	metricsAddr, enableLeaderElection = parseInputArguments()

	assert.Equal(t, "127.0.0.1:555", metricsAddr)
	assert.True(t, enableLeaderElection)

}
