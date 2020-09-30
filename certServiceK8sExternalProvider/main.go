package main

import (
	"fmt"
	"github.com/levigross/grequests"
	"log"
	certservice_provisioner "onap.org/oom-certservice/k8s-external-provider/src/certservice-provisioner"
)

func main() {
	fmt.Println("   ***  Hello World Golang App  ***")
	fmt.Println()

	resp, err := grequests.Get("http://jenkins.onap.org/", nil)

	if err != nil {
		log.Fatalln("Unable to make request: ", err)
	}

	fmt.Println("Checking if jenkins.onap.org is reachable:", resp.StatusCode)

	fmt.Println("Signing certificate...")
	certservice_provisioner.SignCertificate()
}
