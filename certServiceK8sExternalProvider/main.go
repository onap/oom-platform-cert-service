package main

import (
	"fmt"
	"github.com/levigross/grequests"
	"log"
)

func main() {

	fmt.Println("   ***  Hello World Golang App  ***")
	fmt.Println()

	resp, err := grequests.Get("http://onap-wro-dashboard.dyn.nesc.nokia.net/live", nil)
	// You can modify the request by passing an optional RequestOptions struct

	if err != nil {
		log.Fatalln("Unable to make request: ", err)
	}

	fmt.Println("Checking status of the app Dashboard-NG:")
	fmt.Println(resp.String())

}
