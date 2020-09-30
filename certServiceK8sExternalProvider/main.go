package main

import (
	"fmt"
	"github.com/levigross/grequests"
	"log"
)

func main() {
	fmt.Println("   ***  Hello World Golang App  ***")
	fmt.Println()

	resp, err := grequests.Get("http://jenkins.onap.org/", nil)

	if err != nil {
		log.Fatalln("Unable to make request: ", err)
	}

	fmt.Println("Checking if jenkins.onap.org is reachable:", resp.StatusCode)
}
