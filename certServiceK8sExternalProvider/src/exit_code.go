package app

type ExitCode struct {
	Code    int
	Message string
}

var (
	FAILED_TO_CREATE_CONTROLLER_MANAGER        = ExitCode{1, "Unable to create k8s controller manager"}
	FAILED_TO_REGISTER_CMPv2_ISSUER_CONTROLLER = ExitCode{2, "Unable to register CMPv2Issuer controller"}
	FAILED_TO_REGISTER_CERT_REQUEST_CONTROLLER = ExitCode{3, "Unable to register CertificateRequestController"}
	EXCEPTION_WHILE_RUNNING_CONTROLLER_MANAGER = ExitCode{4, "An exception occurs while running k8s controller manager"}
)
